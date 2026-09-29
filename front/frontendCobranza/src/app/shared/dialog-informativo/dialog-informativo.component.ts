import { Component, OnInit, Inject } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material';
export interface DialogData {
  message: string;
  title: string;
}

@Component({
  selector: 'app-dialog-informativo',
  templateUrl: './dialog-informativo.component.html',
  styleUrls: ['./dialog-informativo.component.css']
})
export class DialogInformativoComponent implements OnInit {
  message: string;
  title: string;
  constructor(private dialogRef: MatDialogRef<DialogInformativoComponent>,
    @Inject(MAT_DIALOG_DATA) dd: DialogData) {
    this.message = dd.message;
    this.title = dd.title;

  }

  ngOnInit() {
  }

  close() {
    this.dialogRef.close();
  }

}
