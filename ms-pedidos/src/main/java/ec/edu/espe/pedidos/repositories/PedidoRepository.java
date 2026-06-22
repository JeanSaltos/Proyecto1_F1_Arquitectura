package ec.edu.espe.pedidos.repositories;

import ec.edu.espe.pedidos.models.Pedido;
import ec.edu.espe.pedidos.models.enums.EstadoPedido;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface PedidoRepository extends JpaRepository<Pedido, UUID> {
    List<Pedido> findByClienteId(UUID clienteId);
    List<Pedido> findByClienteIdAndEstadoIn(UUID clienteId, List<EstadoPedido> estados);
}
