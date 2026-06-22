package ec.edu.espe.seguimiento.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        // Registrar el endpoint para conexiones del frontend (ex: ws://localhost:8087/ws/seguimiento)
        registry.addEndpoint("/ws/seguimiento")
                .setAllowedOrigins("*");
    }

    @Override
    public void configureMessageBroker(MessageBrokerRegistry registry) {
        // El broker maneja destinos que comiencen con /topic
        registry.enableSimpleBroker("/topic");

        // Destino para enviar mensajes desde cliente a servidor
        registry.setApplicationDestinationPrefixes("/app");
    }
}
