import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { ActivatedRoute, RouterLink } from '@angular/router';

import { Solicitud, TEXTO_ESTADO } from '../solicitud.model';
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

  protected readonly solicitud = signal<Solicitud | null>(null);
  protected readonly error = signal('');

  protected readonly textoEstado = TEXTO_ESTADO;

  ngOnInit(): void {
    // El id llega en la URL: /solicitudes/5
    const id = Number(this.ruta.snapshot.paramMap.get('id'));

    this.servicio.obtener(id).subscribe({
      next: (datos) => this.solicitud.set(datos),
      error: (respuesta: HttpErrorResponse) => {
        this.error.set(
          respuesta.status === 404
            ? 'La solicitud no existe.'
            : 'No se ha podido cargar la solicitud.',
        );
      },
    });
  }
}
