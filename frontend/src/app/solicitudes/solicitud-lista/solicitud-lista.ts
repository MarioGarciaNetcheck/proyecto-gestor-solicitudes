import { DatePipe } from '@angular/common';
import { Component, signal } from '@angular/core';

import { Solicitud, TEXTO_ESTADO } from '../solicitud.model';

@Component({
  imports: [DatePipe],
  selector: 'app-solicitud-lista',
  styleUrl: './solicitud-lista.css',
  templateUrl: './solicitud-lista.html',
})
export class SolicitudLista {
  // signal(): valor que, al cambiar, actualiza la pantalla.
  // En la plantilla se lee llamándolo como una función: solicitudes()
  // De momento son datos escritos a mano; en la sesión 7 llegarán de la API.
  protected readonly solicitudes = signal<Solicitud[]>([
    {
      id: 1,
      titulo: 'Alta de usuario en la intranet',
      descripcion: 'Necesito acceso a la intranet para el nuevo compañero de administración.',
      solicitante: 'ana',
      estado: 'PENDIENTE',
      fechaCreacion: '2026-10-05T09:15:00',
    },
    {
      id: 2,
      titulo: 'Cambio de monitor',
      descripcion: 'El monitor del puesto 12 parpadea desde el lunes.',
      solicitante: 'luis',
      estado: 'EN_CURSO',
      fechaCreacion: '2026-10-05T10:30:00',
    },
  ]);

  protected readonly textoEstado = TEXTO_ESTADO;

  // Evento (click): añade una solicitud de ejemplo a la lista
  protected anadirEjemplo(): void {
    const siguienteId = this.solicitudes().length + 1;
    const nueva: Solicitud = {
      id: siguienteId,
      titulo: `Solicitud de ejemplo ${siguienteId}`,
      descripcion: null,
      solicitante: 'ana',
      estado: 'PENDIENTE',
      fechaCreacion: new Date().toISOString(),
    };
    // update(): calcula el nuevo valor a partir del anterior
    this.solicitudes.update((lista) => [...lista, nueva]);
  }

  // Evento (click): vacía la lista para ver el bloque @empty
  protected vaciar(): void {
    this.solicitudes.set([]);
  }
}
