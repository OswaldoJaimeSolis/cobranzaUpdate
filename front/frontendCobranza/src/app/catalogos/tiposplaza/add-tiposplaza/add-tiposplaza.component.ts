import { Component, OnInit, Inject, ChangeDetectionStrategy } from '@angular/core';
import { TipoPlaza } from '../model/tipo-plaza';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { TiposplazaService } from '../service/tiposplaza.service';
import { ListTiposplazaVigenciaComponent } from '../list-tiposplaza-vigencia/list-tiposplaza-vigencia.component';
import { ConfigCobroComponent } from '../config-cobro/config-cobro.component';
import { AvisoService } from 'src/app/shared/aviso.service';
export interface DialogData {
  tipoPlaza: TipoPlaza;
}

@Component({
    selector: 'app-add-tiposplaza',
    templateUrl: './add-tiposplaza.component.html',
    styleUrls: ['./add-tiposplaza.component.css'],
    changeDetection: ChangeDetectionStrategy.Eager,
    standalone: false
})
export class AddTiposplazaComponent implements OnInit {
  oFinal: TipoPlaza = new  TipoPlaza();
  encabezado = "Agregar tipo plaza";
  noEditarId = false;
  constructor(private dialogRef: MatDialogRef<AddTiposplazaComponent>,
    @Inject(MAT_DIALOG_DATA) dd: DialogData, private dialogV: MatDialog,
    private service: TiposplazaService, private aviso: AvisoService) {
    if (dd != null) {
      this.oFinal = dd.tipoPlaza;
      
      this.encabezado = "Editar tipo plaza";
      this.noEditarId = true;


    }
  }

  ngOnInit() {
  }

  onCancel(): void {

    this.dialogRef.close();
  }



  onSave(newO: TipoPlaza) {
    this.service.addTipoPlaza(newO)
      .subscribe(
        (data: TipoPlaza) => {
          console.log('created: ', data);
          this.aviso.exito('Tipo de plaza guardado');
          // Al dar de alta se devuelve el tipo creado para que la lista abra su configuración de cobro
          this.dialogRef.close(this.noEditarId ? null : data);
        }, // (1)
        (error: any) => console.log(error), //(2)
        () => console.log('completed') //(3)
      );
  }

  mostrarConfigCobro(tipoPlaza: TipoPlaza) {
    this.dialogV.open(ConfigCobroComponent, {
      width: '900px',
      maxWidth: '95vw',
      data: {
        tipoPlaza: tipoPlaza
      }
    });
  }

  mostrarPeriodos(tipoPlaza: TipoPlaza) {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.width = '560px';
    dialogConfig.maxWidth = '95vw';
    dialogConfig.data = {
      tipoPlaza: tipoPlaza,
      service: this.service
    }
    let dvigencias = this.dialogV.open(ListTiposplazaVigenciaComponent, dialogConfig);

    



  }

}



