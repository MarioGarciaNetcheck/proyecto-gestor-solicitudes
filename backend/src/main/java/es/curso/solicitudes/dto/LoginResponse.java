package es.curso.solicitudes.dto;

import java.time.Instant;
import java.util.List;

/**
 * Respuesta del login: el token que el cliente enviará en cada petición y datos para la interfaz.
 */
public record LoginResponse(String token, String usuario, List<String> roles, Instant expira) {
}
