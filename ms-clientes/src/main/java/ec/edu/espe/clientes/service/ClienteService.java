package ec.edu.espe.clientes.service;

import ec.edu.espe.clientes.dtos.ClienteDTO;
import ec.edu.espe.clientes.dtos.CuentaCorporativaDTO;
import ec.edu.espe.clientes.exception.BusinessException;
import ec.edu.espe.clientes.exception.ResourceNotFoundException;
import ec.edu.espe.clientes.models.Cliente;
import ec.edu.espe.clientes.models.CuentaCorporativa;
import ec.edu.espe.clientes.models.enums.TipoCliente;
import ec.edu.espe.clientes.repositories.ClienteRepository;
import ec.edu.espe.clientes.repositories.CuentaCorporativaRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ClienteService {

    @Autowired
    private ClienteRepository clienteRepository;

    @Autowired
    private CuentaCorporativaRepository cuentaRepository;

    // --- CLIENTE CRUD ---

    public List<Cliente> listarClientes() {
        return clienteRepository.findAll();
    }

    public Cliente buscarClientePorId(UUID id) {
        return clienteRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con id: " + id));
    }

    public Cliente buscarClientePorIdentificacion(String identificacion) {
        return clienteRepository.findByIdentificacion(identificacion)
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado con identificación: " + identificacion));
    }

    @Transactional
    public Cliente crearCliente(ClienteDTO dto) {
        Optional<Cliente> existing = clienteRepository.findByIdentificacion(dto.getIdentificacion());
        if (existing.isPresent()) {
            throw new BusinessException("La identificación '" + dto.getIdentificacion() + "' ya está registrada");
        }

        Cliente cliente = Cliente.builder()
                .nombre(dto.getNombre())
                .email(dto.getEmail())
                .telefono(dto.getTelefono())
                .tipo(dto.getTipo())
                .identificacion(dto.getIdentificacion())
                .build();

        return clienteRepository.save(cliente);
    }

    @Transactional
    public Cliente actualizarCliente(UUID id, ClienteDTO dto) {
        Cliente cliente = buscarClientePorId(id);

        Optional<Cliente> existing = clienteRepository.findByIdentificacion(dto.getIdentificacion());
        if (existing.isPresent() && !existing.get().getId().equals(id)) {
            throw new BusinessException("La identificación '" + dto.getIdentificacion() + "' ya pertenece a otro cliente");
        }

        cliente.setNombre(dto.getNombre());
        cliente.setEmail(dto.getEmail());
        cliente.setTelefono(dto.getTelefono());
        cliente.setTipo(dto.getTipo());
        cliente.setIdentificacion(dto.getIdentificacion());

        return clienteRepository.save(cliente);
    }

    @Transactional
    public void eliminarCliente(UUID id) {
        Cliente cliente = buscarClientePorId(id);

        // Eliminar cuenta asociada si existe
        cuentaRepository.findByClienteId(id).ifPresent(cuenta -> cuentaRepository.delete(cuenta));

        clienteRepository.delete(cliente);
    }

    // --- CUENTAS CORPORATIVAS ---

    public List<CuentaCorporativa> listarCuentas() {
        return cuentaRepository.findAll();
    }

    public CuentaCorporativa buscarCuentaPorId(UUID id) {
        return cuentaRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Cuenta corporativa no encontrada con id: " + id));
    }

    public CuentaCorporativa obtenerCuentaPorClienteId(UUID clienteId) {
        return cuentaRepository.findByClienteId(clienteId)
                .orElseThrow(() -> new ResourceNotFoundException("No se encontró cuenta corporativa para el cliente: " + clienteId));
    }

    @Transactional
    public CuentaCorporativa crearCuenta(CuentaCorporativaDTO dto) {
        Cliente cliente = buscarClientePorId(dto.getClienteId());

        if (!TipoCliente.CORPORATIVO.equals(cliente.getTipo())) {
            throw new BusinessException("El cliente no es de tipo CORPORATIVO");
        }

        Optional<CuentaCorporativa> existing = cuentaRepository.findByClienteId(dto.getClienteId());
        if (existing.isPresent()) {
            throw new BusinessException("El cliente ya posee una cuenta corporativa registrada");
        }

        CuentaCorporativa cuenta = CuentaCorporativa.builder()
                .clienteId(dto.getClienteId())
                .saldo(dto.getSaldo())
                .limiteCredito(dto.getLimiteCredito())
                .build();

        return cuentaRepository.save(cuenta);
    }

    @Transactional
    public CuentaCorporativa actualizarCuenta(UUID id, CuentaCorporativaDTO dto) {
        CuentaCorporativa cuenta = buscarCuentaPorId(id);

        if (!cuenta.getClienteId().equals(dto.getClienteId())) {
            throw new BusinessException("No se puede cambiar el cliente asignado a una cuenta existente");
        }

        cuenta.setSaldo(dto.getSaldo());
        cuenta.setLimiteCredito(dto.getLimiteCredito());

        return cuentaRepository.save(cuenta);
    }

    @Transactional
    public void eliminarCuenta(UUID id) {
        CuentaCorporativa cuenta = buscarCuentaPorId(id);
        cuentaRepository.delete(cuenta);
    }

    @Transactional
    public CuentaCorporativa deducirSaldo(UUID clienteId, BigDecimal monto) {
        CuentaCorporativa cuenta = obtenerCuentaPorClienteId(clienteId);

        BigDecimal balanceDisponible = cuenta.getSaldo().add(cuenta.getLimiteCredito());
        if (balanceDisponible.compareTo(monto) < 0) {
            throw new BusinessException("Saldo insuficiente (saldo + límite de crédito: " + balanceDisponible + ", requerido: " + monto + ")");
        }

        cuenta.setSaldo(cuenta.getSaldo().subtract(monto));
        return cuentaRepository.save(cuenta);
    }

    @Transactional
    public CuentaCorporativa recargarSaldo(UUID clienteId, BigDecimal monto) {
        if (monto.compareTo(BigDecimal.ZERO) <= 0) {
            throw new BusinessException("El monto a recargar debe ser mayor a cero");
        }

        CuentaCorporativa cuenta = obtenerCuentaPorClienteId(clienteId);
        cuenta.setSaldo(cuenta.getSaldo().add(monto));
        return cuentaRepository.save(cuenta);
    }
}
