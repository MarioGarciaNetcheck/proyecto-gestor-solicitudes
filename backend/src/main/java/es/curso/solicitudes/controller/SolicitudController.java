package es.curso.solicitudes.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.curso.solicitudes.dto.CambioEstadoRequest;
import es.curso.solicitudes.dto.SolicitudRequest;
import es.curso.solicitudes.dto.SolicitudResponse;
import es.curso.solicitudes.service.SolicitudService;
import jakarta.validation.Valid;

/**
 * Capa web: recibe peticiones HTTP, delega en el servicio y devuelve JSON.
 * "usuario" es el usuario autenticado; Spring lo obtiene del token de la petición.
 */
@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudController {

	private final SolicitudService servicio;

	public SolicitudController(SolicitudService servicio) {
		this.servicio = servicio;
	}

	// GET /api/solicitudes -> 200 con la lista (un USUARIO solo recibe las suyas)
	@GetMapping
	public List<SolicitudResponse> listar(Authentication usuario) {
		return servicio.listar(usuario.getName(), esAdmin(usuario));
	}

	// GET /api/solicitudes/5 -> 200, 404 si no existe o 403 si es de otra persona
	@GetMapping("/{id}")
	public SolicitudResponse obtener(@PathVariable Long id, Authentication usuario) {
		return servicio.obtener(id, usuario.getName(), esAdmin(usuario));
	}

	// POST /api/solicitudes -> 201 con la solicitud creada, o 400 si los datos no son válidos
	@PostMapping
	public ResponseEntity<SolicitudResponse> crear(@Valid @RequestBody SolicitudRequest datos,
			Authentication usuario) {
		SolicitudResponse creada = servicio.crear(datos, usuario.getName());
		return ResponseEntity.created(URI.create("/api/solicitudes/" + creada.id())).body(creada);
	}

	// PATCH /api/solicitudes/5/estado -> 200, o 403 si no es ADMIN
	@PatchMapping("/{id}/estado")
	public SolicitudResponse cambiarEstado(@PathVariable Long id, @Valid @RequestBody CambioEstadoRequest datos) {
		return servicio.cambiarEstado(id, datos.estado());
	}

	private boolean esAdmin(Authentication usuario) {
		return usuario.getAuthorities().stream()
				.anyMatch(rol -> rol.getAuthority().equals("ROLE_ADMIN"));
	}

}
