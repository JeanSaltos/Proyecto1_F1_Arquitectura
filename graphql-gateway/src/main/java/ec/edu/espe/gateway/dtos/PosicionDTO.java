package ec.edu.espe.gateway.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PosicionDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID envioId;
    private Double lat;
    private Double lng;
    private Double velocidad;
    private String eta;
    private String timestamp;
}
