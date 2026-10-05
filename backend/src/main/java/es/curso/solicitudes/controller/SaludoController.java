package es.curso.solicitudes.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

/**
 * Primer endpoint del curso: comprueba que la API arranca y responde JSON.
 */
@RestController
public class SaludoController {

	@GetMapping("/api/saludo")
	public Map<String, String> saludar() {
		return Map.of("mensaje", "MARIO");
	}

	@GetMapping("/api/ficha")
	public List <String> ficha () {
		return List.of("nombre", "apellido", "telefono");
	}

	@CrossOrigin("http://localhost:4200")
	@GetMapping("/api/hola")
	public Map<String, String> hola (@RequestParam String nombre) {
		return Map.of("mensaje", "Hola " + nombre);
	}

}
