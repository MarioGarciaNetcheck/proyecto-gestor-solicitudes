package es.curso.solicitudes.service;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jwt.JwsHeader;
import org.springframework.security.oauth2.jwt.JwtClaimsSet;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.JwtEncoderParameters;
import org.springframework.stereotype.Service;

import es.curso.solicitudes.dto.LoginResponse;

/**
 * Genera el token JWT de un usuario ya autenticado.
 * El token lleva quién es (sub), sus roles y cuándo caduca (exp). Va firmado, pero NO cifrado:
 * cualquiera puede leer su contenido (por ejemplo en jwt.io), así que nunca debe llevar datos sensibles.
 */
@Service
public class TokenService {

	private final JwtEncoder codificador;
	private final long minutosValidez;

	public TokenService(JwtEncoder codificador, @Value("${app.jwt.minutos-validez}") long minutosValidez) {
		this.codificador = codificador;
		this.minutosValidez = minutosValidez;
	}

	public LoginResponse generar(Authentication autenticacion) {
		Instant ahora = Instant.now();
		Instant expira = ahora.plus(minutosValidez, ChronoUnit.MINUTES);

		// Solo los roles, sin el prefijo interno de Spring: ROLE_ADMIN -> ADMIN
		List<String> roles = autenticacion.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.filter(permiso -> permiso.startsWith("ROLE_"))
				.map(rol -> rol.substring("ROLE_".length()))
				.toList();

		JwtClaimsSet datos = JwtClaimsSet.builder()
				.issuer("gestor-solicitudes")
				.subject(autenticacion.getName())
				.issuedAt(ahora)
				.expiresAt(expira)
				.claim("roles", roles)
				.build();

		JwsHeader cabecera = JwsHeader.with(MacAlgorithm.HS256).build();
		String token = codificador.encode(JwtEncoderParameters.from(cabecera, datos)).getTokenValue();

		return new LoginResponse(token, autenticacion.getName(), roles, expira);
	}

}
