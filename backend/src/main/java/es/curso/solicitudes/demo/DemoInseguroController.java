package es.curso.solicitudes.demo;

import java.net.URI;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import es.curso.solicitudes.dto.SolicitudRequest;
import es.curso.solicitudes.dto.SolicitudResponse;
import es.curso.solicitudes.error.SolicitudNoEncontradaException;
import es.curso.solicitudes.model.Solicitud;
import es.curso.solicitudes.repository.SolicitudRepository;

/**
 * ¡¡CÓDIGO VULNERABLE A PROPÓSITO!! Solo para las demostraciones de la sesión 9.
 * Solo existe si se arranca con el perfil "demo-inseguro":
 *   mvnw spring-boot:run -Dspring-boot.run.profiles=demo-inseguro
 * Cada endpoint inseguro tiene su equivalente correcto en el código normal de la aplicación.
 */
@Profile("demo-inseguro")
@RestController
@RequestMapping("/api/demo")
public class DemoInseguroController {

	private static final Logger log = LoggerFactory.getLogger(DemoInseguroController.class);

	private final JdbcTemplate jdbc;
	private final SolicitudRepository repositorio;

	public DemoInseguroController(JdbcTemplate jdbc, SolicitudRepository repositorio) {
		this.jdbc = jdbc;
		this.repositorio = repositorio;
		log.warn("*** PERFIL demo-inseguro ACTIVO: hay endpoints vulnerables en /api/demo ***");
	}

	// DEMO 1a. Inyección SQL: la consulta se construye pegando texto del usuario.
	// Probar con texto:   %' OR 1=1 --
	// Resultado: devuelve las solicitudes de TODOS los usuarios, no solo las propias.
	@GetMapping("/buscar-inseguro")
	public List<Map<String, Object>> buscarInseguro(@RequestParam String texto, Authentication usuario) {
		String sql = "SELECT id, titulo, solicitante FROM solicitudes "
				+ "WHERE solicitante = '" + usuario.getName() + "' AND titulo LIKE '%" + texto + "%'";
		log.info("SQL ejecutado: {}", sql);
		return jdbc.queryForList(sql);
	}

	// DEMO 1b. La misma búsqueda con parámetros (?): el texto nunca se interpreta como SQL.
	// Con el mismo texto malicioso no devuelve nada, porque ningún título contiene esa cadena.
	@GetMapping("/buscar-seguro")
	public List<Map<String, Object>> buscarSeguro(@RequestParam String texto, Authentication usuario) {
		String sql = "SELECT id, titulo, solicitante FROM solicitudes WHERE solicitante = ? AND titulo LIKE ?";
		return jdbc.queryForList(sql, usuario.getName(), "%" + texto + "%");
	}

	// DEMO 2. IDOR: devuelve cualquier solicitud por id sin comprobar de quién es.
	// Con el token de "luis", pedir /api/demo/solicitudes/1 (que es de "ana").
	// Versión correcta: SolicitudService.obtener(), que responde 403.
	@GetMapping("/solicitudes/{id}")
	public SolicitudResponse obtenerSinComprobarPropietario(@PathVariable Long id) {
		return repositorio.findById(id)
				.map(SolicitudResponse::desde)
				.orElseThrow(() -> new SolicitudNoEncontradaException(id));
	}

	// DEMO 3. Alta sin @Valid: si el servidor confía en que Angular ya validó,
	// con Postman se puede guardar una solicitud con título vacío.
	// Versión correcta: SolicitudController.crear(), que responde 400.
	@PostMapping("/solicitudes")
	public ResponseEntity<SolicitudResponse> crearSinValidar(@RequestBody SolicitudRequest datos,
			Authentication usuario) {
		Solicitud nueva = new Solicitud(datos.titulo(), datos.descripcion(), usuario.getName());
		SolicitudResponse creada = SolicitudResponse.desde(repositorio.save(nueva));
		return ResponseEntity.created(URI.create("/api/solicitudes/" + creada.id())).body(creada);
	}

}
