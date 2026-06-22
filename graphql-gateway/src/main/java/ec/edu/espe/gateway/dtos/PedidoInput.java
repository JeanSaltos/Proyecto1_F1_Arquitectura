package ec.edu.espe.gateway.dtos;

import lombok.Data;

import java.util.UUID;

@Data
public class PedidoInput {
    private UUID clienteId;
    private String descripcion;
    private Double peso;
    private String origen;
    private String destino;
    private String prioridad;
}
