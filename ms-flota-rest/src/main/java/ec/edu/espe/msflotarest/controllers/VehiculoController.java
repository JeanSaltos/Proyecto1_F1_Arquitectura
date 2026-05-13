package ec.edu.espe.msflotarest.controllers;

import ec.edu.espe.msflotarest.dtos.VehiculoDTO;
import ec.edu.espe.msflotarest.models.Vehiculo;
import ec.edu.espe.msflotarest.models.enums.EstadoVehiculo;
import ec.edu.espe.msflotarest.service.IVehiculoService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/vehiculos")
@Tag(name = "Vehículos", description = "CRUD y consulta de disponibilidad de la flota de transporte LogiFlow")
public class VehiculoController {

    @Autowired
    private IVehiculoService service;

    @GetMapping
    @Operation(summary = "Listar todos los vehículos", description = "Retorna la flota completa de LogiFlow")
    @ApiResponse(responseCode = "200", description = "Lista de vehículos obtenida exitosamente")
    public List<Vehiculo> listar() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener vehículo por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vehículo encontrado"),
            @ApiResponse(responseCode = "404", description = "Vehículo no encontrado")
    })
    public ResponseEntity<Vehiculo> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/matricula/{matricula}")
    @Operation(summary = "Buscar vehículo por matrícula",
            description = "Busca un vehículo por su identificador único de placa")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vehículo encontrado"),
            @ApiResponse(responseCode = "404", description = "No existe vehículo con esa matrícula")
    })
    public ResponseEntity<Vehiculo> obtenerPorMatricula(@PathVariable String matricula) {
        return ResponseEntity.ok(service.buscarPorMatricula(matricula));
    }

    @GetMapping("/disponibles")
    @Operation(summary = "Consultar vehículos disponibles",
            description = "Retorna únicamente los vehículos con estado DISPONIBLE, " +
                    "listos para asignación a rutas de entrega")
    @ApiResponse(responseCode = "200", description = "Lista de vehículos disponibles")
    public List<Vehiculo> listarDisponibles() {
        return service.listarTodos().stream()
                .filter(v -> EstadoVehiculo.DISPONIBLE.equals(v.getEstado()))
                .collect(Collectors.toList());
    }

    @PostMapping
    @Operation(summary = "Registrar un nuevo vehículo en la flota")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Vehículo creado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "409", description = "La matrícula ya está registrada")
    })
    public ResponseEntity<Vehiculo> crear(@Valid @RequestBody VehiculoDTO dto) {
        return new ResponseEntity<>(service.crear(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar datos de un vehículo")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vehículo actualizado"),
            @ApiResponse(responseCode = "404", description = "Vehículo no encontrado"),
            @ApiResponse(responseCode = "409", description = "Conflicto con matrícula duplicada")
    })
    public ResponseEntity<Vehiculo> actualizar(@PathVariable Long id, @Valid @RequestBody VehiculoDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un vehículo de la flota")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Vehículo eliminado"),
            @ApiResponse(responseCode = "404", description = "Vehículo no encontrado")
    })
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/estado")
    @Operation(summary = "Actualizar solo el estado operativo del vehículo",
            description = "Estados válidos: DISPONIBLE, EN_SERVICIO, MANTENIMIENTO")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Estado actualizado"),
            @ApiResponse(responseCode = "400", description = "Estado inválido"),
            @ApiResponse(responseCode = "404", description = "Vehículo no encontrado")
    })
    public ResponseEntity<Vehiculo> cambiarEstado(
            @PathVariable Long id,
            @Parameter(description = "Nuevo estado: DISPONIBLE | EN_SERVICIO | MANTENIMIENTO")
            @RequestParam String nuevoEstado) {
        return ResponseEntity.ok(service.actualizarEstado(id, nuevoEstado));
    }
}