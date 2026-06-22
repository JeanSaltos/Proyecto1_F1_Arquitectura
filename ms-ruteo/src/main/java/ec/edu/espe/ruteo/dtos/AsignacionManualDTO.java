package ec.edu.espe.ruteo.dtos;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.util.UUID;

@Data
public class AsignacionManualDTO {
    @NotNull(message = "El ID del pedido es obligatorio")
    private UUID pedidoId;

    @NotNull(message = "El ID del vehículo es obligatorio")
    private UUID vehiculoId;

    @NotNull(message = "El ID del conductor es obligatorio")
    private UUID conductorId;
}
