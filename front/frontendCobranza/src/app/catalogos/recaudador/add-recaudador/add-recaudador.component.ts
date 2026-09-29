import { Component, OnInit, Inject } from '@angular/core';
import { Recaudador } from '../model/recaudador';
import { MatLegacyDialogRef as MatDialogRef, MAT_LEGACY_DIALOG_DATA as MAT_DIALOG_DATA, MatLegacyDialog as MatDialog } from '@angular/material/legacy-dialog';
import { RecaudadorService } from '../service/recaudador.service';
import { BusquedaTiposPlazaComponent } from 'src/app/shared/busqueda/busqueda-tipos-plaza/busqueda-tipos-plaza.component';
import { TipoPlaza } from '../../tiposplaza/model/tipo-plaza';
import { ListTipoPlazaRecaudadorComponent } from '../list-tipo-plaza-recaudador/list-tipo-plaza-recaudador.component';
import { DialogInformativoComponent } from 'src/app/shared/dialog-informativo/dialog-informativo.component';
export interface DialogData {
  recaudador: Recaudador;
  codigoRecaudador: string;
}
export interface BusResult {
  tipoPlazaSelect: TipoPlaza
}

@Component({
  selector: 'app-add-recaudador',
  templateUrl: './add-recaudador.component.html',
  styleUrls: ['./add-recaudador.component.css']
})


export class AddRecaudadorComponent implements OnInit {
  oFinal: Recaudador = new Recaudador();
  encabezado = "Agregar recaudador";
  editarId = false;
  constructor(public dialogRef: MatDialogRef<AddRecaudadorComponent>,
    @Inject(MAT_DIALOG_DATA) public dd: DialogData,
    private service: RecaudadorService, private dialog: MatDialog) {
    if (this.dd != null) {
      this.oFinal.codigoRecaudador = dd.recaudador.codigoRecaudador;
      this.oFinal.passRecaudador = dd.recaudador.passRecaudador;
      this.oFinal.nombreRecaudador = dd.recaudador.nombreRecaudador;
      this.oFinal.activoRecaudador = dd.recaudador.activoRecaudador;

      this.editarId = true;
      this.encabezado = "Editar recaudador";
    }
  }
  ngOnInit() {
  }
  onCancel(): void {
    this.dialogRef.close();
  }



  onSave(newO: Recaudador) {
    this.service.addRecaudador(newO)
      .subscribe(
        (data: Recaudador) => {
          console.log('created: ', data);
          this.onCancel();
        }, // (1)
        (error: any) => console.log(error), //(2)
        () => console.log('completed') //(3)
      );
  }


  tiposPlazaRecaudador() {
    if (this.editarId) {
      let dialog = this.dialog.open(ListTipoPlazaRecaudadorComponent, {
        data: {
          recaudador: this.oFinal,
          service: this.service
        }
      });

    }
    else{
      this.dialog.open(DialogInformativoComponent,{data:{
        title:'Atención',
        message:'Guarde el registro primero'
      }})
    
    }
  }
  
}
