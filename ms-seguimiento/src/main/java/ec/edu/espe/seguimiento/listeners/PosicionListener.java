package ec.edu.espe.seguimiento.listeners;

import ec.edu.espe.seguimiento.config.RabbitMQConfig;
import ec.edu.espe.seguimiento.events.PosicionActualizadaEvent;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

@Component
public class PosicionListener {

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @RabbitListener(queues = RabbitMQConfig.POSICIONES_QUEUE)
    public void handlePosicionActualizada(PosicionActualizadaEvent event) {
        System.out.printf("GPS recibido — EnvioId: %s (Lat: %f, Lng: %f)%n",
                event.getEnvioId(), event.getLat(), event.getLng());

        // Retransmitir a clientes WebSocket escuchando en /topic/seguimiento/{envioId}
        String destination = "/topic/seguimiento/" + event.getEnvioId();
        messagingTemplate.convertAndSend(destination, event);
        System.out.println("GPS retransmitido a WebSocket canal: " + destination);
    }
}
