package ec.edu.espe.clientes.controllers;

import ec.edu.espe.clientes.dtos.ClienteDTO;
import ec.edu.espe.clientes.models.Cliente;
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

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/clientes")
@Tag(name = "Clientes", description = "CRUD de Clientes LogiFlow")
public class ClienteController {

    @Autowired
    private ClienteService service;

    @GetMapping
    @Operation(summary = "Listar todos los clientes")
    public List<Cliente> listar() {
        return service.listarClientes();
    }

    @GetMapping("/{id}")
    @Operation(summary = "Obtener cliente por ID")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    public ResponseEntity<Cliente> obtener(@PathVariable UUID id) {
        return ResponseEntity.ok(service.buscarClientePorId(id));
    }

    @GetMapping("/identificacion/{identificacion}")
    @Operation(summary = "Obtener cliente por cédula o RUC")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente encontrado"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    public ResponseEntity<Cliente> obtenerPorIdentificacion(@PathVariable String identificacion) {
        return ResponseEntity.ok(service.buscarClientePorIdentificacion(identificacion));
    }

    @PostMapping
    @Operation(summary = "Registrar un cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Cliente registrado con éxito"),
            @ApiResponse(responseCode = "400", description = "Datos inválidos o identificación ya registrada")
    })
    public ResponseEntity<Cliente> crear(@Valid @RequestBody ClienteDTO dto) {
        return new ResponseEntity<>(service.crearCliente(dto), HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Actualizar cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Cliente actualizado"),
            @ApiResponse(responseCode = "400", description = "Identificación duplicada"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    public ResponseEntity<Cliente> actualizar(@PathVariable UUID id, @Valid @RequestBody ClienteDTO dto) {
        return ResponseEntity.ok(service.actualizarCliente(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar cliente")
    @ApiResponses({
            @ApiResponse(responseCode = "204", description = "Cliente eliminado"),
            @ApiResponse(responseCode = "404", description = "Cliente no encontrado")
    })
    public ResponseEntity<Void> eliminar(@PathVariable UUID id) {
        service.eliminarCliente(id);
        return ResponseEntity.noContent().build();
    }
}
