import { Component, OnInit, Inject } from '@angular/core';
import { TipoPlazaVigencia } from '../model/tipo-plaza-vigencia';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { TiposplazaService } from '../service/tiposplaza.service';
import { DialogInformativoComponent } from 'src/app/shared/dialog-informativo/dialog-informativo.component';
import { TipoPlaza } from '../model/tipo-plaza';
import { Observable, Subject } from 'rxjs';
export interface DialogData {
  tipoPlazaVigencia: TipoPlazaVigencia;
  tipoPlaza: TipoPlaza;
}


@Component({
    selector: 'app-add-tiposplaza-vigencia',
    templateUrl: './add-tiposplaza-vigencia.component.html',
    styleUrls: ['./add-tiposplaza-vigencia.component.css'],
    standalone: false
})
export class AddTiposplazaVigenciaComponent implements OnInit {
  vigencias: TipoPlazaVigencia[];

  oFinal: TipoPlazaVigencia = new TipoPlazaVigencia();
  tipoPlaza: TipoPlaza;
  encabezado = "Agregar período";
  editarId = false;

  constructor(public dialogRef: MatDialogRef<AddTiposplazaVigenciaComponent>,
    @Inject(MAT_DIALOG_DATA) public dd: DialogData,
    private service: TiposplazaService, private dialogI: MatDialog) {
    if (this.dd != null) {
      if (dd.tipoPlazaVigencia != null) {
        this.oFinal.idTipoPlazaVigencia = dd.tipoPlazaVigencia.idTipoPlazaVigencia;
        this.oFinal.tipoPlaza = dd.tipoPlazaVigencia.tipoPlaza;
        this.oFinal.vigenciaInicial = new Date(dd.tipoPlazaVigencia.vigenciaInicial);
        this.oFinal.vigenciaFinal = new Date(dd.tipoPlazaVigencia.vigenciaFinal);
        this.oFinal.importe = dd.tipoPlazaVigencia.importe;

        this.editarId = true;
        this.encabezado = "Editar período";
      }
      this.oFinal.tipoPlaza = dd.tipoPlaza;
    }
  }
  ngOnInit() {

  }
  onCancel(): void {
    this.dialogRef.close();
  }



  onSave(newO: TipoPlazaVigencia) {


    this.service.addTipoPlazaVigencia(newO)
      .subscribe(
        (data: TipoPlazaVigencia) => {
          console.log('created: ', data);
          this.onCancel();
        }, // (1)
        (error: any) => console.log(error), //(2)
        () => console.log('completed') //(3)
      );

  }

  showMessage(messageF: string) {
    const dConfig = new MatDialogConfig();
    dConfig.data = {
      title: 'Cuidado',
      message: messageF
    }
    let dMessage = this.dialogI.open(DialogInformativoComponent, dConfig);


  }

  validarPeriodo(newO: TipoPlazaVigencia) {

    let periodoValido: boolean = true;
    this.service.getTipoPlazaVigencias(newO.tipoPlaza.codigoTipoPlaza)
      .subscribe((data: TipoPlazaVigencia[]) => {
        this.vigencias = data;
        if (this.vigencias != null && this.vigencias.length > 0) {

          let vigenciaLast = this.vigencias[0];
        

          //revisamos que  sea mayor
          let toDateL = new Date(vigenciaLast.vigenciaInicial);
          let nInicio = new Date(newO.vigenciaInicial.getUTCFullYear(), newO.vigenciaInicial.getUTCMonth(), newO.vigenciaInicial.getUTCDay());
          let lInicio = new Date(toDateL.getUTCFullYear(), toDateL.getUTCMonth(), toDateL.getUTCDay());
        
          if (nInicio.getTime() > lInicio.getTime()) {
            //vamos a actualizar el último periodo
            vigenciaLast.vigenciaFinal = new Date(newO.vigenciaInicial.getTime() - (24 * 60 * 60 * 1000));
            this.onSave(vigenciaLast);
            //vamos a insertar el nuevo
            this.onSave(newO);


          }
          else {
        
            this.showMessage("La vigencia inicial debe ser mayor al último período");
          }


        }
        //si no hay ningún registro
        else {
          this.onSave(newO);
        }



      }, (error: any) => { console.log(error); });


  }

}
