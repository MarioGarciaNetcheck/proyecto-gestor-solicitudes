package es.curso.solicitudes.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

/**
 * Documentación OpenAPI (Swagger UI en /swagger-ui.html).
 * Declara el token JWT para que Swagger muestre el botón "Authorize":
 * se pega el token del login y Swagger lo envía en la cabecera "Authorization: Bearer <token>".
 */
@Configuration
public class OpenApiConfig {

	private static final String ESQUEMA_TOKEN = "token";

	@Bean
	OpenAPI documentacionApi() {
		return new OpenAPI()
				.info(new Info().title("Gestor de solicitudes").version("1.0"))
				.components(new Components().addSecuritySchemes(ESQUEMA_TOKEN,
						new SecurityScheme().type(SecurityScheme.Type.HTTP).scheme("bearer").bearerFormat("JWT")))
				.addSecurityItem(new SecurityRequirement().addList(ESQUEMA_TOKEN));
	}

}
