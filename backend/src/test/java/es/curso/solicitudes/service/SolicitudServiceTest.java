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
	void crearDejaLaSolicitudPendienteYSinEspaciosSobrantes() {
		// Preparar: el repositorio devuelve lo mismo que recibe
		when(repositorio.save(any(Solicitud.class))).thenAnswer(invocacion -> invocacion.getArgument(0));
		SolicitudRequest datos = new SolicitudRequest("  Cambio de monitor  ", "Parpadea", " luis ");

		// Ejecutar
		SolicitudResponse creada = servicio.crear(datos);

		// Comprobar
		assertThat(creada.titulo()).isEqualTo("Cambio de monitor");
		assertThat(creada.solicitante()).isEqualTo("luis");
		assertThat(creada.estado()).isEqualTo(EstadoSolicitud.PENDIENTE);
		assertThat(creada.fechaCreacion()).isNotNull();
	}

	@Test
	void obtenerUnaSolicitudInexistenteLanzaExcepcion() {
		when(repositorio.findById(99L)).thenReturn(Optional.empty());

		assertThatThrownBy(() -> servicio.obtener(99L))
				.isInstanceOf(SolicitudNoEncontradaException.class);
	}

}
