package es.curso.solicitudes.error;

import java.util.Map;
import java.util.TreeMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ProblemDetail;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 * Tratamiento centralizado de errores.
 * Todas las respuestas de error siguen el formato estándar ProblemDetail (RFC 9457)
 * y nunca incluyen trazas ni detalles internos.
 */
@RestControllerAdvice
public class GestorErroresApi extends ResponseEntityExceptionHandler {

	private static final Logger log = LoggerFactory.getLogger(GestorErroresApi.class);

	// 404: la solicitud pedida no existe
	@ExceptionHandler(SolicitudNoEncontradaException.class)
	public ProblemDetail noEncontrada(SolicitudNoEncontradaException ex) {
		ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.NOT_FOUND, ex.getMessage());
		problema.setTitle("Solicitud no encontrada");
		return problema;
	}

	// 403: el usuario está autenticado pero el recurso no es suyo
	@ExceptionHandler(AccesoDenegadoException.class)
	public ProblemDetail accesoDenegado(AccesoDenegadoException ex) {
		ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.FORBIDDEN, ex.getMessage());
		problema.setTitle("Acceso denegado");
		return problema;
	}

	// 401: usuario o contraseña incorrectos en el login.
	// El mensaje no indica cuál de los dos ha fallado, para no dar pistas a un atacante.
	@ExceptionHandler(AuthenticationException.class)
	public ProblemDetail loginIncorrecto(AuthenticationException ex) {
		ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.UNAUTHORIZED,
				"Usuario o contraseña incorrectos");
		problema.setTitle("No autenticado");
		return problema;
	}

	// 400: datos que no cumplen las reglas de validación (@NotBlank, @Size...)
	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatusCode status, WebRequest request) {
		// Un mensaje por campo, ordenados por nombre para que la respuesta sea siempre igual.
		// Si un campo incumple varias reglas, se muestra la de "obligatorio".
		Map<String, String> errores = new TreeMap<>();
		for (FieldError error : ex.getBindingResult().getFieldErrors()) {
			boolean esObligatorio = "NotBlank".equals(error.getCode()) || "NotNull".equals(error.getCode());
			if (esObligatorio) {
				errores.put(error.getField(), error.getDefaultMessage());
			} else {
				errores.putIfAbsent(error.getField(), error.getDefaultMessage());
			}
		}

		ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.BAD_REQUEST,
				"Los datos enviados no son válidos");
		problema.setTitle("Error de validación");
		problema.setProperty("errores", errores);
		return ResponseEntity.badRequest().body(problema);
	}

	// 500: cualquier error no previsto. El detalle va al log del servidor, no al cliente.
	@ExceptionHandler(Exception.class)
	public ProblemDetail errorInesperado(Exception ex) {
		log.error("Error no controlado", ex);
		ProblemDetail problema = ProblemDetail.forStatusAndDetail(HttpStatus.INTERNAL_SERVER_ERROR,
				"Se ha producido un error interno. Inténtelo más tarde.");
		problema.setTitle("Error interno");
		return problema;
	}

}
