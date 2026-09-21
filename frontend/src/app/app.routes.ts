import { Routes } from '@angular/router';

import { SolicitudDetalle } from './solicitudes/solicitud-detalle/solicitud-detalle';
import { SolicitudLista } from './solicitudes/solicitud-lista/solicitud-lista';

// Cada ruta asocia una URL del navegador con un componente.
export const routes: Routes = [
  { path: '', redirectTo: 'solicitudes', pathMatch: 'full' },
  { path: 'solicitudes', component: SolicitudLista },
  { path: 'solicitudes/:id', component: SolicitudDetalle },
  { path: '**', redirectTo: 'solicitudes' },
];
