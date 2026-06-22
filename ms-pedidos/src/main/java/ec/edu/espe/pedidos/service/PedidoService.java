package ec.edu.espe.pedidos.service;

import ec.edu.espe.pedidos.config.RabbitMQConfig;
import ec.edu.espe.pedidos.dtos.PedidoDTO;
import ec.edu.espe.pedidos.events.PedidoCanceladoEvent;
import ec.edu.espe.pedidos.events.PedidoCreadoEvent;
import ec.edu.espe.pedidos.exception.BusinessException;
import ec.edu.espe.pedidos.exception.ResourceNotFoundException;
import ec.edu.espe.pedidos.models.Pedido;
import ec.edu.espe.pedidos.models.enums.EstadoPedido;
import ec.edu.espe.pedidos.repositories.PedidoRepository;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepository repository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${clientes.service.url:http://localhost:8084}")
    private String clientesServiceUrl;

    public List<Pedido> listarTodos() {
        return repository.findAll();
    }

    public Pedido buscarPorId(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Pedido no encontrado con id: " + id));
    }

    public List<Pedido> listarActivosPorCliente(UUID clienteId) {
        List<EstadoPedido> estadosActivos = Arrays.asList(EstadoPedido.CREADO, EstadoPedido.ASIGNADO, EstadoPedido.EN_RUTA);
        return repository.findByClienteIdAndEstadoIn(clienteId, estadosActivos);
    }

    @Transactional
    public Pedido crear(PedidoDTO dto) {
        // Validar que el cliente exista en ms-clientes
        try {
            String url = clientesServiceUrl + "/api/clientes/" + dto.getClienteId();
            restTemplate.getForObject(url, Object.class);
        } catch (HttpClientErrorException.NotFound e) {
            throw new BusinessException("El cliente con id " + dto.getClienteId() + " no está registrado");
        } catch (Exception e) {
            // Si ms-clientes no está disponible, registramos igualmente para no bloquear el flujo (fallback)
            System.err.println("Advertencia: No se pudo validar el cliente en ms-clientes. Detalle: " + e.getMessage());
        }

        Pedido pedido = Pedido.builder()
                .clienteId(dto.getClienteId())
                .descripcion(dto.getDescripcion())
                .peso(dto.getPeso())
                .origen(dto.getOrigen())
                .destino(dto.getDestino())
                .estado(EstadoPedido.CREADO)
                .prioridad(dto.getPrioridad())
                .fechaCreacion(LocalDateTime.now())
                .build();

        pedido = repository.save(pedido);

        // Publicar evento pedido.creado
        PedidoCreadoEvent event = PedidoCreadoEvent.builder()
                .id(pedido.getId())
                .clienteId(pedido.getClienteId())
                .descripcion(pedido.getDescripcion())
                .peso(pedido.getPeso())
                .origen(pedido.getOrigen())
                .destino(pedido.getDestino())
                .prioridad(pedido.getPrioridad().name())
                .estado(pedido.getEstado().name())
                .fechaCreacion(pedido.getFechaCreacion())
                .build();

        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, "pedido.creado", event);
        } catch (Exception e) {
            System.err.println("Fallo al publicar evento pedido.creado: " + e.getMessage());
        }

        return pedido;
    }

    @Transactional
    public Pedido cancelar(UUID id) {
        Pedido pedido = buscarPorId(id);

        if (pedido.getEstado() == EstadoPedido.CANCELADO) {
            throw new BusinessException("El pedido ya está cancelado");
        }
        if (pedido.getEstado() == EstadoPedido.ENTREGADO || pedido.getEstado() == EstadoPedido.EN_RUTA) {
            throw new BusinessException("No se puede cancelar un pedido en estado " + pedido.getEstado());
        }

        pedido.setEstado(EstadoPedido.CANCELADO);
        pedido = repository.save(pedido);

        // Publicar evento pedido.cancelado
        PedidoCanceladoEvent event = PedidoCanceladoEvent.builder()
                .id(pedido.getId())
                .clienteId(pedido.getClienteId())
                .fechaCancelacion(LocalDateTime.now())
                .build();

        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, "pedido.cancelado", event);
        } catch (Exception e) {
            System.err.println("Fallo al publicar evento pedido.cancelado: " + e.getMessage());
        }

        return pedido;
    }

    @Transactional
    public Pedido actualizarEstado(UUID id, EstadoPedido nuevoEstado) {
        Pedido pedido = buscarPorId(id);
        pedido.setEstado(nuevoEstado);
        return repository.save(pedido);
    }
}
