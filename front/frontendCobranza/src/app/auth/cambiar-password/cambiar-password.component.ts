import { Component, ChangeDetectionStrategy } from '@angular/core';
import { MatDialogRef } from '@angular/material/dialog';
import { AuthService } from '../auth.service';

@Component({
    selector: 'app-cambiar-password',
    templateUrl: './cambiar-password.component.html',
    styleUrls: ['./cambiar-password.component.css'],
    changeDetection: ChangeDetectionStrategy.Eager,
    standalone: false
})
export class CambiarPasswordComponent {
  actual = '';
  nueva = '';
  confirmacion = '';
  error = '';
  mensaje = '';

  constructor(public dialogRef: MatDialogRef<CambiarPasswordComponent>, private auth: AuthService) { }

  guardar() {
    this.mensaje = '';
    if (this.nueva != this.confirmacion) {
      this.error = 'La confirmación no coincide con la contraseña nueva';
      return;
    }
    this.auth.cambiarPassword(this.actual, this.nueva).subscribe({
      next: () => {
        this.error = '';
        this.dialogRef.close(true);
      },
      error: (error: any) => this.error = error.error?.mensaje || 'No se pudo cambiar la contraseña'
    });
  }
}
