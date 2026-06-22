package ec.edu.espe.clientes.dtos;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.math.BigDecimal;
import java.util.UUID;

@Data
public class CuentaCorporativaDTO {
    @NotNull(message = "El ID del cliente es obligatorio")
    private UUID clienteId;

    @NotNull(message = "El saldo es obligatorio")
    @DecimalMin(value = "0.0", message = "El saldo inicial no puede ser negativo")
    private BigDecimal saldo;

    @NotNull(message = "El límite de crédito es obligatorio")
    @DecimalMin(value = "0.0", message = "El límite de crédito no puede ser negativo")
    private BigDecimal limiteCredito;
}
