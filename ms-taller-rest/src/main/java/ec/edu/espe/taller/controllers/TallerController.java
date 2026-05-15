package ec.edu.espe.taller.controllers;

import ec.edu.espe.taller.dtos.MantenimientoDTO;
import ec.edu.espe.taller.models.OrdenMantenimiento;
import ec.edu.espe.taller.models.VehiculoTaller;
import ec.edu.espe.taller.service.ITallerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/taller")
@Tag(name = "Taller", description = "Gestión de mantenimiento en taller externo")
public class TallerController {

    @Autowired
    private ITallerService service;

    @GetMapping("/vehiculos/{matricula}")
    @Operation(summary = "Obtener datos del vehículo", description = "Devuelve los datos del vehículo dado su matrícula")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Vehículo encontrado"),
            @ApiResponse(responseCode = "404", description = "Vehículo no encontrado")
    })
    public ResponseEntity<VehiculoTaller> obtenerVehiculo(@PathVariable String matricula) {
        VehiculoTaller vehiculo = service.consultarVehiculo(matricula);
        if (vehiculo != null) {
            return ResponseEntity.ok(vehiculo);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @PostMapping("/mantenimientos")
    @Operation(summary = "Registrar orden de mantenimiento", description = "Registra una orden de mantenimiento")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Orden creada exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos")
    })
    public ResponseEntity<OrdenMantenimiento> registrarMantenimiento(@Valid @RequestBody MantenimientoDTO dto) {
        OrdenMantenimiento orden = service.registrarOrden(dto.getMatricula(), dto.getDescripcion());
        return new ResponseEntity<>(orden, HttpStatus.CREATED);
    }
}
