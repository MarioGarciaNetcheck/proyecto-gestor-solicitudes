package es.curso.solicitudes.model;

import java.time.LocalDateTime;

/**
 * Una solicitud del Gestor. De momento es una clase Java normal que vive en memoria.
 * En la sesión 4 se convertirá en una entidad JPA guardada en H2.
 */
public class Solicitud {

	private Long id;
	private String titulo;
	private String descripcion;
	private String solicitante;
	private EstadoSolicitud estado;
	private LocalDateTime fechaCreacion;

	public Solicitud(String titulo, String descripcion, String solicitante) {
		this.titulo = titulo;
		this.descripcion = descripcion;
		this.solicitante = solicitante;
		this.estado = EstadoSolicitud.PENDIENTE;
		this.fechaCreacion = LocalDateTime.now();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getTitulo() {
		return titulo;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public String getSolicitante() {
		return solicitante;
	}

	public EstadoSolicitud getEstado() {
		return estado;
	}

	public void setEstado(EstadoSolicitud estado) {
		this.estado = estado;
	}

	public LocalDateTime getFechaCreacion() {
		return fechaCreacion;
	}

}
