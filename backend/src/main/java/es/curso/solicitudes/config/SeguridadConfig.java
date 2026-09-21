package es.curso.solicitudes.config;

import java.nio.charset.StandardCharsets;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Configuración de seguridad del curso. Deliberadamente pequeña:
 * - Usuarios en memoria (en un proyecto real vendrían de una base de datos o de un proveedor de identidad).
 * - Login con usuario y contraseña que devuelve un token JWT.
 * - Cada petición a la API debe llevar la cabecera "Authorization: Bearer <token>".
 */
@Configuration
public class SeguridadConfig {

	// 1. Reglas de acceso por URL y rol
	@Bean
	SecurityFilterChain reglasDeAcceso(HttpSecurity http) throws Exception {
		http
				.authorizeHttpRequests(reglas -> reglas
						.requestMatchers("/api/saludo", "/api/auth/login").permitAll()
						.requestMatchers("/h2-console/**").permitAll()
						// Documentación de la API (Swagger). En producción se desactiva o se protege
						.requestMatchers("/swagger-ui.html", "/swagger-ui/**", "/v3/api-docs/**").permitAll()
						.requestMatchers(HttpMethod.PATCH, "/api/solicitudes/*/estado").hasRole("ADMIN")
						.requestMatchers("/api/**").hasAnyRole("USUARIO", "ADMIN")
						.anyRequest().denyAll())
				// La API no guarda sesión: cada petición se identifica con su token
				.sessionManagement(sesion -> sesion.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				// CSRF protege las sesiones basadas en cookies. Con token en cabecera no es necesario.
				.csrf(csrf -> csrf.disable())
				// La consola de H2 se muestra dentro de un iframe
				.headers(cabeceras -> cabeceras.frameOptions(opciones -> opciones.sameOrigin()))
				// Valida el token JWT de cada petición
				.oauth2ResourceServer(servidor -> servidor.jwt(Customizer.withDefaults()));
		return http.build();
	}

	// 2. Usuarios de prueba. Las contraseñas se guardan cifradas con BCrypt, nunca en claro.
	@Bean
	UserDetailsService usuarios(PasswordEncoder cifrador) {
		return new InMemoryUserDetailsManager(
				User.withUsername("ana").password(cifrador.encode("ana123")).roles("USUARIO").build(),
				User.withUsername("luis").password(cifrador.encode("luis123")).roles("USUARIO").build(),
				User.withUsername("admin").password(cifrador.encode("admin123")).roles("ADMIN").build());
	}

	@Bean
	PasswordEncoder cifrador() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	// 3. Comprueba usuario y contraseña en el login
	@Bean
	AuthenticationManager gestorAutenticacion(UserDetailsService usuarios, PasswordEncoder cifrador) {
		DaoAuthenticationProvider proveedor = new DaoAuthenticationProvider(usuarios);
		proveedor.setPasswordEncoder(cifrador);
		return new ProviderManager(proveedor);
	}

	// 4. Firma y verificación de tokens JWT con una clave secreta (HMAC-SHA256).
	// La clave llega por configuración (variable de entorno JWT_SECRETO), no está escrita en el código.
	@Bean
	SecretKey claveJwt(@Value("${app.jwt.secreto}") String secreto) {
		byte[] bytes = secreto.getBytes(StandardCharsets.UTF_8);
		if (bytes.length < 32) {
			throw new IllegalStateException("app.jwt.secreto debe tener al menos 32 caracteres");
		}
		return new SecretKeySpec(bytes, "HmacSHA256");
	}

	@Bean
	JwtEncoder codificadorJwt(SecretKey claveJwt) {
		return NimbusJwtEncoder.withSecretKey(claveJwt).build();
	}

	@Bean
	JwtDecoder decodificadorJwt(SecretKey claveJwt) {
		return NimbusJwtDecoder.withSecretKey(claveJwt).macAlgorithm(MacAlgorithm.HS256).build();
	}

	// 5. Convierte el campo "roles" del token en roles de Spring (USUARIO -> ROLE_USUARIO)
	@Bean
	JwtAuthenticationConverter convertidorRoles() {
		JwtGrantedAuthoritiesConverter roles = new JwtGrantedAuthoritiesConverter();
		roles.setAuthoritiesClaimName("roles");
		roles.setAuthorityPrefix("ROLE_");
		JwtAuthenticationConverter convertidor = new JwtAuthenticationConverter();
		convertidor.setJwtGrantedAuthoritiesConverter(roles);
		return convertidor;
	}

}
