package es.curso.solicitudes.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import es.curso.solicitudes.model.Solicitud;
import es.curso.solicitudes.repository.SolicitudRepository;

/**
 * Carga unas solicitudes de ejemplo la primera vez que arranca la aplicación
 * (cuando la tabla está vacía).
 */
@Component
public class DatosIniciales implements CommandLineRunner {

	private final SolicitudRepository repositorio;

	public DatosIniciales(SolicitudRepository repositorio) {
		this.repositorio = repositorio;
	}

	@Override
	public void run(String... args) {
		if (repositorio.count() > 0) {
			return;
		}
		repositorio.save(new Solicitud("Alta de usuario en la intranet",
				"Necesito acceso a la intranet para el nuevo compañero de administración.", "ana"));
		repositorio.save(new Solicitud("Cambio de monitor",
				"El monitor del puesto 12 parpadea desde el lunes.", "luis"));
		repositorio.save(new Solicitud("Licencia de software",
				"Solicito una licencia del editor de PDF para el departamento.", "ana"));
	}

}
