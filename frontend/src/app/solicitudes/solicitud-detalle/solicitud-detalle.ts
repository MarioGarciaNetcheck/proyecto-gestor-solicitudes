import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { AuthService } from '../../auth/auth.service';
import { ESTADOS, EstadoSolicitud, Solicitud, TEXTO_ESTADO } from '../solicitud.model';
import { SolicitudService } from '../solicitud.service';

@Component({
  imports: [DatePipe, RouterLink],
  selector: 'app-solicitud-detalle',
  styleUrl: './solicitud-detalle.css',
  templateUrl: './solicitud-detalle.html',
})
export class SolicitudDetalle implements OnInit {
  private readonly servicio = inject(SolicitudService);
  private readonly ruta = inject(ActivatedRoute);
  protected readonly auth = inject(AuthService);

  protected readonly solicitud = signal<Solicitud | null>(null);
  protected readonly error = signal('');

  protected readonly textoEstado = TEXTO_ESTADO;
  protected readonly estados = ESTADOS;

  ngOnInit(): void {
    // El id llega en la URL: /solicitudes/5
    const id = Number(this.ruta.snapshot.paramMap.get('id'));

    this.servicio.obtener(id).subscribe({
      next: (datos) => this.solicitud.set(datos),
      error: (respuesta: HttpErrorResponse) => this.error.set(this.mensajeError(respuesta)),
    });
  }

  // Solo se muestra a ADMIN, pero el servidor lo comprueba igualmente (403 para el resto)
  protected cambiarEstado(estado: string): void {
    const actual = this.solicitud();
    if (!actual) {
      return;
    }
    this.servicio.cambiarEstado(actual.id, estado as EstadoSolicitud).subscribe({
      next: (datos) => this.solicitud.set(datos),
      error: (respuesta: HttpErrorResponse) => this.error.set(this.mensajeError(respuesta)),
    });
  }

  private mensajeError(respuesta: HttpErrorResponse): string {
    switch (respuesta.status) {
      case 401:
        return 'Debe iniciar sesión.';
      case 403:
        return 'No tiene permiso para realizar esta acción.';
      case 404:
        return 'La solicitud no existe.';
      default:
        return 'No se ha podido completar la operación.';
    }
  }
}
