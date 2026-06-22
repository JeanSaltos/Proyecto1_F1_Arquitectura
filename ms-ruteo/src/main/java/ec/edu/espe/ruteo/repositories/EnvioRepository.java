package ec.edu.espe.ruteo.repositories;

import ec.edu.espe.ruteo.models.Envio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface EnvioRepository extends JpaRepository<Envio, UUID> {
    Optional<Envio> findByPedidoId(UUID pedidoId);
}
