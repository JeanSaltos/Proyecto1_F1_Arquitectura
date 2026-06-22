package ec.edu.espe.auth.service;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.DecodedJWT;
import ec.edu.espe.auth.dtos.*;
import ec.edu.espe.auth.exception.BusinessException;
import ec.edu.espe.auth.models.Usuario;
import ec.edu.espe.auth.repositories.UsuarioRepository;
import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.Optional;

@Service
public class AuthService {

    @Autowired
    private UsuarioRepository userRepository;

    @Value("${jwt.secret}")
    private String jwtSecret;

    @Value("${jwt.expiration}")
    private long jwtExpiration;

    public AuthResponse registrar(RegisterRequest request) {
        Optional<Usuario> existing = userRepository.findByUsername(request.getUsername());
        if (existing.isPresent()) {
            throw new BusinessException("El nombre de usuario '" + request.getUsername() + "' ya está registrado");
        }

        String hashedPassword = BCrypt.hashpw(request.getPassword(), BCrypt.gensalt());
        Usuario user = Usuario.builder()
                .username(request.getUsername())
                .password(hashedPassword)
                .rol(request.getRol())
                .build();

        userRepository.save(user);
        String token = generarToken(user);
        return new AuthResponse(token, user.getUsername(), user.getRol().name());
    }

    public AuthResponse login(LoginRequest request) {
        Usuario user = userRepository.findByUsername(request.getUsername())
                .orElseThrow(() -> new BusinessException("Usuario o contraseña incorrectos"));

        if (!BCrypt.checkpw(request.getPassword(), user.getPassword())) {
            throw new BusinessException("Usuario o contraseña incorrectos");
        }

        String token = generarToken(user);
        return new AuthResponse(token, user.getUsername(), user.getRol().name());
    }

    public VerifyResponse verificar(String token) {
        try {
            Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
            JWTVerifier verifier = JWT.require(algorithm)
                    .withIssuer("logiflow")
                    .build();
            DecodedJWT jwt = verifier.verify(token);
            String username = jwt.getClaim("username").asString();
            String rol = jwt.getClaim("rol").asString();
            return new VerifyResponse(true, username, rol);
        } catch (Exception e) {
            return new VerifyResponse(false, null, null);
        }
    }

    private String generarToken(Usuario user) {
        Algorithm algorithm = Algorithm.HMAC256(jwtSecret);
        return JWT.create()
                .withIssuer("logiflow")
                .withSubject(user.getId().toString())
                .withClaim("username", user.getUsername())
                .withClaim("rol", user.getRol().name())
                .withIssuedAt(new Date())
                .withExpiresAt(new Date(System.currentTimeMillis() + jwtExpiration))
                .sign(algorithm);
    }
}
