package ec.edu.espe.msflotarest.repositories;

import ec.edu.espe.msflotarest.models.Vehiculo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VehiculoRepository extends JpaRepository<Vehiculo, Long> {
    Optional<Vehiculo> findByMatricula(String matricula);
    boolean existsByMatricula(String matricula);
}