package ec.edu.espe.msflotarest.service.impl;

import ec.edu.espe.msflotarest.dtos.VehiculoDTO;
import ec.edu.espe.msflotarest.exception.BusinessException;
import ec.edu.espe.msflotarest.exception.ResourceNotFoundException;
import ec.edu.espe.msflotarest.models.Vehiculo;
import ec.edu.espe.msflotarest.models.enums.EstadoVehiculo;
import ec.edu.espe.msflotarest.repositories.VehiculoRepository;
import ec.edu.espe.msflotarest.service.IVehiculoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class VehiculoServiceImpl implements IVehiculoService {

    @Autowired
    private VehiculoRepository repository;

    @Override
    @Transactional(readOnly = true)
    public List<Vehiculo> listarTodos() {
        return repository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Vehiculo buscarPorId(java.util.UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Vehículo no encontrado con ID: " + id));
    }

    @Override
    @Transactional(readOnly = true)
    public Vehiculo buscarPorMatricula(String matricula) {
        return repository.findByMatricula(matricula)
                .orElseThrow(() -> new ResourceNotFoundException("Vehículo no encontrado con matrícula: " + matricula));
    }

    @Override
    @Transactional
    public Vehiculo crear(VehiculoDTO dto) {
        if (repository.existsByMatricula(dto.getMatricula())) {
            throw new BusinessException("La matrícula " + dto.getMatricula() + " ya está registrada en la flota");
        }

        Vehiculo vehiculo = new Vehiculo();
        mapearDtoAEntidad(dto, vehiculo);
        return repository.save(vehiculo);
    }

    @Override
    @Transactional
    public Vehiculo actualizar(java.util.UUID id, VehiculoDTO dto) {
        Vehiculo vehiculoExistente = buscarPorId(id);

        // Si la matrícula cambia, validar que la nueva no esté en uso
        if (!vehiculoExistente.getMatricula().equals(dto.getMatricula()) &&
                repository.existsByMatricula(dto.getMatricula())) {
            throw new BusinessException("La nueva matrícula ya pertenece a otro vehículo de la flota");
        }

        mapearDtoAEntidad(dto, vehiculoExistente);
        return repository.save(vehiculoExistente);
    }

    @Override
    @Transactional
    public void eliminar(java.util.UUID id) {
        if (!repository.existsById(id)) {
            throw new ResourceNotFoundException("No se puede eliminar: Vehículo con ID " + id + " no encontrado");
        }
        repository.deleteById(id);
    }

    @Override
    @Transactional
    public Vehiculo actualizarEstado(java.util.UUID id, String nuevoEstado) {
        Vehiculo v = buscarPorId(id);
        try {
            v.setEstado(EstadoVehiculo.valueOf(nuevoEstado.toUpperCase()));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(
                    "Estado inválido: '" + nuevoEstado + "'. Estados permitidos: DISPONIBLE, EN_SERVICIO, MANTENIMIENTO");
        }
        return repository.save(v);
    }

    /**
     * Mapeo manual de DTO a Entidad para mantener el control de los datos
     * y aplicar el patrón Anti-Corruption Layer dentro del propio Bounded Context.
     */
    private void mapearDtoAEntidad(VehiculoDTO dto, Vehiculo entidad) {
        entidad.setMatricula(dto.getMatricula());
        entidad.setTipo(dto.getTipo());
        entidad.setCapacidad(dto.getCapacidad());
        entidad.setEstado(dto.getEstado());
    }
}