package es.curso.solicitudes.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import es.curso.solicitudes.dto.SolicitudRequest;
import es.curso.solicitudes.dto.SolicitudResponse;
import es.curso.solicitudes.error.AccesoDenegadoException;
import es.curso.solicitudes.error.SolicitudNoEncontradaException;
import es.curso.solicitudes.model.EstadoSolicitud;
import es.curso.solicitudes.model.Solicitud;
import es.curso.solicitudes.repository.SolicitudRepository;

/**
 * Prueba unitaria: se prueba el servicio aislado, sustituyendo el repositorio por un simulador (mock).
 * No arranca Spring ni la base de datos.
 */
@ExtendWith(MockitoExtension.class)
class SolicitudServiceTest {

	@Mock
	private SolicitudRepository repositorio;

	@InjectMocks
	private SolicitudService servicio;

	@Test
	void crearDejaLaSolicitudPendienteYAsignadaAlUsuario() {
		// Preparar: el repositorio devuelve lo mismo que recibe
		when(repositorio.save(any(Solicitud.class))).thenAnswer(invocacion -> invocacion.getArgument(0));
		SolicitudRequest datos = new SolicitudRequest("  Cambio de monitor  ", "Parpadea");

		// Ejecutar
		SolicitudResponse creada = servicio.crear(datos, "luis");

		// Comprobar
		assertThat(creada.titulo()).isEqualTo("Cambio de monitor");
		assertThat(creada.solicitante()).isEqualTo("luis");
		assertThat(creada.estado()).isEqualTo(EstadoSolicitud.PENDIENTE);
		assertThat(creada.fechaCreacion()).isNotNull();
	}

	@Test
	void obtenerUnaSolicitudInexistenteLanzaExcepcion() {
		when(repositorio.findById(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> servicio.obtener(99L, "ana", false))
				.isInstanceOf(SolicitudNoEncontradaException.class);
	}

	@Test
	void unUsuarioNoPuedeVerSolicitudesAjenas() {
		Solicitud deAna = new Solicitud("Cambio de monitor", null, "ana");
		when(repositorio.findById(1L)).thenReturn(Optional.of(deAna));

		assertThatThrownBy(() -> servicio.obtener(1L, "luis", false))
				.isInstanceOf(AccesoDenegadoException.class);
	}

	@Test
	void unAdminPuedeVerCualquierSolicitud() {
		Solicitud deAna = new Solicitud("Cambio de monitor", null, "ana");
		when(repositorio.findById(1L)).thenReturn(Optional.of(deAna));

		assertThat(servicio.obtener(1L, "admin", true).solicitante()).isEqualTo("ana");
	}

}
