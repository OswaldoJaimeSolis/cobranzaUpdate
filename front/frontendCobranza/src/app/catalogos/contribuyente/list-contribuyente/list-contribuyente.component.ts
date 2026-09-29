import { Component, OnInit } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material';
import { Contribuyente } from '../model/contribuyente';
import { ContribuyenteService } from '../service/contribuyente.service';
import { AddContribuyenteComponent } from '../add-contribuyente/add-contribuyente.component';
import { DialgAnswerSiNoComponent } from 'src/app/shared/dialg-answer-si-no/dialg-answer-si-no.component';

@Component({
  selector: 'app-list-contribuyente',
  templateUrl: './list-contribuyente.component.html',
  styleUrls: ['./list-contribuyente.component.css']
})
export class ListContribuyenteComponent implements OnInit {
  contribuyentes: Contribuyente[];
  displayOrNot: boolean = true;
  stringBusqueda: string;
  isLoading:boolean=false;
  constructor(private service: ContribuyenteService, public dialog: MatDialog) { }

  ngOnInit() {

    this.getContribuyentes();

  };


  getContribuyentes() {
    this.isLoading=true;
    this.service.getTodos()
      .subscribe(
        (data: Contribuyente[]) => { //start of (1)
          this.contribuyentes = data;
          if (this.contribuyentes.length > 0)
            this.displayOrNot = false;
          else
            this.displayOrNot = true;
        }, //end of (1)
        (error: any) => console.log(error), //(2) second argument
        () => {console.log('all data gets'), this.isLoading=false;} //(3) second argument
      );
  }


  deleteContribuyente(codigoContribuyente: string) {
    var respuesta: Boolean = false;
    const dConfig = new MatDialogConfig();
    dConfig.data = {
      title: "Eliminando",
      message: '¿Está seguro de eliminar el registro?',
      respuesta: respuesta
    }
    const dialogAnswer = this.dialog.open(DialgAnswerSiNoComponent, dConfig);


    dialogAnswer.afterClosed().subscribe(data => {
      if (data == undefined) {
        respuesta = false;
      }
      else {
        respuesta = data.respuesta;
      }

      if (respuesta) {
        this.service.deleteContribuyente(codigoContribuyente)
          .subscribe(
            (res: any) => this.getContribuyentes(), //(1)
            (error: any) => console.log(error), //(2)
            () => console.log('deleted') //(3)
          );
      }

    });


  }




  openDialog(): void {
    let dialogRef = this.dialog.open(AddContribuyenteComponent,
      {
        width: '500px',
      });



    dialogRef.afterClosed().subscribe(result => {
      this.getContribuyentes();
      console.log('The dialog was closed');
    });
  }

  openDialogEdit(con: Contribuyente): void {
    //console.log(op.numeroPresidenciaEmpleado);
    let dialogRef = this.dialog.open(AddContribuyenteComponent, {
      data: {
        contribuyente: con




      }
    });

    dialogRef.afterClosed().subscribe(result => {
      this.getContribuyentes();
      console.log('The dialog was closed');
    });
  }
}
