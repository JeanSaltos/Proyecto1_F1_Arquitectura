package ec.edu.espe.clientes.repositories;

import ec.edu.espe.clientes.models.CuentaCorporativa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface CuentaCorporativaRepository extends JpaRepository<CuentaCorporativa, UUID> {
    Optional<CuentaCorporativa> findByClienteId(UUID clienteId);
}
