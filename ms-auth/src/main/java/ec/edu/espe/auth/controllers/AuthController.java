package ec.edu.espe.auth.controllers;

import ec.edu.espe.auth.dtos.*;
import ec.edu.espe.auth.service.AuthService;
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
@RequestMapping("/api/auth")
@Tag(name = "Autenticación", description = "Endpoints para registro, inicio de sesión y validación de tokens JWT de LogiFlow")
public class AuthController {

    @Autowired
    private AuthService service;

    @PostMapping("/register")
    @Operation(summary = "Registrar un nuevo usuario", description = "Crea un nuevo usuario con credenciales y rol en el sistema")
    @ApiResponses({
            @ApiResponse(responseCode = "201", description = "Usuario registrado exitosamente"),
            @ApiResponse(responseCode = "400", description = "Datos de entrada inválidos o usuario ya existe")
    })
    public ResponseEntity<AuthResponse> register(@Valid @RequestBody RegisterRequest request) {
        return new ResponseEntity<>(service.registrar(request), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    @Operation(summary = "Iniciar sesión", description = "Valida credenciales y retorna un token JWT válido")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Autenticación exitosa"),
            @ApiResponse(responseCode = "400", description = "Credenciales incorrectas")
    })
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest request) {
        return ResponseEntity.ok(service.login(request));
    }

    @GetMapping("/verify")
    @Operation(summary = "Verificar token JWT", description = "Valida si un token JWT es válido y extrae su información")
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Validación ejecutada (revisar 'isValid' en el body)")
    })
    public ResponseEntity<VerifyResponse> verify(
            @RequestParam(required = false) String token,
            @RequestHeader(value = "Authorization", required = false) String authHeader) {

        String jwtToken = token;
        if (jwtToken == null && authHeader != null && authHeader.startsWith("Bearer ")) {
            jwtToken = authHeader.substring(7);
        }

        if (jwtToken == null || jwtToken.isEmpty()) {
            return ResponseEntity.ok(new VerifyResponse(false, null, null));
        }

        return ResponseEntity.ok(service.verificar(jwtToken));
    }
}
