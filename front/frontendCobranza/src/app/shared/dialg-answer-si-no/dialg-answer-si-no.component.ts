import { Component, OnInit, Inject } from '@angular/core';
import { MatLegacyDialogRef as MatDialogRef, MAT_LEGACY_DIALOG_DATA as MAT_DIALOG_DATA } from '@angular/material/legacy-dialog';
export interface DialogData {
  message: string;
  title: string;
  respuesta: boolean;
}
@Component({
  selector: 'app-dialg-answer-si-no',
  templateUrl: './dialg-answer-si-no.component.html',
  styleUrls: ['./dialg-answer-si-no.component.css']
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
