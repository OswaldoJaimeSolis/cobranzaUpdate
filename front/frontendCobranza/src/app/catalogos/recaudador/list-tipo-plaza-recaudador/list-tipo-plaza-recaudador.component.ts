import { Component, OnInit, Inject, ChangeDetectionStrategy } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { Recaudador } from '../model/recaudador';
import { RecuadadorTipoPlaza } from '../model/recuadador-tipo-plaza';
import { RecaudadorService } from '../service/recaudador.service';
import { BusquedaTiposPlazaComponent } from 'src/app/shared/busqueda/busqueda-tipos-plaza/busqueda-tipos-plaza.component';
import { TipoPlaza } from '../../tiposplaza/model/tipo-plaza';
import { DialogInformativoComponent } from 'src/app/shared/dialog-informativo/dialog-informativo.component';
import { DialgAnswerSiNoComponent } from 'src/app/shared/dialg-answer-si-no/dialg-answer-si-no.component';
export interface BusResult {
  tipoPlazaSelect: TipoPlaza
}
export interface DialogData {
  recaudador: Recaudador;
  service: RecaudadorService;
}

@Component({
    selector: 'app-list-tipo-plaza-recaudador',
    templateUrl: './list-tipo-plaza-recaudador.component.html',
    styleUrls: ['./list-tipo-plaza-recaudador.component.css'],
    changeDetection: ChangeDetectionStrategy.Eager,
    standalone: false
})
export class ListTipoPlazaRecaudadorComponent implements OnInit {
  recaudador: Recaudador;
  tpsRecaudador: RecuadadorTipoPlaza[];
  service: RecaudadorService;
  displayOrNot: boolean = true;
  constructor(private dialogRef: MatDialogRef<ListTipoPlazaRecaudadorComponent>,
    @Inject(MAT_DIALOG_DATA) dd: DialogData, private dialogBus: MatDialog) {
    this.recaudador = dd.recaudador;
    this.service = dd.service;

  }

  ngOnInit() {
    this.getRecaudadorTP();
  }

  getRecaudadorTP() {
    this.service.getRecaudadorTP(this.recaudador.codigoRecaudador)
      .subscribe((data: RecuadadorTipoPlaza[]) => {
        this.tpsRecaudador = data;
        if (this.tpsRecaudador.length > 0) {
          this.displayOrNot = false;

        }
        else
          this.displayOrNot = true;
      },
        (error: any) => console.log(error), //(2) second argument
        () => console.log('all data gets') //(3) second argument


      );
  }

  openDialogAdd(): void {

    let dBus = this.dialogBus.open(BusquedaTiposPlazaComponent);
    let tipoPlaza: TipoPlaza;

    dBus.afterClosed().subscribe((data: BusResult) => {
      if (data == undefined) {

      }
      else {
        tipoPlaza = data.tipoPlazaSelect as TipoPlaza;
        let tt: TipoPlaza = data.tipoPlazaSelect as TipoPlaza;

      }

      if (tipoPlaza != null) {
        let agregado: boolean = false;
        //revisar si no está  agregado
        this.tpsRecaudador.forEach(tpsr => {
          if (tpsr.tipoPlaza.codigoTipoPlaza == tipoPlaza.codigoTipoPlaza)
            agregado = true;
        });
        if (!agregado) {
          let rtp: RecuadadorTipoPlaza = new RecuadadorTipoPlaza();
          rtp.recaudador = this.recaudador;
          rtp.tipoPlaza = tipoPlaza;

          this.service.addRecaudadorTP(rtp)
            .subscribe((res: any) => this.getRecaudadorTP(),
              (error: any) => console.log(error), () => console.log("agregado correctamente"));
        }
        else {
          let dM = this.dialogBus.open(DialogInformativoComponent, {
            data: {
              title: 'Atención!',
              message: "El tipo de plaza ya había sido agregado"
            }
          });
        }

      }

    });
  }



  deleteO(idRecaudadorTP: number) {
    var respuesta: Boolean = false;
    const dConfig = new MatDialogConfig();
    dConfig.data = {
      title: "Eliminando",
      message: '¿Está seguro de eliminar el registro?',
      respuesta: respuesta
    }
    const dialogAnswer = this.dialogBus.open(DialgAnswerSiNoComponent, dConfig);


    dialogAnswer.afterClosed().subscribe(data => {
      if (data == undefined) {
        respuesta = false;
      }
      else {
        respuesta = data.respuesta;
      }

      if (respuesta) {
        this.service.deleteRecaudadorTP(idRecaudadorTP)
          .subscribe(
            (res: any) => this.getRecaudadorTP(), //(1)
            (error: any) => console.log(error), //(2)
            () => console.log('orale') //(3)
          );

      }

    });


  }

}
