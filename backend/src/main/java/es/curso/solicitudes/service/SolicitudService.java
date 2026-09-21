package es.curso.solicitudes.service;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import es.curso.solicitudes.dto.SolicitudRequest;
import es.curso.solicitudes.dto.SolicitudResponse;
import es.curso.solicitudes.error.SolicitudNoEncontradaException;
import es.curso.solicitudes.model.Solicitud;
import es.curso.solicitudes.repository.SolicitudRepository;

/**
 * Lógica de negocio. El controlador no accede al repositorio directamente.
 */
@Service
public class SolicitudService {

	private final SolicitudRepository repositorio;

	public SolicitudService(SolicitudRepository repositorio) {
		this.repositorio = repositorio;
	}

	@Transactional(readOnly = true)
	public List<SolicitudResponse> listar() {
		return repositorio.findAllByOrderByFechaCreacionDesc().stream()
				.map(SolicitudResponse::desde)
				.toList();
	}

	@Transactional(readOnly = true)
	public SolicitudResponse obtener(Long id) {
		Solicitud solicitud = repositorio.findById(id)
				.orElseThrow(() -> new SolicitudNoEncontradaException(id));
		return SolicitudResponse.desde(solicitud);
	}

	@Transactional
	public SolicitudResponse crear(SolicitudRequest datos) {
		Solicitud nueva = new Solicitud(
				datos.titulo(),
				datos.descripcion(),
				datos.solicitante());
		return SolicitudResponse.desde(repositorio.save(nueva));
	}

}
