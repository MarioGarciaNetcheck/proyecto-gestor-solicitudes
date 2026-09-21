package es.curso.solicitudes.dto;

import es.curso.solicitudes.model.EstadoSolicitud;
import jakarta.validation.constraints.NotNull;

public record CambioEstadoRequest(@NotNull(message = "El estado es obligatorio") EstadoSolicitud estado) {
}
