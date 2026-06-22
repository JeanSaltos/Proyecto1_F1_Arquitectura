package ec.edu.espe.pedidos.integration;

import ec.edu.espe.pedidos.config.RabbitMQConfig;
import ec.edu.espe.pedidos.repositories.PedidoRepository;
import org.junit.jupiter.api.Test;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.RabbitMQContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

import java.util.Map;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@Testcontainers
@SpringBootTest
class PedidoMessagingIT {

    @Container
    static final PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:14-alpine")
            .withDatabaseName("logiflow_pedidos")
            .withUsername("tc_user")
            .withPassword("tc_password");

    @Container
    static final RabbitMQContainer rabbitmq = new RabbitMQContainer("rabbitmq:3-management-alpine");

    @Autowired
    private PedidoRepository pedidoRepository;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @DynamicPropertySource
    static void registerProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
        registry.add("spring.jpa.hibernate.ddl-auto", () -> "create-drop");
        registry.add("spring.rabbitmq.host", rabbitmq::getHost);
        registry.add("spring.rabbitmq.port", rabbitmq::getAmqpPort);
        registry.add("spring.rabbitmq.username", rabbitmq::getAdminUsername);
        registry.add("spring.rabbitmq.password", rabbitmq::getAdminPassword);
    }

    @Test
    void startsPostgresAndRabbitMqAndPublishesEvent() {
        assertThat(postgres.isRunning()).isTrue();
        assertThat(rabbitmq.isRunning()).isTrue();
        assertThat(pedidoRepository.count()).isZero();

        rabbitTemplate.convertAndSend(
                RabbitMQConfig.EXCHANGE_NAME,
                "pedido.creado",
                Map.of("id", UUID.randomUUID().toString(), "estado", "CREADO"));
    }
}
