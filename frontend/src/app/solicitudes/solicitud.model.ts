// Modelos TypeScript: describen la forma de los datos de una solicitud.

export type EstadoSolicitud = 'PENDIENTE' | 'EN_CURSO' | 'RESUELTA';

// Coincide con el JSON que devuelve la API (SolicitudResponse en Spring Boot)
export interface Solicitud {
  id: number;
  titulo: string;
  descripcion: string | null;
  solicitante: string;
  estado: EstadoSolicitud;
  fechaCreacion: string;
}

// Texto que se muestra al usuario para cada estado
export const TEXTO_ESTADO: Record<EstadoSolicitud, string> = {
  PENDIENTE: 'Pendiente',
  EN_CURSO: 'En curso',
  RESUELTA: 'Resuelta',
};
