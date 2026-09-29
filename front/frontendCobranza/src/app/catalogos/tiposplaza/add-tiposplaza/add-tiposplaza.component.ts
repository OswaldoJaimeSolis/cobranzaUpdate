import { Component, OnInit, Inject } from '@angular/core';
import { TipoPlaza } from '../model/tipo-plaza';
import { MatDialogRef, MAT_DIALOG_DATA, MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { TiposplazaService } from '../service/tiposplaza.service';
import { ListTiposplazaVigenciaComponent } from '../list-tiposplaza-vigencia/list-tiposplaza-vigencia.component';
export interface DialogData {
  tipoPlaza: TipoPlaza;
}

@Component({
    selector: 'app-add-tiposplaza',
    templateUrl: './add-tiposplaza.component.html',
    styleUrls: ['./add-tiposplaza.component.css'],
    standalone: false
})
export class AddTiposplazaComponent implements OnInit {
  oFinal: TipoPlaza = new  TipoPlaza();
  encabezado = "Agregar tipo plaza";
  noEditarId = false;
  vigenciasHidde = false;
  constructor(private dialogRef: MatDialogRef<AddTiposplazaComponent>,
    @Inject(MAT_DIALOG_DATA) dd: DialogData, private dialogV: MatDialog,
    private service: TiposplazaService) {
    if (dd != null) {
      this.oFinal = dd.tipoPlaza;
      
      this.encabezado = "Editar tipo plaza";
      this.noEditarId = true;


    }
    this.vigenciasHidde = this.oFinal.porImporteGlobalTipoPlaza;

  }

  ngOnInit() {
    this.actualizarBtnPeriodo(this.oFinal.porImporteGlobalTipoPlaza);
  }

  actualizarBtnPeriodo(periodosbtn:boolean){
    this.vigenciasHidde=!periodosbtn;
    console.log(this.oFinal.porImporteGlobalTipoPlaza);
    console.log(this.vigenciasHidde);
  }

  onCancel(): void {

    this.dialogRef.close();
  }



  onSave(newO: TipoPlaza) {
    this.service.addTipoPlaza(newO)
      .subscribe(
        (data: TipoPlaza) => {
          console.log('created: ', data);
          this.onCancel();
        }, // (1)
        (error: any) => console.log(error), //(2)
        () => console.log('completed') //(3)
      );
  }

  mostrarPeriodos(tipoPlaza: TipoPlaza) {
    const dialogConfig = new MatDialogConfig();
    dialogConfig.data = {
      tipoPlaza: tipoPlaza,
      service: this.service
    }
    let dvigencias = this.dialogV.open(ListTiposplazaVigenciaComponent, dialogConfig);

    



  }

}



