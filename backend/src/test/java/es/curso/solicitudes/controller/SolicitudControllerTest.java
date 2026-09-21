package es.curso.solicitudes.controller;

import static org.mockito.Mockito.verifyNoInteractions;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.jwt;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import es.curso.solicitudes.config.SeguridadConfig;
import es.curso.solicitudes.service.SolicitudService;

/**
 * Comprueba en el servidor la validación y los permisos, independientemente del cliente que llame.
 */
@WebMvcTest(SolicitudController.class)
@Import(SeguridadConfig.class)
class SolicitudControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@MockitoBean
	private SolicitudService servicio;

	@Test
	void sinTokenDevuelve401() throws Exception {
		mockMvc.perform(get("/api/solicitudes"))
				.andExpect(status().isUnauthorized());
	}

	@Test
	void altaConTituloVacioDevuelve400() throws Exception {
		String json = """
				{ "titulo": "", "descripcion": "Sin título" }
				""";

		mockMvc.perform(post("/api/solicitudes")
				.with(jwt().jwt(token -> token.subject("ana")).authorities(new SimpleGrantedAuthority("ROLE_USUARIO")))
				.contentType(MediaType.APPLICATION_JSON).content(json))
				.andExpect(status().isBadRequest())
				.andExpect(jsonPath("$.errores.titulo").value("El título es obligatorio"));

		verifyNoInteractions(servicio);
	}

	@Test
	void unUsuarioNoPuedeCambiarElEstado() throws Exception {
		mockMvc.perform(patch("/api/solicitudes/1/estado")
				.with(jwt().jwt(token -> token.subject("ana")).authorities(new SimpleGrantedAuthority("ROLE_USUARIO")))
				.contentType(MediaType.APPLICATION_JSON).content("{ \"estado\": \"RESUELTA\" }"))
				.andExpect(status().isForbidden());

		verifyNoInteractions(servicio);
	}

}
