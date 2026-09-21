import { Component } from '@angular/core';

import { SolicitudLista } from './solicitudes/solicitud-lista/solicitud-lista';

@Component({
  imports: [SolicitudLista],
  selector: 'app-root',
  styleUrl: './app.css',
  templateUrl: './app.html',
})
export class App {
  protected readonly titulo = 'Gestor de solicitudes';
}
