import { Component, OnInit } from '@angular/core';
import { PropietarioPlaza } from '../model/propietario-plaza';
import { PropietarioPlazaService } from '../service/propietario-plaza.service';
import { MatDialog } from '@angular/material';
import { AddPropietarioPlazaComponent } from '../add-propietario-plaza/add-propietario-plaza.component';
import { DialogInformativoComponent } from 'src/app/shared/dialog-informativo/dialog-informativo.component';

@Component({
  selector: 'app-list-propietario-plaza',
  templateUrl: './list-propietario-plaza.component.html',
  styleUrls: ['./list-propietario-plaza.component.css']
})
export class ListPropietarioPlazaComponent implements OnInit {
  propietariosPlaza: PropietarioPlaza[];
  displayOrNot: boolean = true;
  stringBusqueda: string;
  isLoading: boolean = false;
  constructor(private service: PropietarioPlazaService, private dialog: MatDialog) { }

  ngOnInit() {
    this.getPropietariosPlaza();
  }
  getPropietariosPlaza() {
    this.isLoading = true;
    this.service.getTodos()
      .subscribe(
        (data: PropietarioPlaza[]) => { //start of (1)
          this.propietariosPlaza = data;
          console.log(this.propietariosPlaza)
          if (this.propietariosPlaza.length > 0) {
            console.log(this.propietariosPlaza);
            this.displayOrNot = false;
          }
          else
            this.displayOrNot = true;
        }, //end of (1)
        (error: any) => console.log(error), //(2) second argument
        () => { console.log('all data gets'), this.isLoading = false; } //(3) second argument
      );
  }



  openDialog(): void {
    let dialogRef = this.dialog.open(AddPropietarioPlazaComponent,
      {
        width: '500px',
      });



    dialogRef.afterClosed().subscribe(result => {
      this.getPropietariosPlaza();
      console.log('The dialog was closed');
    });
  }

  openDialogEdit(o: PropietarioPlaza): void {
    console.log(o.vigenciaFinal);
    if (new Date(o.vigenciaFinal).getFullYear() == 2099) {

      let dialogRef = this.dialog.open(AddPropietarioPlazaComponent, {
        data: {
          propietarioPlaza: o
        }
      });
      dialogRef.afterClosed().subscribe(result => {
        this.getPropietariosPlaza();
        console.log('The dialog was closed');
      });
    }
    else {
      let dMessage = this.dialog.open(DialogInformativoComponent, {
        data: {
          title: 'Atención!',
          message: 'No se puede modificar porque no está abierta la fecha'
        }
      });
    }

  }
}