package ec.edu.espe.gateway.controllers;

import ec.edu.espe.gateway.dtos.*;
import ec.edu.espe.gateway.listeners.PositionEventConsumer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.graphql.data.method.annotation.Argument;
import org.springframework.graphql.data.method.annotation.MutationMapping;
import org.springframework.graphql.data.method.annotation.QueryMapping;
import org.springframework.graphql.data.method.annotation.SubscriptionMapping;
import org.springframework.stereotype.Controller;
import org.springframework.web.client.RestTemplate;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

@Controller
public class GatewayController {

    @Autowired
    private PositionEventConsumer positionConsumer;

    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${pedidos.service.url:http://localhost:8085}")
    private String pedidosServiceUrl;

    @Value("${ruteo.service.url:http://localhost:8086}")
    private String ruteoServiceUrl;

    // --- QUERIES ---

    @QueryMapping
    public List<PedidoDTO> pedidosActivos(@Argument UUID clienteId) {
        String url = pedidosServiceUrl + "/api/pedidos/cliente/" + clienteId + "/activos";
        PedidoDTO[] response = restTemplate.getForObject(url, PedidoDTO[].class);
        return response != null ? Arrays.asList(response) : List.of();
    }

    @QueryMapping
    public EnvioDTO envio(@Argument UUID id) {
        String url = ruteoServiceUrl + "/api/ruteo/envios/" + id;
        EnvioDTO envio = restTemplate.getForObject(url, EnvioDTO.class);
        
        if (envio != null) {
            // Adjuntar la última posición en caché en el gateway
            PosicionDTO ultimaPos = positionConsumer.getLatestPosition(envio.getId());
            envio.setUltimaPosicion(ultimaPos);
        }
        return envio;
    }

    // --- MUTATIONS ---

    @MutationMapping
    public PedidoDTO crearPedido(@Argument PedidoInput input) {
        String url = pedidosServiceUrl + "/api/pedidos";
        return restTemplate.postForObject(url, input, PedidoDTO.class);
    }

    @MutationMapping
    public PedidoDTO cancelarPedido(@Argument UUID id) {
        String url = pedidosServiceUrl + "/api/pedidos/" + id + "/cancelar";
        return restTemplate.postForObject(url, null, PedidoDTO.class);
    }

    // --- SUBSCRIPTIONS ---

    @SubscriptionMapping
    public Publisher<PosicionDTO> tracking(@Argument UUID envioId) {
        System.out.println("Nueva suscripción GraphQL recibida para rastrear Envío ID: " + envioId);
        // Filtrar reactivamente el flujo global de posiciones por ID de envío específico
        return positionConsumer.getPositionsFlux()
                .filter(pos -> pos.getEnvioId().equals(envioId))
                .doOnCancel(() -> System.out.println("Suscripción GraphQL cancelada para Envío ID: " + envioId));
    }
}
