package ec.edu.espe.gateway.listeners;

import ec.edu.espe.gateway.config.RabbitMQConfig;
import ec.edu.espe.gateway.dtos.PosicionDTO;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Sinks;

import java.util.concurrent.ConcurrentHashMap;
import java.util.UUID;

@Component
public class PositionEventConsumer {

    // Caché en memoria de la última posición conocida para cada envío
    private final ConcurrentHashMap<UUID, PosicionDTO> latestPositions = new ConcurrentHashMap<>();

    // Multi-transmisor reactivo de Reactor para alimentar suscripciones GraphQL
    private final Sinks.Many<PosicionDTO> sink = Sinks.many().multicast().onBackpressureBuffer();

    @RabbitListener(queues = RabbitMQConfig.GATEWAY_POSITIONS_QUEUE)
    public void consumePosition(PosicionDTO event) {
        if (event.getEnvioId() != null) {
            System.out.println("BFF Gateway capturó coordenadas GPS para Envío ID: " + event.getEnvioId());
            latestPositions.put(event.getEnvioId(), event);
            sink.tryEmitNext(event);
        }
    }

    public PosicionDTO getLatestPosition(UUID envioId) {
        return latestPositions.get(envioId);
    }

    public Flux<PosicionDTO> getPositionsFlux() {
        return sink.asFlux();
    }
}
