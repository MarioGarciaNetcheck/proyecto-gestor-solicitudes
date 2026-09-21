package es.curso.solicitudes.service;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import es.curso.solicitudes.dto.SolicitudRequest;
import es.curso.solicitudes.dto.SolicitudResponse;
import es.curso.solicitudes.model.Solicitud;

/**
 * Lógica de negocio. El controlador no guarda datos: se los pide al servicio.
 * De momento las solicitudes se guardan en una lista en memoria y se pierden al reiniciar.
 * En la sesión 4 esta lista se sustituye por un repositorio JPA con H2.
 */
@Service
public class SolicitudService {

	// Lista segura para varias peticiones a la vez, y contador para asignar ids
	private final List<Solicitud> solicitudes = new CopyOnWriteArrayList<>();
	private final AtomicLong siguienteId = new AtomicLong(1);

	public SolicitudService() {
		crear(new SolicitudRequest("Alta de usuario en la intranet",
				"Necesito acceso a la intranet para el nuevo compañero de administración.", "ana"));
		crear(new SolicitudRequest("Cambio de monitor",
				"El monitor del puesto 12 parpadea desde el lunes.", "luis"));
		crear(new SolicitudRequest("Licencia de software",
				"Solicito una licencia del editor de PDF para el departamento.", "ana"));
	}

	// Las más recientes primero
	public List<SolicitudResponse> listar() {
		return solicitudes.reversed().stream()
				.map(SolicitudResponse::desde)
				.toList();
	}

	// Optional: puede que exista o no. El controlador decide qué responder si no existe.
	public Optional<SolicitudResponse> obtener(Long id) {
		return solicitudes.stream()
				.filter(solicitud -> solicitud.getId().equals(id))
				.findFirst()
				.map(SolicitudResponse::desde);
	}

	public SolicitudResponse crear(SolicitudRequest datos) {
		Solicitud nueva = new Solicitud(datos.titulo(), datos.descripcion(), datos.solicitante());
		nueva.setId(siguienteId.getAndIncrement());
		solicitudes.add(nueva);
		return SolicitudResponse.desde(nueva);
	}

}
