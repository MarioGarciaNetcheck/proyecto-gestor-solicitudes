import { Routes } from '@angular/router';

import { SolicitudAlta } from './solicitudes/solicitud-alta/solicitud-alta';
import { SolicitudDetalle } from './solicitudes/solicitud-detalle/solicitud-detalle';
import { SolicitudLista } from './solicitudes/solicitud-lista/solicitud-lista';

// Cada ruta asocia una URL del navegador con un componente.
// El orden importa: 'solicitudes/nueva' debe ir antes que 'solicitudes/:id'.
export const routes: Routes = [
  { path: '', redirectTo: 'solicitudes', pathMatch: 'full' },
  { path: 'solicitudes', component: SolicitudLista },
  { path: 'solicitudes/nueva', component: SolicitudAlta },
  { path: 'solicitudes/:id', component: SolicitudDetalle },
  { path: '**', redirectTo: 'solicitudes' },
];
