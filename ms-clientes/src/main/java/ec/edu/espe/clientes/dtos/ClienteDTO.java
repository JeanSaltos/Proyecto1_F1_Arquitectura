package ec.edu.espe.clientes.dtos;

import ec.edu.espe.clientes.models.enums.TipoCliente;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class ClienteDTO {
    @NotBlank(message = "El nombre del cliente es obligatorio")
    private String nombre;

    @NotBlank(message = "El email es obligatorio")
    @Email(message = "El formato de email no es válido")
    private String email;

    @NotBlank(message = "El teléfono es obligatorio")
    private String telefono;

    @NotNull(message = "El tipo de cliente es obligatorio")
    private TipoCliente tipo;

    @NotBlank(message = "La identificación (Cédula o RUC) es obligatoria")
    private String identificacion;
}
