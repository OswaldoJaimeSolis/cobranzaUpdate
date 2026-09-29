import { Component, OnInit, Inject } from '@angular/core';
import { PropietarioPlaza } from '../model/propietario-plaza';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialog } from '@angular/material/dialog';
import { Contribuyente } from '../../contribuyente/model/contribuyente';
import { PropietarioPlazaService } from '../service/propietario-plaza.service';
import { BusquedaTiposPlazaComponent } from 'src/app/shared/busqueda/busqueda-tipos-plaza/busqueda-tipos-plaza.component';
import { TipoPlaza } from '../../tiposplaza/model/tipo-plaza';
import { BusquedaContribuyentesComponent } from 'src/app/shared/busqueda/busqueda-contribuyentes/busqueda-contribuyentes.component';
import { DialogInformativoComponent } from 'src/app/shared/dialog-informativo/dialog-informativo.component';
import { DatePipe } from '@angular/common';
import { UntypedFormGroup } from '@angular/forms';
export interface BusResultTP {
  tipoPlazaSelect: TipoPlaza
}
export interface BusResultContribuyente {
  contribuyenteSelect: Contribuyente
}
export interface DialogData {
  propietarioPlaza: PropietarioPlaza;
}
@Component({
    selector: 'app-add-propietario-plaza',
    templateUrl: './add-propietario-plaza.component.html',
    styleUrls: ['./add-propietario-plaza.component.css'],
    standalone: false
})
export class AddPropietarioPlazaComponent implements OnInit {
  oOriginal: PropietarioPlaza;
  oFinal: PropietarioPlaza = new PropietarioPlaza();
  encabezado = "Agregar propietario";
  nuevo = true;
  formPropietarioPlaza: UntypedFormGroup;

  constructor(private dialogRef: MatDialogRef<AddPropietarioPlazaComponent>,
    @Inject(MAT_DIALOG_DATA) dd: DialogData, private dialog: MatDialog, private service: PropietarioPlazaService, public datePipe: DatePipe) {
    if (dd != null) {
      this.oOriginal = dd.propietarioPlaza;
      this.oFinal = new PropietarioPlaza();
      this.oFinal.idPropietarioPlaza = dd.propietarioPlaza.idPropietarioPlaza;
      this.oFinal.contribuyente.codigoContribuyente = dd.propietarioPlaza.contribuyente.codigoContribuyente;
      this.oFinal.contribuyente.nombre = dd.propietarioPlaza.contribuyente.nombre;
      this.oFinal.contribuyente.apePaterno = dd.propietarioPlaza.contribuyente.apePaterno;
      this.oFinal.contribuyente.apeMaterno = dd.propietarioPlaza.contribuyente.apeMaterno;
      this.oFinal.contribuyente.rfcContribuyente = dd.propietarioPlaza.contribuyente.rfcContribuyente;

      this.oFinal.plaza.codigoPlaza = dd.propietarioPlaza.plaza.codigoPlaza;

      this.oFinal.tipoPlaza.codigoTipoPlaza = dd.propietarioPlaza.tipoPlaza.codigoTipoPlaza;
      this.oFinal.tipoPlaza.addLocal = dd.propietarioPlaza.tipoPlaza.addLocal;
      this.oFinal.tipoPlaza.descripcionTipoPlaza = dd.propietarioPlaza.tipoPlaza.descripcionTipoPlaza;
      this.oFinal.tipoPlaza.leyendaTipoPlaza = dd.propietarioPlaza.tipoPlaza.leyendaTipoPlaza;
      this.oFinal.tipoPlaza.porImporteGlobalTipoPlaza = dd.propietarioPlaza.tipoPlaza.porImporteGlobalTipoPlaza;

      this.oFinal.giroDescripcion = dd.propietarioPlaza.giroDescripcion;
      this.oFinal.importe = dd.propietarioPlaza.importe;
      this.oFinal.vigenciaFinal = new Date(dd.propietarioPlaza.vigenciaFinal);
      this.oFinal.vigenciaInicial = new Date(dd.propietarioPlaza.vigenciaInicial);
      this.encabezado = "Editar propietario";
      this.nuevo = false;
      console.log("por importe global" + this.oFinal.tipoPlaza.porImporteGlobalTipoPlaza)
    }

  }


  ngOnInit() {
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  guardarPropietarioPlaza(newO: PropietarioPlaza) {
    console.log("NOO DEBERIA");
    if (this.nuevo) {
      this.onSave(newO);
    }
    else {
      this.onEdit(newO);
    }
  }

  onEdit(newO: PropietarioPlaza) {
    if (newO.vigenciaInicial > new Date(this.oOriginal.vigenciaInicial)) {
      if (this.validarAlgunCambio(newO)) {
        this.service.editPropietarioPlaza(newO, newO.idPropietarioPlaza)
          .subscribe(
            (data: PropietarioPlaza) => {
              console.log('created: ', data);
              this.onCancel();
            }, // (1)
            (error: any) => console.log(error), //(2)
            () => console.log('completed') //(3)
          );
      }
      else {
        this.dialog.open(DialogInformativoComponent, {
          data: {
            title: "Atención!",
            message: 'No hizo ningún cambio'
          }
        });
      }
    }
    else {
      this.dialog.open(DialogInformativoComponent, {
        data: {
          title: "F. inicial INCORRECTA",
          message: 'Indica una fecha mayor: ' + this.datePipe.transform(this.oOriginal.vigenciaInicial, 'dd-MM-yyyy')
        }
      });
    }

  }

  onSave(newO: PropietarioPlaza) {
    console.log("NUEVOOOOOO");
    this.service.addPropietarioPlaza(newO)
      .subscribe(
        (data: PropietarioPlaza) => {
          console.log('created: ', data);
          this.onCancel();
        }, // (1)
        (error: any) => console.log(error), //(2)
        () => console.log('completed') //(3)
      );
  }

  buscarTipoPlaza() {
    let dBusTP = this.dialog.open(BusquedaTiposPlazaComponent);
    dBusTP.afterClosed().subscribe((data: BusResultTP) => {
      if (data == undefined) {

      }
      else {
        console.log("eeeeeeeeey");
        console.log(data);
        this.oFinal.tipoPlaza.codigoTipoPlaza = data.tipoPlazaSelect.codigoTipoPlaza;
        this.oFinal.tipoPlaza.addLocal = data.tipoPlazaSelect.addLocal;
        this.oFinal.tipoPlaza.descripcionTipoPlaza = data.tipoPlazaSelect.descripcionTipoPlaza;
        this.oFinal.tipoPlaza.leyendaTipoPlaza = data.tipoPlazaSelect.leyendaTipoPlaza;
        this.oFinal.tipoPlaza.porImporteGlobalTipoPlaza = data.tipoPlazaSelect.porImporteGlobalTipoPlaza;
      }
    });
  }

  validarAlgunCambio(newO: PropietarioPlaza): boolean {
    //valida si con, tp, giro
    if (this.oOriginal.contribuyente.codigoContribuyente == newO.contribuyente.codigoContribuyente &&
      this.oOriginal.tipoPlaza.codigoTipoPlaza == newO.tipoPlaza.codigoTipoPlaza &&
      this.oOriginal.giroDescripcion == newO.giroDescripcion) {
      if (this.oOriginal.tipoPlaza.porImporteGlobalTipoPlaza) {
        if (this.oOriginal.importe == newO.importe) {
          return false;
        }
        else {
          return true;
        }
      }
      else {
        return false;
      }
    }
    else {
      return true;
    }
  }

  buscarContribuyente() {
    let dBusCon = this.dialog.open(BusquedaContribuyentesComponent);
    dBusCon.afterClosed().subscribe((data: BusResultContribuyente) => {
      if (data != undefined) {
        this.oFinal.contribuyente = data.contribuyenteSelect;
      }
    });
  }

  /*private validarCampos():string{
    if(this.oFinal.tipoPlaza.codigoTipoPlaza==null){
      
    }
  } */



}
