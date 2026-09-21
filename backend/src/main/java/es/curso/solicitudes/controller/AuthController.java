package es.curso.solicitudes.controller;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import es.curso.solicitudes.dto.LoginRequest;
import es.curso.solicitudes.dto.LoginResponse;
import es.curso.solicitudes.service.TokenService;
import jakarta.validation.Valid;

@RestController
public class AuthController {

	private final AuthenticationManager gestorAutenticacion;
	private final TokenService tokens;

	public AuthController(AuthenticationManager gestorAutenticacion, TokenService tokens) {
		this.gestorAutenticacion = gestorAutenticacion;
		this.tokens = tokens;
	}

	// POST /api/auth/login -> 200 con el token, o 401 si el usuario o la contraseña no son correctos
	@PostMapping("/api/auth/login")
	public LoginResponse login(@Valid @RequestBody LoginRequest datos) {
		Authentication autenticacion = gestorAutenticacion.authenticate(
				UsernamePasswordAuthenticationToken.unauthenticated(datos.usuario(), datos.contrasena()));
		return tokens.generar(autenticacion);
	}

}
