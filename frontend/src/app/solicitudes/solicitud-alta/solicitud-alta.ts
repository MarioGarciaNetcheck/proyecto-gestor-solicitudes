import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router, RouterLink } from '@angular/router';

import { SolicitudService } from '../solicitud.service';

@Component({
  imports: [ReactiveFormsModule, RouterLink],
  selector: 'app-solicitud-alta',
  styleUrl: './solicitud-alta.css',
  templateUrl: './solicitud-alta.html',
})
export class SolicitudAlta {
  private readonly servicio = inject(SolicitudService);
  private readonly router = inject(Router);

  // Formulario reactivo: las reglas replican las del servidor para avisar antes de enviar.
  // Aun así, quien decide es el servidor: estas reglas se pueden saltar (por ejemplo, con Postman).
  protected readonly formulario = new FormGroup({
    titulo: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.minLength(3), Validators.maxLength(100)],
    }),
    descripcion: new FormControl('', {
      nonNullable: true,
      validators: [Validators.maxLength(1000)],
    }),
    solicitante: new FormControl('', {
      nonNullable: true,
      validators: [Validators.required, Validators.maxLength(50)],
    }),
  });

  protected readonly enviando = signal(false);
  protected readonly erroresServidor = signal<string[]>([]);

  // Muestra el error de un campo solo cuando el usuario ya lo ha tocado
  protected campoNoValido(nombre: 'titulo' | 'descripcion' | 'solicitante'): boolean {
    const campo = this.formulario.controls[nombre];
    return campo.invalid && campo.touched;
  }

  protected guardar(): void {
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }

    this.enviando.set(true);
    this.erroresServidor.set([]);

    this.servicio.crear(this.formulario.getRawValue()).subscribe({
      next: () => this.router.navigate(['/solicitudes']),
      error: (respuesta: HttpErrorResponse) => {
        this.enviando.set(false);
        if (respuesta.status === 400 && respuesta.error?.errores) {
          // Errores de validación que devuelve Spring Boot: { campo: mensaje }
          this.erroresServidor.set(Object.values(respuesta.error.errores));
        } else {
          this.erroresServidor.set(['No se ha podido guardar la solicitud. Inténtelo de nuevo.']);
        }
      },
    });
  }
}
