package ec.edu.espe.ruteo.models;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "envios")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Envio {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "pedido_id", nullable = false)
    private UUID pedidoId;

    @Column(name = "vehiculo_id", nullable = false)
    private UUID vehiculoId;

    @Column(name = "conductor_id", nullable = false)
    private UUID conductorId;

    @Column(nullable = false, length = 500)
    private String ruta;

    @Column(nullable = false)
    private String estado;

    @Column(nullable = false)
    private Double kms;

    @Column(nullable = false)
    private LocalDateTime eta;

    @Column(name = "fecha_asignacion", nullable = false)
    private LocalDateTime fechaAsignacion;
}
