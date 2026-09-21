package es.curso.solicitudes.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/**
 * Datos que el cliente envía para dar de alta una solicitud (POST).
 * Las anotaciones se comprueban en el servidor gracias a @Valid en el controlador.
 * El solicitante no se recibe del cliente: se toma del usuario autenticado (token).
 */
public record SolicitudRequest(

		@NotBlank(message = "El título es obligatorio")
		@Size(min = 3, max = 100, message = "El título debe tener entre 3 y 100 caracteres")
		String titulo,

		@Size(max = 1000, message = "La descripción no puede superar los 1000 caracteres")
		String descripcion) {
}
