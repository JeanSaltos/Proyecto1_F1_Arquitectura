package ec.edu.espe.msflotarest.models;

import ec.edu.espe.msflotarest.models.enums.EstadoVehiculo;
import ec.edu.espe.msflotarest.models.enums.TipoVehiculo;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "vehiculos")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Vehiculo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String matricula; //

    @Enumerated(EnumType.STRING)
    private TipoVehiculo tipo; //

    private Double capacidad; // En kg

    @Enumerated(EnumType.STRING)
    private EstadoVehiculo estado; //
}