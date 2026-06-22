package ec.edu.espe.ruteo.events;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PedidoCanceladoEvent implements Serializable {
    private static final long serialVersionUID = 1L;

    private UUID id;
    private UUID clienteId;
    private LocalDateTime fechaCancelacion;
}
