import { ApplicationConfig, provideBrowserGlobalErrorListeners } from '@angular/core';
import { provideHttpClient } from '@angular/common/http';
import { provideRouter } from '@angular/router';
import { routes } from './app.routes';

export const appConfig: ApplicationConfig = {
  providers: [
    provideBrowserGlobalErrorListeners(),
    provideRouter(routes),
    // Configura HttpClient para llamar a la API.
    // (En Angular 22 ya viene incluido por defecto; se deja explícito para que se vea.)
    provideHttpClient(),
  ],
};
