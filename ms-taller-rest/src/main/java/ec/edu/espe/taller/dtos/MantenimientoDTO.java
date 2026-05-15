package ec.edu.espe.taller.dtos;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class MantenimientoDTO {

    @NotBlank(message = "La matrícula es obligatoria")
    private String matricula;

    @NotBlank(message = "La descripción es obligatoria")
    private String descripcion;
}
