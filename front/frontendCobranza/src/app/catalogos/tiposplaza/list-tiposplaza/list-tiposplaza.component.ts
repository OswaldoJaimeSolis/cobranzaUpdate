import { Component, OnInit } from '@angular/core';
import { TipoPlaza } from '../model/tipo-plaza';
import { TiposplazaService } from '../service/tiposplaza.service';
import { MatDialog } from '@angular/material/dialog';
import { AddTiposplazaComponent } from '../add-tiposplaza/add-tiposplaza.component';

@Component({
    selector: 'app-list-tiposplaza',
    templateUrl: './list-tiposplaza.component.html',
    styleUrls: ['./list-tiposplaza.component.css'],
    standalone: false
})
export class ListTiposplazaComponent implements OnInit {
  tiposPlaza: TipoPlaza[];
  displayOrNot: boolean = true;
  stringBusqueda: string;
  isLoading:boolean=false;
  constructor(private service: TiposplazaService, private dialog: MatDialog) { }

  ngOnInit() {
    this.getTiposPlaza();
  }

  getTiposPlaza() {
    this.isLoading=true;
    this.service.getTodos()
      .subscribe(
        (data: TipoPlaza[]) => { //start of (1)
          this.tiposPlaza = data;
          if (this.tiposPlaza.length > 0)
            this.displayOrNot = false;
          else
            this.displayOrNot = true;
        }, //end of (1)
        (error: any) => console.log(error), //(2) second argument
        () => {console.log('all data gets');this.isLoading=false;} //(3) second argument
      );
  }

  deleteO(codigoTipoPlaza: string) {
    this.service.deleteTipoPlaza(codigoTipoPlaza)
      .subscribe(
        (res: any) => this.getTiposPlaza(), //(1)
        (error: any) => console.log(error), //(2)
        () => console.log('deleted') //(3)
      )
  }

  openDialog(): void {
    let dialogRef = this.dialog.open(AddTiposplazaComponent,
      {
        width: '500px',
      });



    dialogRef.afterClosed().subscribe(result => {
      this.getTiposPlaza();
      console.log('The dialog was closed');
    });
  }

  openDialogEdit(o: TipoPlaza): void {
    //console.log(op.numeroPresidenciaEmpleado);
    let dialogRef = this.dialog.open(AddTiposplazaComponent, {
      data: {
        tipoPlaza: o
      }
    });

    dialogRef.afterClosed().subscribe(result => {
      this.getTiposPlaza();
      console.log('The dialog was closed');
    });
  }
}