import { Component, OnInit, Inject, ChangeDetectionStrategy } from '@angular/core';
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
import { AvisoService } from 'src/app/shared/aviso.service';
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
    changeDetection: ChangeDetectionStrategy.Eager,
    standalone: false
})
export class AddPropietarioPlazaComponent implements OnInit {
  oOriginal: PropietarioPlaza;
  oFinal: PropietarioPlaza = new PropietarioPlaza();
  encabezado = "Agregar propietario";
  nuevo = true;
  formPropietarioPlaza: UntypedFormGroup;

  constructor(private dialogRef: MatDialogRef<AddPropietarioPlazaComponent>,
    @Inject(MAT_DIALOG_DATA) dd: DialogData, private dialog: MatDialog, private service: PropietarioPlazaService, public datePipe: DatePipe,
    private aviso: AvisoService) {
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
      this.oFinal.licenciaComercio = dd.propietarioPlaza.licenciaComercio === true;
      this.oFinal.adeudoAnterior = dd.propietarioPlaza.adeudoAnterior;
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

  /** Cierra el diálogo avisando que se guardó */
  private guardado(): void {
    this.aviso.exito('Plaza guardada');
    this.onCancel();
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

  // Licencia de comercio y adeudo anterior no cambian la vigencia: se guardan sobre el mismo registro
  cambiaronDatosCobro(newO: PropietarioPlaza): boolean {
    return (this.oOriginal.licenciaComercio === true) != (newO.licenciaComercio === true) ||
      (this.oOriginal.adeudoAnterior || 0) != (newO.adeudoAnterior || 0);
  }

  private soloDia(fecha: Date | string): number {
    const d = new Date(fecha);
    return new Date(d.getFullYear(), d.getMonth(), d.getDate()).getTime();
  }

  private mostrarError(error: any, porOmision: string) {
    this.dialog.open(DialogInformativoComponent, {
      data: {
        title: "No se pudo guardar",
        tipo: 'error',
        message: error && error.error && error.error.mensaje ? error.error.mensaje : porOmision
      }
    });
  }

  // Una fecha inicial anterior a la del registro es una corrección: se cambia en el mismo registro
  // (junto con licencia y adeudo anterior), sin cerrar el registro ni crear otro.
  private corregirFechaInicial(newO: PropietarioPlaza) {
    if (this.validarAlgunCambio(newO)) {
      this.dialog.open(DialogInformativoComponent, {
        data: {
          title: "Atención!",
          message: 'Para adelantar la fecha inicial no cambie contribuyente, tipo de plaza, giro ni importe: guarde primero la fecha y después los demás cambios'
        }
      });
      return;
    }
    const fecha = this.datePipe.transform(newO.vigenciaInicial, 'yyyy-MM-dd');
    this.service.corregirVigenciaInicial(newO.idPropietarioPlaza, fecha).subscribe({
      next: () => {
        if (!this.cambiaronDatosCobro(newO)) {
          this.guardado();
          return;
        }
        this.service.updateDatosCobro(newO.idPropietarioPlaza, newO.licenciaComercio === true, newO.adeudoAnterior)
          .subscribe({
            next: () => this.guardado(),
            error: (error: any) => this.mostrarError(error, 'No se pudo guardar')
          });
      },
      error: (error: any) => this.mostrarError(error, 'No se pudo corregir la fecha inicial')
    });
  }

  onEdit(newO: PropietarioPlaza) {
    if (this.soloDia(newO.vigenciaInicial) < this.soloDia(this.oOriginal.vigenciaInicial)) {
      this.corregirFechaInicial(newO);
      return;
    }
    if (!this.validarAlgunCambio(newO) && this.cambiaronDatosCobro(newO)) {
      this.service.updateDatosCobro(newO.idPropietarioPlaza, newO.licenciaComercio === true, newO.adeudoAnterior)
        .subscribe({
          next: () => this.guardado(),
          error: (error: any) => this.dialog.open(DialogInformativoComponent, {
            data: {
              title: "Atención!",
              message: error.error && error.error.mensaje ? error.error.mensaje : 'No se pudo guardar'
            }
          })
        });
      return;
    }
    if (this.soloDia(newO.vigenciaInicial) > this.soloDia(this.oOriginal.vigenciaInicial)) {
      if (this.validarAlgunCambio(newO)) {
        this.service.editPropietarioPlaza(newO, newO.idPropietarioPlaza)
          .subscribe(
            (data: PropietarioPlaza) => {
              console.log('created: ', data);
              this.guardado();
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
          title: "Fecha inicial incorrecta",
          tipo: 'aviso',
          message: 'Para cambiar contribuyente, tipo de plaza, giro o importe indica una fecha mayor a '
            + this.datePipe.transform(this.oOriginal.vigenciaInicial, 'dd-MM-yyyy')
            + '. Para corregir la fecha inicial indica una fecha anterior sin cambiar otros datos'
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
          this.guardado();
        }, // (1)
        (error: any) => console.log(error), //(2)
        () => console.log('completed') //(3)
      );
  }

  buscarTipoPlaza() {
    let dBusTP = this.dialog.open(BusquedaTiposPlazaComponent, { width: '900px', maxWidth: '95vw' });
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
    let dBusCon = this.dialog.open(BusquedaContribuyentesComponent, { width: '900px', maxWidth: '95vw' });
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
