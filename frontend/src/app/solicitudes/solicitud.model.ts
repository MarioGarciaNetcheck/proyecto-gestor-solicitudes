// Modelos TypeScript: describen la forma del JSON que intercambiamos con la API.

export type EstadoSolicitud = 'PENDIENTE' | 'EN_CURSO' | 'RESUELTA';

// Lo que devuelve la API (coincide con SolicitudResponse en Spring Boot)
export interface Solicitud {
  id: number;
  titulo: string;
  descripcion: string | null;
  solicitante: string;
  estado: EstadoSolicitud;
  fechaCreacion: string;
}

// Lo que enviamos para dar de alta (coincide con SolicitudRequest en Spring Boot)
export interface NuevaSolicitud {
  titulo: string;
  descripcion: string;
  solicitante: string;
}

// Texto que se muestra al usuario para cada estado
export const TEXTO_ESTADO: Record<EstadoSolicitud, string> = {
  PENDIENTE: 'Pendiente',
  EN_CURSO: 'En curso',
  RESUELTA: 'Resuelta',
};
