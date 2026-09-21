import { HttpErrorResponse } from '@angular/common/http';
import { Component, inject, signal } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { Router } from '@angular/router';

import { AuthService } from '../auth.service';

@Component({
  imports: [ReactiveFormsModule],
  selector: 'app-login',
  styleUrl: './login.css',
  templateUrl: './login.html',
})
export class Login {
  private readonly auth = inject(AuthService);
  private readonly router = inject(Router);

  protected readonly formulario = new FormGroup({
    usuario: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
    contrasena: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
  });

  protected readonly error = signal('');

  protected entrar(): void {
    if (this.formulario.invalid) {
      this.formulario.markAllAsTouched();
      return;
    }

    const { usuario, contrasena } = this.formulario.getRawValue();
    this.auth.login(usuario, contrasena).subscribe({
      next: () => this.router.navigate(['/solicitudes']),
      error: (respuesta: HttpErrorResponse) => {
        this.error.set(
          respuesta.status === 401
            ? 'Usuario o contraseña incorrectos.'
            : 'No se ha podido iniciar sesión. ¿Está arrancada la API?',
        );
      },
    });
  }
}
