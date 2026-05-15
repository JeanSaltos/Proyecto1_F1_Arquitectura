package ec.edu.espe.msflotarest.controllers;

import ec.edu.espe.msflotarest.dtos.ConductorDTO;
import ec.edu.espe.msflotarest.models.Conductor;
import ec.edu.espe.msflotarest.service.IConductorService;
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

@RestController
@RequestMapping("/api/conductores")
@Tag(name = "Conductores", description = "CRUD y consulta de disponibilidad del personal operativo LogiFlow")
public class ConductorController {

    @Autowired
    private IConductorService service;

    @GetMapping
    @Operation(summary = "Listar todos los conductores",
            description = "Retorna la nómina completa del personal operativo")
    @ApiResponse(responseCode = "200", description = "Lista obtenida exitosamente")
    public List<Conductor> listar() {
        return service.listarTodos();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener conductor por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Conductor encontrado"),
            @ApiResponse(responseCode = "404", description = "Conductor no encontrado")
    })
    public ResponseEntity<Conductor> obtenerPorId(@PathVariable java.util.UUID id) {
        return ResponseEntity.ok(service.buscarPorId(id));
    }

    @GetMapping("/cedula/{cedula}")
    @Operation(summary = "Buscar conductor por cédula",
            description = "Busca un conductor por su número de identificación único")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Conductor encontrado"),
            @ApiResponse(responseCode = "404", description = "Conductor no encontrado con esa cédula")
    })
    public ResponseEntity<Conductor> obtenerPorCedula(@PathVariable String cedula) {
        return ResponseEntity.ok(service.buscarPorCedula(cedula));
    }

    @GetMapping("/disponibles")
    @Operation(summary = "Listar conductores disponibles",
            description = "Retorna conductores aptos para asignación inmediata a rutas")
    @ApiResponse(responseCode = "200", description = "Lista de conductores disponibles")
    public List<Conductor> listarDisponibles() {
        return service.listarDisponibles();
    }

    @PostMapping
    @Operation(summary = "Registrar un nuevo conductor")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Conductor registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos"),
            @ApiResponse(responseCode = "409", description = "La cédula ya está registrada")
    })
    public ResponseEntity<Conductor> registrar(@Valid @RequestBody ConductorDTO dto) {
        return new ResponseEntity<>(service.registrar(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar información del conductor")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Conductor actualizado"),
            @ApiResponse(responseCode = "404", description = "Conductor no encontrado"),
            @ApiResponse(responseCode = "409", description = "Conflicto con cédula duplicada")
    })
    public ResponseEntity<Conductor> actualizar(@PathVariable java.util.UUID id, @Valid @RequestBody ConductorDTO dto) {
        return ResponseEntity.ok(service.actualizar(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Dar de baja a un conductor")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Conductor eliminado"),
            @ApiResponse(responseCode = "404", description = "Conductor no encontrado")
    })
    public ResponseEntity<Void> eliminar(@PathVariable java.util.UUID id) {
        service.eliminar(id);
        return ResponseEntity.noContent().build();
    }
}