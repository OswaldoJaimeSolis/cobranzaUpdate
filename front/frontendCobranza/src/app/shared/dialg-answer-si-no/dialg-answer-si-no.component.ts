import { Component, OnInit, Inject, ChangeDetectionStrategy } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
export interface DialogData {
  message: string;
  title: string;
  respuesta: boolean;
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
  constructor(private dialogRef: MatDialogRef<DialgAnswerSiNoComponent>,
    @Inject(MAT_DIALOG_DATA) dd: DialogData) {
    this.message = dd.message;
    this.respuesta = dd.respuesta;
    this.title = dd.title;

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
