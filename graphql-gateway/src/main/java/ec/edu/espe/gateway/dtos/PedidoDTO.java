package ec.edu.espe.gateway.dtos;

import lombok.Data;

import java.util.UUID;

@Data
public class PedidoDTO {
    private UUID id;
    private UUID clienteId;
    private String descripcion;
    private Double peso;
    private String origen;
    private String destino;
    private String estado;
    private String prioridad;
    private String fechaCreacion;
}
