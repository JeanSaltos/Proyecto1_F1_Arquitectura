package ec.edu.espe.ruteo.controllers;

import ec.edu.espe.ruteo.dtos.AsignacionManualDTO;
import ec.edu.espe.ruteo.models.Envio;
import ec.edu.espe.ruteo.service.RuteoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/ruteo")
@Tag(name = "Ruteo y Asignación", description = "Endpoints para la gestión de envíos, asignación de recursos y cálculo de rutas")
public class RuteoController {

    @Autowired
    private RuteoService service;

    @GetMapping("/envios")
    @Operation(summary = "Listar todos los envíos registrados")
    public List<Envio> listar() {
        return service.listarTodos();
    }

    @GetMapping("/envios/{id}")
    @Operation(summary = "Obtener envío por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Envío encontrado"),
            @ApiResponse(responseCode = "404", description = "Envío no encontrado")
    })
    public ResponseEntity<Envio> obtener(@PathVariable UUID id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/envios/pedido/{pedidoId}")
    @Operation(summary = "Obtener envío asociado a un pedido")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Envío encontrado"),
            @ApiResponse(responseCode = "404", description = "No existe envío registrado para ese pedido")
    })
    public ResponseEntity<Envio> obtenerPorPedido(@PathVariable UUID pedidoId) {
        return ResponseEntity.ok(service.buscarPorPedidoId(pedidoId));
    }

    @PostMapping("/asignar")
    @Operation(summary = "Asignación manual de vehículo y conductor", description = "Crea un envío y actualiza los estados operativos del pedido y recursos")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Asignación manual realizada con éxito"),
            @ApiResponse(responseCode = "400", description = "Los recursos no están disponibles o datos incorrectos")
    })
    public ResponseEntity<Envio> asignarManual(@Valid @RequestBody AsignacionManualDTO dto) {
        return new ResponseEntity<>(service.asignarManual(dto), HttpStatus.CREATED);
    }

    @PostMapping("/envios/{id}/simular")
    @Operation(summary = "Simular actualización de coordenadas GPS", description = "Publica un evento posicion.actualizada en RabbitMQ")
    public ResponseEntity<Void> simularPosicion(
            @PathVariable UUID id,
            @RequestParam(required = false) Double lat,
            @RequestParam(required = false) Double lng,
            @RequestParam(required = false) Double velocidad) {
        service.simularPosicion(id, lat, lng, velocidad);
        return ResponseEntity.ok().build();
    }
}
