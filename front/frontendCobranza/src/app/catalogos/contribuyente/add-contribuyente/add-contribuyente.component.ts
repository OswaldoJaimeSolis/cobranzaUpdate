import { Component, OnInit, Inject, ChangeDetectionStrategy } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Contribuyente } from '../model/contribuyente';
import { ContribuyenteService } from '../service/contribuyente.service';

export interface DialogData {
  contribuyente: Contribuyente;
  codigoContribuyente: string;
}

@Component({
    selector: 'app-add-contribuyente',
    templateUrl: './add-contribuyente.component.html',
    styleUrls: ['./add-contribuyente.component.css'],
    changeDetection: ChangeDetectionStrategy.Eager,
    standalone: false
})
export class AddContribuyenteComponent implements OnInit {
  conFinal: Contribuyente = new Contribuyente();
  encabezado = "Agregar";
  editarId = false;
  constructor(public dialogRef: MatDialogRef<AddContribuyenteComponent>,
    @Inject(MAT_DIALOG_DATA) public dd: DialogData,
    private service: ContribuyenteService) {
    if (this.dd != null) {
       this.conFinal.codigoContribuyente= dd.contribuyente.codigoContribuyente;
       this.conFinal.nombre=dd.contribuyente.nombre;
       this.conFinal.apePaterno= dd.contribuyente.apePaterno;
       this.conFinal.apeMaterno= dd.contribuyente.apeMaterno;
       this.conFinal.rfcContribuyente= dd.contribuyente.rfcContribuyente;
       this.editarId=true;
    }

  }

  ngOnInit() {
  }

  onCancel(): void {
    this.dialogRef.close();
  }
 


  onSave(newO: Contribuyente){
    this.service.addContribuyente(newO)
        .subscribe(
            (data: Contribuyente) => {
                console.log('created: ',data);
                this.onCancel();
            }, // (1)
            (error: any) => console.log(error), //(2)
            () => console.log('completed') //(3)
        );
  }

}
