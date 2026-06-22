package ec.edu.espe.auth.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class VerifyResponse {
    private boolean isValid;
    private String username;
    private String rol;
}
