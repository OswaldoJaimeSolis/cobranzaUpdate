import { Component, OnInit } from '@angular/core';
import { PropietarioPlaza } from '../model/propietario-plaza';
import { PropietarioPlazaService } from '../service/propietario-plaza.service';
import { MatLegacyDialog as MatDialog } from '@angular/material/legacy-dialog';
import { AddPropietarioPlazaComponent } from '../add-propietario-plaza/add-propietario-plaza.component';
import { DialogInformativoComponent } from 'src/app/shared/dialog-informativo/dialog-informativo.component';

@Component({
  selector: 'app-list-propietario-plaza-jb',
  templateUrl: './list-propietario-plaza-jb.component.html',
  styleUrls: ['./list-propietario-plaza-jb.component.css']
})
export class ListPropietarioPlazaJbComponent implements OnInit {
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

      this.service.getOne(o.idPropietarioPlaza)
        .subscribe((onePP: PropietarioPlaza) => {
          //se recupera la plaza dada
          let dialogRef = this.dialog.open(AddPropietarioPlazaComponent, {
            data: {
              propietarioPlaza: onePP
            }
          });
          dialogRef.afterClosed().subscribe(result => {
            this.getPropietariosPlaza();
            console.log('The dialog was closed');
          });


        },
          (error: any) => console.log(error),
          () => { console.log('get one') });


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