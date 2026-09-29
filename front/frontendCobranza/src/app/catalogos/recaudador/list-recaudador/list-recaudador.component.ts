import { Component, OnInit } from '@angular/core';
import { Recaudador } from '../model/recaudador';
import { RecaudadorService } from '../service/recaudador.service';
import { MatDialog, MatDialogConfig } from '@angular/material';
import { AddRecaudadorComponent } from '../add-recaudador/add-recaudador.component';
import { DialgAnswerSiNoComponent } from 'src/app/shared/dialg-answer-si-no/dialg-answer-si-no.component';

@Component({
  selector: 'app-list-recaudador',
  templateUrl: './list-recaudador.component.html',
  styleUrls: ['./list-recaudador.component.css']
})
export class ListRecaudadorComponent implements OnInit {

  recaudadores: Recaudador[];
  displayOrNot: boolean = true;
  stringBusqueda: string;
  isLoading: boolean = false;
  constructor(private service: RecaudadorService, public dialog: MatDialog) { }

  ngOnInit() {

    this.getRecaudadores();

  };


  getRecaudadores() {
    this.isLoading = true;
    this.service.getTodos()
      .subscribe(
        (data: Recaudador[]) => { //start of (1)
          this.recaudadores = data;
          if (this.recaudadores.length > 0)
            this.displayOrNot = false;
          else
            this.displayOrNot = true;
        }, //end of (1)
        (error: any) => console.log(error), //(2) second argument
        () => { console.log('all data gets'); this.isLoading = false } //(3) second argument
      );
  }



  deleteRecaudador(codigoRecaudador: string) {
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
        this.service.deleteRecaudador(codigoRecaudador)
          .subscribe(
            (res: any) => this.getRecaudadores(), //(1)
            (error: any) => console.log(error), //(2)
            () => console.log('deleted') //(3)
          );
      }

    });


  }



  openDialog(): void {
    let dialogRef = this.dialog.open(AddRecaudadorComponent,
      {
        width: '500px',
      });



    dialogRef.afterClosed().subscribe(result => {
      this.getRecaudadores();
      console.log('The dialog was closed');
    });
  }

  openDialogEdit(o: Recaudador): void {
    //console.log(op.numeroPresidenciaEmpleado);
    let dialogRef = this.dialog.open(AddRecaudadorComponent, {
      data: {
        recaudador: o
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      this.getRecaudadores();
      console.log('The dialog was closed');
    });
  }
}