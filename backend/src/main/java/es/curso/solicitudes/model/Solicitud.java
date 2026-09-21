package es.curso.solicitudes.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Entidad JPA: representa una fila de la tabla SOLICITUDES.
 * No se devuelve directamente al cliente; para eso están los DTO.
 */
@Entity
@Table(name = "solicitudes")
public class Solicitud {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, length = 100)
	private String titulo;

	@Column(length = 1000)
	private String descripcion;

	@Column(nullable = false, length = 50)
	private String solicitante;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 20)
	private EstadoSolicitud estado;

	@Column(nullable = false)
	private LocalDateTime fechaCreacion;

	// JPA necesita un constructor sin argumentos.
	protected Solicitud() {
	}

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
