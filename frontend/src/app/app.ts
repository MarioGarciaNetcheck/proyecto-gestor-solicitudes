import { Component, signal } from '@angular/core';
import { RouterOutlet } from '@angular/router';

@Component({
  selector: 'app-root',
  templateUrl: './app.html',
})
export class App {
  saludo = signal('');

  async saludar(nombre: string) {
    const r = await fetch('http://localhost:8080/api/hola?nombre=' + nombre);
    const datos = await r.json();
    this.saludo.set(datos.mensaje);
  }
}
