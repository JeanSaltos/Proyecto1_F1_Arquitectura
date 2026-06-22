package ec.edu.espe.ruteo.listeners;

import ec.edu.espe.ruteo.config.RabbitMQConfig;
import ec.edu.espe.ruteo.events.PedidoCanceladoEvent;
import ec.edu.espe.ruteo.events.PedidoCreadoEvent;
import ec.edu.espe.ruteo.service.RuteoService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class PedidoEventListener {

    @Autowired
    private RuteoService ruteoService;

    @RabbitListener(queues = RabbitMQConfig.PEDIDOS_QUEUE)
    public void handlePedidoCreado(PedidoCreadoEvent event) {
        System.out.println("Evento recibido 'pedido.creado' para Pedido ID: " + event.getId());
        try {
            ruteoService.procesarAsignacionAutomatica(event);
        } catch (Exception e) {
            System.err.println("Error procesando asignación automática para pedido " + event.getId() + ": " + e.getMessage());
        }
    }

    @RabbitListener(queues = RabbitMQConfig.PEDIDOS_CANCELADOS_QUEUE)
    public void handlePedidoCancelado(PedidoCanceladoEvent event) {
        System.out.println("Evento recibido 'pedido.cancelado' para Pedido ID: " + event.getId());
        try {
            ruteoService.procesarCancelacionAutomatica(event);
        } catch (Exception e) {
            System.err.println("Error procesando cancelación automática para pedido " + event.getId() + ": " + e.getMessage());
        }
    }
}
