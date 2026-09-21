package es.curso.solicitudes.dto;

import java.time.LocalDateTime;

import es.curso.solicitudes.model.EstadoSolicitud;
import es.curso.solicitudes.model.Solicitud;

/**
 * Datos que la API devuelve al cliente. Aquí se decide qué campos se exponen.
 */
public record SolicitudResponse(
		Long id,
		String titulo,
		String descripcion,
		String solicitante,
		EstadoSolicitud estado,
		LocalDateTime fechaCreacion) {

	public static SolicitudResponse desde(Solicitud solicitud) {
		return new SolicitudResponse(
				solicitud.getId(),
				solicitud.getTitulo(),
				solicitud.getDescripcion(),
				solicitud.getSolicitante(),
				solicitud.getEstado(),
				solicitud.getFechaCreacion());
	}

}
