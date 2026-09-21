package es.curso.solicitudes.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Primer endpoint del curso: comprueba que la API arranca y responde JSON.
 */
@RestController
public class SaludoController {

	@GetMapping("/api/saludo")
	public Map<String, String> saludar() {
		return Map.of("mensaje", "Hola desde el Gestor de solicitudes");
	}

}
