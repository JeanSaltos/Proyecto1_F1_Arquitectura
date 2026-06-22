package ec.edu.espe.pedidos.controllers;

import ec.edu.espe.pedidos.dtos.PedidoDTO;
import ec.edu.espe.pedidos.models.Pedido;
import ec.edu.espe.pedidos.models.enums.EstadoPedido;
import ec.edu.espe.pedidos.service.PedidoService;
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
@RequestMapping("/api/pedidos")
@Tag(name = "Pedidos", description = "Endpoints para la creación, cancelación y consulta de pedidos en LogiFlow")
public class PedidoController {

    @Autowired
    private PedidoService service;

    @GetMapping
    @Operation(summary = "Listar todos los pedidos")
    public List<Pedido> listar() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener un pedido por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido encontrado"),
            @ApiResponse(responseCode = "404", description = "Pedido no encontrado")
    })
    public ResponseEntity<Pedido> obtener(@PathVariable UUID id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/cliente/{clienteId}/activos")
    @Operation(summary = "Listar pedidos activos de un cliente", description = "Retorna pedidos en estado CREADO, ASIGNADO o EN_RUTA")
    public List<Pedido> listarActivos(@PathVariable UUID clienteId) {
        return service.listarActivosPorCliente(clienteId);
    }

    @PostMapping
    @Operation(summary = "Crear un nuevo pedido", description = "Registra un pedido y publica el evento pedido.creado")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Pedido registrado y publicado"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o cliente no existe")
    })
    public ResponseEntity<Pedido> crear(@Valid @RequestBody PedidoDTO dto) {
        return new ResponseEntity<>(service.crear(dto), HttpStatus.CREATED);
    }

    @PostMapping("/{id}/cancelar")
    @Operation(summary = "Cancelar un pedido", description = "Cancela el pedido y publica el evento pedido.cancelado")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Pedido cancelado con éxito"),
            @ApiResponse(responseCode = "400", description = "El pedido no se puede cancelar en su estado actual")
    })
    public ResponseEntity<Pedido> cancelar(@PathVariable UUID id) {
        return ResponseEntity.ok(service.cancelar(id));
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Actualizar estado del pedido", description = "Cambia el estado de un pedido (ej. ASIGNADO, EN_RUTA, ENTREGADO)")
    public ResponseEntity<Pedido> actualizarEstado(
            @PathVariable UUID id,
            @RequestParam EstadoPedido nuevoEstado) {
        return ResponseEntity.ok(service.actualizarEstado(id, nuevoEstado));
    }
}
