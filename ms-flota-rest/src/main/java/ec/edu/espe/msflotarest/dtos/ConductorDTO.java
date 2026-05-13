package ec.edu.espe.msflotarest.dtos;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ConductorDTO {
    @NotBlank(message = "La cédula es obligatoria")
    @Pattern(regexp = "^\\d{10}$", message = "La cédula debe tener 10 dígitos")
    private String cedula;

    @NotBlank(message = "El nombre es obligatorio")
    @Size(min = 3, max = 100, message = "El nombre debe tener entre 3 y 100 caracteres")
    private String nombre;

    @NotBlank(message = "El tipo de licencia es obligatorio")
    private String licencia;

    @NotNull(message = "La disponibilidad debe ser definida")
    private Boolean disponible;
}
