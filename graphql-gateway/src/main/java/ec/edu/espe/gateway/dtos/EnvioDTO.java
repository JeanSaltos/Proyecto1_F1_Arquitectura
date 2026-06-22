package ec.edu.espe.gateway.dtos;

import lombok.Data;

import java.util.UUID;

@Data
public class EnvioDTO {
    private UUID id;
    private UUID pedidoId;
    private UUID vehiculoId;
    private UUID conductorId;
    private String ruta;
    private String estado;
    private Double kms;
    private String eta;
    private String fechaAsignacion;
    private PosicionDTO ultimaPosicion;
}
