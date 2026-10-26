import { Component, OnInit, Inject, ChangeDetectionStrategy } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
export interface DialogData {
  message: string;
  title: string;
  respuesta: boolean;
  /** Texto del botón que confirma (por omisión "Sí") */
  textoAceptar?: string;
  /** Texto del botón que cancela (por omisión "Cancelar") */
  textoCancelar?: string;
  /** Acción destructiva: el botón de confirmar va en color de error */
  destructivo?: boolean;
}
@Component({
    selector: 'app-dialg-answer-si-no',
    templateUrl: './dialg-answer-si-no.component.html',
    styleUrls: ['./dialg-answer-si-no.component.css'],
    changeDetection: ChangeDetectionStrategy.Eager,
    standalone: false
})
export class DialgAnswerSiNoComponent implements OnInit {
  message: string;
  title: string;
  respuesta: boolean;
  textoAceptar: string;
  textoCancelar: string;
  destructivo: boolean;
  constructor(private dialogRef: MatDialogRef<DialgAnswerSiNoComponent>,
    @Inject(MAT_DIALOG_DATA) dd: DialogData) {
    this.message = dd.message;
    this.respuesta = dd.respuesta;
    this.title = dd.title;
    this.textoAceptar = dd.textoAceptar || 'Sí';
    this.textoCancelar = dd.textoCancelar || 'Cancelar';
    this.destructivo = dd.destructivo === true;
  }

  ngOnInit() {
  }

  cancelar() {
    this.respuesta = false;
    this.dialogRef.close({respuesta:this.respuesta});
  }
  aceptar() {
    this.respuesta = true;
    this.dialogRef.close({respuesta:this.respuesta});
  }

}
