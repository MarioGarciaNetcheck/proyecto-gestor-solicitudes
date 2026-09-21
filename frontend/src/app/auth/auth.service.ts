import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable, inject, signal } from '@angular/core';
import { Observable, tap } from 'rxjs';

// Respuesta de POST /api/auth/login (coincide con LoginResponse en Spring Boot)
export interface Sesion {
  token: string;
  usuario: string;
  roles: string[];
  expira: string;
}

const CLAVE_ALMACEN = 'gestor-solicitudes.sesion';

/**
 * Guarda la sesión del usuario (el token JWT) y prepara la cabecera Authorization.
 *
 * Importante: ocultar botones en Angular solo mejora la experiencia.
 * Quien protege los datos es el servidor, que comprueba el token y el rol en cada petición.
 */
@Injectable({ providedIn: 'root' })
export class AuthService {
  private readonly http = inject(HttpClient);

  // sessionStorage: la sesión se mantiene al recargar la página y se pierde al cerrar la pestaña
  readonly sesion = signal<Sesion | null>(this.leerSesionGuardada());

  login(usuario: string, contrasena: string): Observable<Sesion> {
    return this.http.post<Sesion>('/api/auth/login', { usuario, contrasena }).pipe(
      tap((sesion) => {
        sessionStorage.setItem(CLAVE_ALMACEN, JSON.stringify(sesion));
        this.sesion.set(sesion);
      }),
    );
  }

  logout(): void {
    sessionStorage.removeItem(CLAVE_ALMACEN);
    this.sesion.set(null);
  }

  esAdmin(): boolean {
    return this.sesion()?.roles.includes('ADMIN') ?? false;
  }

  // Cabecera que el servidor necesita para saber quién hace la petición.
  // (En proyectos grandes esto se automatiza con un "interceptor"; aquí se hace a mano para que se vea.)
  cabeceras(): HttpHeaders {
    const token = this.sesion()?.token;
    return token ? new HttpHeaders({ Authorization: `Bearer ${token}` }) : new HttpHeaders();
  }

  private leerSesionGuardada(): Sesion | null {
    const guardada = sessionStorage.getItem(CLAVE_ALMACEN);
    if (!guardada) {
      return null;
    }
    const sesion: Sesion = JSON.parse(guardada);
    // Si el token ha caducado, se descarta
    return new Date(sesion.expira) > new Date() ? sesion : null;
  }
}
