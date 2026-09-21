package es.curso.solicitudes.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import es.curso.solicitudes.model.Solicitud;

/**
 * Spring Data genera la implementación: no hay que escribir SQL.
 * Las consultas usan parámetros, lo que evita la inyección SQL.
 */
public interface SolicitudRepository extends JpaRepository<Solicitud, Long> {

	List<Solicitud> findAllByOrderByFechaCreacionDesc();

	List<Solicitud> findBySolicitanteOrderByFechaCreacionDesc(String solicitante);

}
