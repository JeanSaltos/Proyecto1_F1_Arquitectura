package ec.edu.espe.msflotarest.dtos;

import ec.edu.espe.msflotarest.models.enums.EstadoVehiculo;
import ec.edu.espe.msflotarest.models.enums.TipoVehiculo;
import jakarta.validation.constraints.*;
import lombok.Data;

@Data
public class VehiculoDTO {
    @NotBlank(message = "La matrícula es obligatoria")
    @Size(min = 7, max = 8, message = "La matrícula debe tener entre 7 y 8 caracteres")
    @Pattern(regexp = "^[A-Z]{3}-\\d{3,4}$", message = "Formato de matrícula inválido (Ej: ABC-1234)")
    private String matricula;

    @NotNull(message = "El tipo de vehículo es obligatorio")
    private TipoVehiculo tipo;

    @NotNull(message = "La capacidad es obligatoria")
    @Positive(message = "La capacidad debe ser un número positivo")
    private Double capacidad;

    @NotNull(message = "El estado es obligatorio")
    private EstadoVehiculo estado;
}
