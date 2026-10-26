import { Injectable } from '@angular/core';
import { MatSnackBar } from '@angular/material/snack-bar';

/** Avisos breves (MatSnackBar): éxito en verde, error en rojo. */
@Injectable({
  providedIn: 'root'
})
export class AvisoService {
  constructor(private snackBar: MatSnackBar) { }

  exito(mensaje: string) {
    this.snackBar.open(mensaje, 'Cerrar', {
      duration: 4000,
      panelClass: 'aviso-exito',
      politeness: 'polite'
    });
  }

  error(mensaje: string) {
    this.snackBar.open(mensaje, 'Cerrar', {
      duration: 6000,
      panelClass: 'aviso-error',
      politeness: 'assertive'
    });
  }
}
