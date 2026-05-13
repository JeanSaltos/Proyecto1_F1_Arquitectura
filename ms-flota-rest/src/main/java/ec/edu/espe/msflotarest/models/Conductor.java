package ec.edu.espe.msflotarest.models;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "conductores")
@Data
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Conductor {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String cedula;

    private String nombre; //

    private String licencia;

    private Boolean disponible; //
}
