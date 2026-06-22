package ec.edu.espe.ruteo.service;

import ec.edu.espe.ruteo.config.RabbitMQConfig;
import ec.edu.espe.ruteo.dtos.AsignacionManualDTO;
import ec.edu.espe.ruteo.events.EnvioAsignadoEvent;
import ec.edu.espe.ruteo.events.PedidoCanceladoEvent;
import ec.edu.espe.ruteo.events.PedidoCreadoEvent;
import ec.edu.espe.ruteo.exception.BusinessException;
import ec.edu.espe.ruteo.exception.ResourceNotFoundException;
import ec.edu.espe.ruteo.models.Envio;
import ec.edu.espe.ruteo.repositories.EnvioRepository;
import lombok.Data;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.http.client.JdkClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.*;

@Service
public class RuteoService {

    @Autowired
    private EnvioRepository repository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    private final RestTemplate restTemplate = new RestTemplate(new JdkClientHttpRequestFactory());

    @Value("${flota.service.url:http://localhost:8081}")
    private String flotaServiceUrl;

    @Value("${pedidos.service.url:http://localhost:8085}")
    private String pedidosServiceUrl;

    public List<Envio> listarTodos() {
        return repository.findAll();
    }

    public Envio buscarPorId(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Envío no encontrado con id: " + id));
    }

    public Envio buscarPorPedidoId(UUID pedidoId) {
        return repository.findByPedidoId(pedidoId)
                .orElseThrow(() -> new ResourceNotFoundException("No existe envío registrado para el pedido: " + pedidoId));
    }

    @Transactional
    public void procesarAsignacionAutomatica(PedidoCreadoEvent event) {
        System.out.println("Iniciando asignación automática para Pedido ID: " + event.getId());

        // 1. Obtener vehículos disponibles
        VehiculoResponse[] vehiculos;
        try {
            String url = flotaServiceUrl + "/api/vehiculos/disponibles";
            vehiculos = restTemplate.getForObject(url, VehiculoResponse[].class);
        } catch (Exception e) {
            System.err.println("No se pudo conectar a ms-flota-rest para obtener vehículos: " + e.getMessage());
            return;
        }

        if (vehiculos == null || vehiculos.length == 0) {
            System.out.println("No hay vehículos disponibles para asignar al pedido: " + event.getId());
            return;
        }

        // 2. Obtener conductores disponibles
        ConductorResponse[] conductores;
        try {
            String url = flotaServiceUrl + "/api/conductores/disponibles";
            conductores = restTemplate.getForObject(url, ConductorResponse[].class);
        } catch (Exception e) {
            System.err.println("No se pudo conectar a ms-flota-rest para obtener conductores: " + e.getMessage());
            return;
        }

        if (conductores == null || conductores.length == 0) {
            System.out.println("No hay conductores disponibles para asignar al pedido: " + event.getId());
            return;
        }

        // 3. Emparejar (algoritmo simple: primer vehículo y conductor disponibles)
        VehiculoResponse vehiculo = vehiculos[0];
        ConductorResponse conductor = conductores[0];

        // 4. Ejecutar la asignación
        ejecutarAsignacion(event.getId(), vehiculo, conductor, event.getOrigen(), event.getDestino());
    }

    @Transactional
    public Envio asignarManual(AsignacionManualDTO dto) {
        // Verificar si ya existe un envío para este pedido
        Optional<Envio> existing = repository.findByPedidoId(dto.getPedidoId());
        if (existing.isPresent()) {
            throw new BusinessException("El pedido " + dto.getPedidoId() + " ya tiene un envío asignado");
        }

        // Obtener detalles del vehículo
        VehiculoResponse vehiculo;
        try {
            String url = flotaServiceUrl + "/api/vehiculos/" + dto.getVehiculoId();
            vehiculo = restTemplate.getForObject(url, VehiculoResponse.class);
        } catch (Exception e) {
            throw new BusinessException("No se pudo obtener datos del vehículo con ID: " + dto.getVehiculoId() + ". Error: " + e.getMessage());
        }

        if (vehiculo == null || !"DISPONIBLE".equals(vehiculo.getEstado())) {
            throw new BusinessException("El vehículo " + dto.getVehiculoId() + " no está disponible");
        }

        // Obtener detalles del conductor
        ConductorResponse conductor;
        try {
            String url = flotaServiceUrl + "/api/conductores/" + dto.getConductorId();
            conductor = restTemplate.getForObject(url, ConductorResponse.class);
        } catch (Exception e) {
            throw new BusinessException("No se pudo obtener datos del conductor con ID: " + dto.getConductorId() + ". Error: " + e.getMessage());
        }

        if (conductor == null || !conductor.getDisponible()) {
            throw new BusinessException("El conductor " + dto.getConductorId() + " no está disponible");
        }

        // Obtener origen y destino del pedido
        String origen = "Origen Manual";
        String destino = "Destino Manual";
        try {
            String url = pedidosServiceUrl + "/api/pedidos/" + dto.getPedidoId();
            Map<?, ?> pedidoData = restTemplate.getForObject(url, Map.class);
            if (pedidoData != null) {
                origen = (String) pedidoData.get("origen");
                destino = (String) pedidoData.get("destino");
            }
        } catch (Exception e) {
            System.err.println("Advertencia: No se pudo obtener origen/destino del pedido. Usando valores por defecto.");
        }

        return ejecutarAsignacion(dto.getPedidoId(), vehiculo, conductor, origen, destino);
    }

    private Envio ejecutarAsignacion(UUID pedidoId, VehiculoResponse vehiculo, ConductorResponse conductor, String origen, String destino) {
        // 1. Cambiar estado del vehículo a EN_SERVICIO
        try {
            String url = flotaServiceUrl + "/api/vehiculos/" + vehiculo.getId() + "/estado?nuevoEstado=EN_SERVICIO";
            restTemplate.patchForObject(url, null, Object.class);
        } catch (Exception e) {
            System.err.println("No se pudo cambiar estado del vehículo en ms-flota-rest: " + e.getMessage());
        }

        // 2. Cambiar estado del conductor a no disponible
        try {
            String url = flotaServiceUrl + "/api/conductores/" + conductor.getId();
            Map<String, Object> conductorUpdate = new HashMap<>();
            conductorUpdate.put("cedula", conductor.getCedula());
            conductorUpdate.put("nombre", conductor.getNombre());
            conductorUpdate.put("licencia", conductor.getLicencia());
            conductorUpdate.put("disponible", false);
            restTemplate.put(url, conductorUpdate);
        } catch (Exception e) {
            System.err.println("No se pudo cambiar estado del conductor en ms-flota-rest: " + e.getMessage());
        }

        // 3. Cambiar estado del pedido a ASIGNADO
        try {
            String url = pedidosServiceUrl + "/api/pedidos/" + pedidoId + "/estado?nuevoEstado=ASIGNADO";
            restTemplate.exchange(url, HttpMethod.PATCH, null, Object.class);
        } catch (Exception e) {
            System.err.println("No se pudo cambiar estado del pedido en ms-pedidos: " + e.getMessage());
        }

        // 4. Generar Envio en ms-ruteo
        double kms = Math.round((5.0 + new Random().nextDouble() * 95.0) * 10.0) / 10.0; // Random entre 5 y 100 kms
        String rutaOptimizada = String.format("Ruta óptima de %s a %s vía Carretera Principal (Distancia: %.1f km)", origen, destino, kms);

        Envio envio = Envio.builder()
                .pedidoId(pedidoId)
                .vehiculoId(vehiculo.getId())
                .conductorId(conductor.getId())
                .ruta(rutaOptimizada)
                .estado("ASIGNADO")
                .kms(kms)
                .eta(LocalDateTime.now().plusHours(2)) // ETA de 2 horas
                .fechaAsignacion(LocalDateTime.now())
                .build();

        envio = repository.save(envio);

        // 5. Publicar evento envio.asignado
        EnvioAsignadoEvent assignedEvent = EnvioAsignadoEvent.builder()
                .id(envio.getId())
                .pedidoId(envio.getPedidoId())
                .vehiculoId(envio.getVehiculoId())
                .conductorId(envio.getConductorId())
                .ruta(envio.getRuta())
                .kms(envio.getKms())
                .eta(envio.getEta())
                .fechaAsignacion(envio.getFechaAsignacion())
                .build();

        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, "envio.asignado", assignedEvent);
            System.out.println("Evento 'envio.asignado' publicado para Envío ID: " + envio.getId());
        } catch (Exception e) {
            System.err.println("Error al publicar evento envio.asignado: " + e.getMessage());
        }

        return envio;
    }

    @Transactional
    public void procesarCancelacionAutomatica(PedidoCanceladoEvent event) {
        Optional<Envio> optEnvio = repository.findByPedidoId(event.getId());
        if (optEnvio.isEmpty()) {
            System.out.println("No existe envío asignado para cancelar para el Pedido ID: " + event.getId());
            return;
        }

        Envio envio = optEnvio.get();
        envio.setEstado("CANCELADO");
        repository.save(envio);

        // Liberar vehículo en ms-flota-rest
        try {
            String url = flotaServiceUrl + "/api/vehiculos/" + envio.getVehiculoId() + "/estado?nuevoEstado=DISPONIBLE";
            restTemplate.patchForObject(url, null, Object.class);
        } catch (Exception e) {
            System.err.println("No se pudo liberar vehículo en ms-flota-rest: " + e.getMessage());
        }

        // Liberar conductor en ms-flota-rest
        try {
            String url = flotaServiceUrl + "/api/conductores/" + envio.getConductorId();
            ConductorResponse conductor = restTemplate.getForObject(url, ConductorResponse.class);
            if (conductor != null) {
                Map<String, Object> conductorUpdate = new HashMap<>();
                conductorUpdate.put("cedula", conductor.getCedula());
                conductorUpdate.put("nombre", conductor.getNombre());
                conductorUpdate.put("licencia", conductor.getLicencia());
                conductorUpdate.put("disponible", true);
                restTemplate.put(url, conductorUpdate);
            }
        } catch (Exception e) {
            System.err.println("No se pudo liberar conductor en ms-flota-rest: " + e.getMessage());
        }

        System.out.println("Envío ID " + envio.getId() + " cancelado y recursos liberados.");
    }

    @Transactional
    public void simularPosicion(UUID envioId, Double lat, Double lng, Double velocidad) {
        Envio envio = buscarPorId(envioId);
        
        if ("ASIGNADO".equals(envio.getEstado())) {
            envio.setEstado("EN_TRANSITO");
            repository.save(envio);
            
            try {
                String url = pedidosServiceUrl + "/api/pedidos/" + envio.getPedidoId() + "/estado?nuevoEstado=EN_RUTA";
                restTemplate.exchange(url, HttpMethod.PATCH, null, Object.class);
            } catch (Exception e) {
                System.err.println("No se pudo cambiar estado del pedido a EN_RUTA en ms-pedidos: " + e.getMessage());
            }
        }
        
        ec.edu.espe.ruteo.events.PosicionActualizadaEvent event = ec.edu.espe.ruteo.events.PosicionActualizadaEvent.builder()
                .envioId(envioId)
                .lat(lat != null ? lat : -0.220164 + new Random().nextDouble() * 0.01)
                .lng(lng != null ? lng : -78.512327 + new Random().nextDouble() * 0.01)
                .velocidad(velocidad != null ? velocidad : 50.0 + new Random().nextDouble() * 20.0)
                .eta(envio.getEta().minusMinutes(5))
                .timestamp(LocalDateTime.now())
                .build();
                
        try {
            rabbitTemplate.convertAndSend(RabbitMQConfig.EXCHANGE_NAME, "posicion.actualizada", event);
            System.out.println("Simulación de posición publicada para Envío ID: " + envioId);
        } catch (Exception e) {
            System.err.println("Error al publicar simulación de posición: " + e.getMessage());
        }
    }

    // --- Clases Auxiliares de Deserialización ---

    @Data
    public static class VehiculoResponse {
        private UUID id;
        private String matricula;
        private String tipo;
        private Double capacidad;
        private String estado;
    }

    @Data
    public static class ConductorResponse {
        private UUID id;
        private String cedula;
        private String nombre;
        private String licencia;
        private Boolean disponible;
    }
}
