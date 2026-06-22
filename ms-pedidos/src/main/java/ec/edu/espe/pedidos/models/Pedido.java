package ec.edu.espe.pedidos.models;

import ec.edu.espe.pedidos.models.enums.EstadoPedido;
import ec.edu.espe.pedidos.models.enums.PrioridadPedido;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "pedidos")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pedido {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "cliente_id", nullable = false)
    private UUID clienteId;

    @Column(nullable = false)
    private String descripcion;

    @Column(nullable = false)
    private Double peso;

    @Column(nullable = false)
    private String origen;

    @Column(nullable = false)
    private String destino;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoPedido estado;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private PrioridadPedido prioridad;

    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;
}
