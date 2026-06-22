package ec.edu.espe.ruteo.config;

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
    public static final String PEDIDOS_QUEUE = "pedidos-ruteo-queue";
    public static final String PEDIDOS_CANCELADOS_QUEUE = "pedidos-cancelados-ruteo-queue";

    @Bean
    public TopicExchange logiflowExchange() {
        return new TopicExchange(EXCHANGE_NAME);
    }

    @Bean
    public Queue pedidosQueue() {
        return new Queue(PEDIDOS_QUEUE, true);
    }

    @Bean
    public Queue pedidosCanceladosQueue() {
        return new Queue(PEDIDOS_CANCELADOS_QUEUE, true);
    }

    @Bean
    public Binding bindingPedidos(Queue pedidosQueue, TopicExchange logiflowExchange) {
        return BindingBuilder.bind(pedidosQueue).to(logiflowExchange).with("pedido.creado");
    }

    @Bean
    public Binding bindingPedidosCancelados(Queue pedidosCanceladosQueue, TopicExchange logiflowExchange) {
        return BindingBuilder.bind(pedidosCanceladosQueue).to(logiflowExchange).with("pedido.cancelado");
    }

    @Bean
    public MessageConverter jsonMessageConverter() {
        return new Jackson2JsonMessageConverter();
    }


}
