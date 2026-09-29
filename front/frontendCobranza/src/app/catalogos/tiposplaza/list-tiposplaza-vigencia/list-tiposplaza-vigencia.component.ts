import { Component, OnInit, Inject } from '@angular/core';
import { TipoPlaza } from '../model/tipo-plaza';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { TiposplazaService } from '../service/tiposplaza.service';
import { TipoPlazaVigencia } from '../model/tipo-plaza-vigencia';
import { AddTiposplazaVigenciaComponent } from '../add-tiposplaza-vigencia/add-tiposplaza-vigencia.component';
import { DialgAnswerSiNoComponent } from 'src/app/shared/dialg-answer-si-no/dialg-answer-si-no.component';

export interface DialogData {
  tipoPlaza: TipoPlaza;
  service: TiposplazaService;
}
@Component({
  selector: 'app-list-tiposplaza-vigencia',
  templateUrl: './list-tiposplaza-vigencia.component.html',
  styleUrls: ['./list-tiposplaza-vigencia.component.css']
})
export class ListTiposplazaVigenciaComponent implements OnInit {
  tipoPlaza: TipoPlaza;
  service: TiposplazaService;
  vigencias: TipoPlazaVigencia[];
  displayOrNot: boolean = true;
  isLoading: boolean = false;
  constructor(private dialogRef: MatDialogRef<ListTiposplazaVigenciaComponent>,
    @Inject(MAT_DIALOG_DATA) public dd: DialogData, private dialogAdd: MatDialog) {
    this.service = dd.service;
    this.tipoPlaza = dd.tipoPlaza;
  }

  ngOnInit() {
    this.getTipoPlazaVigencias();
  }
  getTipoPlazaVigencias() {
    this.isLoading = true;
    this.service.getTipoPlazaVigencias(this.tipoPlaza.codigoTipoPlaza)
      .subscribe((data: TipoPlazaVigencia[]) => {
        this.vigencias = data;
        if (this.vigencias.length > 0) {
          this.displayOrNot = false;
          console.log('all data gets' + this.vigencias.length);
        }
        else
          this.displayOrNot = true;
      },
        (error: any) => console.log(error), //(2) second argument
        () => { console.log('all data gets'); this.isLoading = false; } //(3) second argument


      );
  }

  openDialog(): void {
    console.log(this.tipoPlaza.codigoTipoPlaza);
    let dialogRef = this.dialogAdd.open(AddTiposplazaVigenciaComponent, {
      data: {

        tipoPlaza: this.tipoPlaza
      }
    });



    dialogRef.afterClosed().subscribe(result => {
      this.getTipoPlazaVigencias();
      console.log('The dialog was closed');
    });
  }

  openDialogEdit(o: TipoPlazaVigencia): void {
    //console.log(op.numeroPresidenciaEmpleado);
    let dialogRef = this.dialogAdd.open(AddTiposplazaVigenciaComponent, {
      data: {
        tipoPlazaVigencia: o,
        tipoPlaza: this.tipoPlaza
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      this.getTipoPlazaVigencias();
      console.log('The dialog was closed');
    });
  }

  deleteO(idTipoPlazaVigencia: number) {
    var respuesta: Boolean = false;
    const dConfig = new MatDialogConfig();
    dConfig.data = {
      title: "Eliminando",
      message: '¿Está seguro de eliminar el registro?',
      respuesta: respuesta
    }
    const dialogAnswer = this.dialogAdd.open(DialgAnswerSiNoComponent, dConfig);


    dialogAnswer.afterClosed().subscribe(data => {
      if (data == undefined) {
        respuesta = false;
      }
      else {
        respuesta = data.respuesta;
      }

      if (respuesta) {
        this.service.deleteTipoPlazaVigencia(idTipoPlazaVigencia)
          .subscribe(
            (res: any) => this.getTipoPlazaVigencias(), //(1)
            (error: any) => console.log(error), //(2)
            () => console.log('orale') //(3)
          );

      }

    });


  }

}
