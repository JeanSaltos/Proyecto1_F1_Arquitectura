package ec.edu.espe.msflotarest.repositories;

import ec.edu.espe.msflotarest.models.Conductor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ConductorRepository extends JpaRepository<Conductor, java.util.UUID> {
    Optional<Conductor> findByCedula(String cedula);
    boolean existsByCedula(String cedula);
}