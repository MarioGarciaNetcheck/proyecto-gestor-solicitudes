import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { AuthService } from '../auth/auth.service';
import { EstadoSolicitud, NuevaSolicitud, Solicitud } from './solicitud.model';

/**
 * Servicio: concentra las llamadas HTTP a la API de solicitudes.
 * Los componentes no usan HttpClient directamente.
 */
@Injectable({ providedIn: 'root' })
export class SolicitudService {
  private readonly http = inject(HttpClient);
  private readonly auth = inject(AuthService);

  // Ruta relativa: en desarrollo, proxy.conf.json la redirige a http://localhost:8080
  private readonly url = '/api/solicitudes';

  listar(): Observable<Solicitud[]> {
    return this.http.get<Solicitud[]>(this.url, { headers: this.auth.cabeceras() });
  }

  obtener(id: number): Observable<Solicitud> {
    return this.http.get<Solicitud>(`${this.url}/${id}`, { headers: this.auth.cabeceras() });
  }

  crear(datos: NuevaSolicitud): Observable<Solicitud> {
    return this.http.post<Solicitud>(this.url, datos, { headers: this.auth.cabeceras() });
  }

  cambiarEstado(id: number, estado: EstadoSolicitud): Observable<Solicitud> {
    return this.http.patch<Solicitud>(
      `${this.url}/${id}/estado`,
      { estado },
      { headers: this.auth.cabeceras() },
    );
  }
}
