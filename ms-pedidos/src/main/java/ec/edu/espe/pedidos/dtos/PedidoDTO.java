package ec.edu.espe.pedidos.dtos;

import ec.edu.espe.pedidos.models.enums.PrioridadPedido;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class PedidoDTO {
    @NotNull(message = "El ID del cliente es obligatorio")
    private UUID clienteId;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;

    @NotNull(message = "El peso es obligatorio")
    @DecimalMin(value = "0.01", message = "El peso debe ser mayor a cero")
    private Double peso;

    @NotBlank(message = "La ubicación de origen es obligatoria")
    private String origen;

    @NotBlank(message = "La ubicación de destino es obligatoria")
    private String destino;

    @NotNull(message = "La prioridad del pedido es obligatoria")
    private PrioridadPedido prioridad;
}
