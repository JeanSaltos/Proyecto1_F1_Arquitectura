package ec.edu.espe.msflotarest.config;

import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

/**
 * Configuración centralizada de la documentación OpenAPI/Swagger.
 * Accesible en: http://localhost:8081/swagger-ui.html
 */
@Configuration
public class OpenApiConfig {

    @Bean
    public OpenAPI logiFlowFlotaOpenAPI() {
        return new OpenAPI()
                .info(new Info()
                        .title("LogiFlow — API de Gestión de Flota (ms-flota-rest)")
                        .description("Microservicio REST del Bounded Context 'Gestión de Flota'. " +
                                "Proporciona operaciones CRUD para vehículos y conductores, " +
                                "incluyendo consulta de disponibilidad para el módulo de Ruteo y Despacho.")
                        .version("1.0.0")
                        .contact(new Contact()
                                .name("Saltos y Velasquez — ESPE")
                                .email("logiflow@espe.edu.ec"))
                        .license(new License()
                                .name("Uso Académico")
                                .url("https://www.espe.edu.ec")))
                .servers(List.of(
                        new Server().url("http://localhost:8081").description("Entorno Local de Desarrollo")
                ));
    }
}
