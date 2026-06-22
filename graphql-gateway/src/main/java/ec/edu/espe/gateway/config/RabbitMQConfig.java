package ec.edu.espe.gateway.config;

import org.springframework.amqp.core.*;
import org.springframework.amqp.rabbit.connection.ConnectionFactory;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    public static final String EXCHANGE_NAME = "logiflow-exchange";

    // Cola exclusiva del gateway para suscripción reactiva y caché de posiciones
    public static final String GATEWAY_POSITIONS_QUEUE = "gateway-positions-queue";

    @Bean
    public TopicExchange logiflowExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue gatewayPositionsQueue() {
        // Usamos una cola temporal auto-delete para no saturar RabbitMQ cuando el gateway se apaga
        return QueueBuilder.nonDurable(GATEWAY_POSITIONS_QUEUE)
                .autoDelete()
                .build();
    }

    @Bean
    public Binding bindingGatewayPositions(Queue gatewayPositionsQueue, TopicExchange logiflowExchange) {
        return BindingBuilder.bind(gatewayPositionsQueue).to(logiflowExchange).with("posicion.actualizada");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }


}
