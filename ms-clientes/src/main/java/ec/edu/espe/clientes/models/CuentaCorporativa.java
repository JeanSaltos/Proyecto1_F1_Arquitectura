package ec.edu.espe.clientes.models;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "cuentas_corporativas")
@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CuentaCorporativa {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "cliente_id", unique = true, nullable = false)
    private UUID clienteId;

    @Column(nullable = false)
    private BigDecimal saldo;

    @Column(name = "limite_credito", nullable = false)
    private BigDecimal limiteCredito;
}
