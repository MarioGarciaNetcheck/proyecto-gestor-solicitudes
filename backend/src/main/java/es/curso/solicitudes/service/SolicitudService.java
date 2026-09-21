package es.curso.solicitudes.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.curso.solicitudes.dto.SolicitudRequest;
import es.curso.solicitudes.dto.SolicitudResponse;
import es.curso.solicitudes.error.AccesoDenegadoException;
import es.curso.solicitudes.error.SolicitudNoEncontradaException;
import es.curso.solicitudes.model.EstadoSolicitud;
import es.curso.solicitudes.model.Solicitud;
import es.curso.solicitudes.repository.SolicitudRepository;

/**
 * Lógica de negocio. El controlador no accede al repositorio directamente.
 * Reglas de autorización por recurso:
 * - Un USUARIO solo ve sus propias solicitudes.
 * - Un ADMIN ve todas y puede cambiar su estado.
 */
@Service
public class SolicitudService {

	private final SolicitudRepository repositorio;

	public SolicitudService(SolicitudRepository repositorio) {
		this.repositorio = repositorio;
	}

	@Transactional(readOnly = true)
	public List<SolicitudResponse> listar(String usuario, boolean esAdmin) {
		List<Solicitud> solicitudes = esAdmin
				? repositorio.findAllByOrderByFechaCreacionDesc()
				: repositorio.findBySolicitanteOrderByFechaCreacionDesc(usuario);
		return solicitudes.stream()
				.map(SolicitudResponse::desde)
				.toList();
	}

	@Transactional(readOnly = true)
	public SolicitudResponse obtener(Long id, String usuario, boolean esAdmin) {
		Solicitud solicitud = buscar(id);
		// Sin esta comprobación, cualquiera podría ver solicitudes ajenas cambiando el id de la URL (IDOR)
		if (!esAdmin && !solicitud.getSolicitante().equals(usuario)) {
			throw new AccesoDenegadoException("No tiene permiso para ver esta solicitud");
		}
		return SolicitudResponse.desde(solicitud);
	}

	@Transactional
	public SolicitudResponse crear(SolicitudRequest datos, String usuario) {
		Solicitud nueva = new Solicitud(
				datos.titulo().trim(),
				datos.descripcion(),
				usuario);
		return SolicitudResponse.desde(repositorio.save(nueva));
	}

	// Solo ADMIN: la regla está en SeguridadConfig (PATCH /api/solicitudes/*/estado)
	@Transactional
	public SolicitudResponse cambiarEstado(Long id, EstadoSolicitud estado) {
		Solicitud solicitud = buscar(id);
		solicitud.setEstado(estado);
		return SolicitudResponse.desde(solicitud);
	}

	private Solicitud buscar(Long id) {
		return repositorio.findById(id)
				.orElseThrow(() -> new SolicitudNoEncontradaException(id));
	}

}
