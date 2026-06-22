package ec.edu.espe.seguimiento.config;

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
    public static final String POSICIONES_QUEUE = "posiciones-seguimiento-queue";

    @Bean
    public TopicExchange logiflowExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue posicionesQueue() {
        return new Queue(POSICIONES_QUEUE, true);
    }

    @Bean
    public Binding bindingPosiciones(Queue posicionesQueue, TopicExchange logiflowExchange) {
        return BindingBuilder.bind(posicionesQueue).to(logiflowExchange).with("posicion.actualizada");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }


}
