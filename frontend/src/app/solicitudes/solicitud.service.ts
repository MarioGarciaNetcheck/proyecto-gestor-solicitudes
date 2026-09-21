import { HttpClient } from '@angular/common/http';
import { Injectable, inject } from '@angular/core';
import { Observable } from 'rxjs';

import { NuevaSolicitud, Solicitud } from './solicitud.model';

/**
 * Servicio: concentra las llamadas HTTP a la API de solicitudes.
 * Los componentes no usan HttpClient directamente.
 */
@Injectable({ providedIn: 'root' })
export class SolicitudService {
  private readonly http = inject(HttpClient);

  // Ruta relativa: en desarrollo, proxy.conf.json la redirige a http://localhost:8080
  private readonly url = '/api/solicitudes';

  listar(): Observable<Solicitud[]> {
    return this.http.get<Solicitud[]>(this.url);
  }

  obtener(id: number): Observable<Solicitud> {
    return this.http.get<Solicitud>(`${this.url}/${id}`);
  }

  crear(datos: NuevaSolicitud): Observable<Solicitud> {
    return this.http.post<Solicitud>(this.url, datos);
  }
}
