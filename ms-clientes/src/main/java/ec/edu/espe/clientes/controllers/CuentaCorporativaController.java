package ec.edu.espe.clientes.controllers;

import ec.edu.espe.clientes.dtos.CuentaCorporativaDTO;
import ec.edu.espe.clientes.models.CuentaCorporativa;
import ec.edu.espe.clientes.service.ClienteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/clientes/cuentas")
@Tag(name = "Cuentas Corporativas", description = "Endpoints para la gestión financiera de cuentas de clientes corporativos de LogiFlow")
public class CuentaCorporativaController {

    @Autowired
    private ClienteService service;

    @GetMapping
    @Operation(summary = "Listar todas las cuentas corporativas")
    public List<CuentaCorporativa> listar() {
        return service.listarCuentas();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener cuenta corporativa por ID")
    public ResponseEntity<CuentaCorporativa> obtener(@PathVariable UUID id) {
        return ResponseEntity.ok(service.buscarCuentaPorId(id));
    }

    @GetMapping("/cliente/{clienteId}")
    @Operation(summary = "Obtener cuenta corporativa asignada a un cliente")
    public ResponseEntity<CuentaCorporativa> obtenerPorCliente(@PathVariable UUID clienteId) {
        return ResponseEntity.ok(service.obtenerCuentaPorClienteId(clienteId));
    }

    @PostMapping
    @Operation(summary = "Crear una cuenta corporativa")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cuenta creada con éxito"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o cuenta ya existente")
    })
    public ResponseEntity<CuentaCorporativa> crear(@Valid @RequestBody CuentaCorporativaDTO dto) {
        return new ResponseEntity<>(service.crearCuenta(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar cuenta corporativa")
    public ResponseEntity<CuentaCorporativa> actualizar(@PathVariable UUID id, @Valid @RequestBody CuentaCorporativaDTO dto) {
        return ResponseEntity.ok(service.actualizarCuenta(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar cuenta corporativa")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cuenta eliminada con exito"),
            @ApiResponse(responseCode = "404", description = "Cuenta corporativa no encontrada")
    })
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        service.eliminarCuenta(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/cliente/{clienteId}/deducir")
    @Operation(summary = "Deducir saldo de la cuenta por servicio logístico")
    public ResponseEntity<CuentaCorporativa> deducir(
            @PathVariable UUID clienteId,
            @RequestParam BigDecimal monto) {
        return ResponseEntity.ok(service.deducirSaldo(clienteId, monto));
    }

    @PostMapping("/cliente/{clienteId}/recargar")
    @Operation(summary = "Recargar saldo a una cuenta corporativa")
    public ResponseEntity<CuentaCorporativa> recargar(
            @PathVariable UUID clienteId,
            @RequestParam BigDecimal monto) {
        return ResponseEntity.ok(service.recargarSaldo(clienteId, monto));
    }
}
