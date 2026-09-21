package es.curso.solicitudes.controller;

import java.net.URI;
import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import es.curso.solicitudes.dto.SolicitudRequest;
import es.curso.solicitudes.dto.SolicitudResponse;
import es.curso.solicitudes.service.SolicitudService;
import jakarta.validation.Valid;

/**
 * Capa web: recibe peticiones HTTP, delega en el servicio y devuelve JSON.
 */
@RestController
@RequestMapping("/api/solicitudes")
public class SolicitudController {

	private final SolicitudService servicio;

	public SolicitudController(SolicitudService servicio) {
		this.servicio = servicio;
	}

	// GET /api/solicitudes -> 200 con la lista
	@GetMapping
	public List<SolicitudResponse> listar() {
		return servicio.listar();
	}

	// GET /api/solicitudes/5 -> 200 con la solicitud, o 404 si no existe
	@GetMapping("/{id}")
	public SolicitudResponse obtener(@PathVariable Long id) {
		return servicio.obtener(id);
	}

	// POST /api/solicitudes -> 201 con la solicitud creada, o 400 si los datos no son válidos
	@PostMapping
	public ResponseEntity<SolicitudResponse> crear(@Valid @RequestBody SolicitudRequest datos) {
		SolicitudResponse creada = servicio.crear(datos);
		return ResponseEntity.created(URI.create("/api/solicitudes/" + creada.id())).body(creada);
	}

}
