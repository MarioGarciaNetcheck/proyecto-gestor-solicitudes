import { DatePipe } from '@angular/common';
import { HttpErrorResponse } from '@angular/common/http';
import { Component, OnInit, inject, signal } from '@angular/core';
import { RouterLink } from '@angular/router';

import { Solicitud, TEXTO_ESTADO } from '../solicitud.model';
import { SolicitudService } from '../solicitud.service';

@Component({
  imports: [DatePipe, RouterLink],
  selector: 'app-solicitud-lista',
  styleUrl: './solicitud-lista.css',
  templateUrl: './solicitud-lista.html',
})
export class SolicitudLista implements OnInit {
  private readonly servicio = inject(SolicitudService);

  // signal(): valor que, al cambiar con set(), actualiza la pantalla.
  // En la plantilla se lee llamándolo como una función: solicitudes()
  protected readonly solicitudes = signal<Solicitud[]>([]);
  protected readonly cargando = signal(true);
  protected readonly error = signal('');

  protected readonly textoEstado = TEXTO_ESTADO;

  ngOnInit(): void {
    this.servicio.listar().subscribe({
      next: (datos) => {
        this.solicitudes.set(datos);
        this.cargando.set(false);
      },
      error: (respuesta: HttpErrorResponse) => {
        this.error.set(
          respuesta.status === 401
            ? 'Debe iniciar sesión para ver las solicitudes.'
            : 'No se han podido cargar las solicitudes. ¿Está arrancada la API?',
        );
        this.cargando.set(false);
      },
    });
  }
}
