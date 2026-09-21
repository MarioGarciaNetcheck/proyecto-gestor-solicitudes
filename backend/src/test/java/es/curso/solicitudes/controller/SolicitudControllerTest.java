package es.curso.solicitudes.controller;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import es.curso.solicitudes.service.SolicitudService;

/**
 * Comprueba que el servidor rechaza datos no válidos aunque el cliente (Angular, Postman...) los envíe.
 */
@WebMvcTest(SolicitudController.class)
class SolicitudControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private SolicitudService servicio;

	@Test
	void altaConTituloVacioDevuelve400() throws Exception {
		String json = """
				{ "titulo": "", "descripcion": "Sin título", "solicitante": "ana" }
				""";

		mockMvc.perform(post("/api/solicitudes").contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errores.titulo").value("El título es obligatorio"));

		verifyNoInteractions(servicio);
	}

}
