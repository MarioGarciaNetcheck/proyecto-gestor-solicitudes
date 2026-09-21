package es.curso.solicitudes.error;

public class SolicitudNoEncontradaException extends RuntimeException {

	public SolicitudNoEncontradaException(Long id) {
		super("No existe la solicitud con id " + id);
	}

}
