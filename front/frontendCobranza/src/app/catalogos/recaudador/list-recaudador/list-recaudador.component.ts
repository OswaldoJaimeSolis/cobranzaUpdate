import { Component, OnInit, ChangeDetectionStrategy, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { RecaudadorPipe } from '../filter/recaudador.pipe';
import { AvisoService } from 'src/app/shared/aviso.service';
import { Recaudador } from '../model/recaudador';
import { RecaudadorService } from '../service/recaudador.service';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { AddRecaudadorComponent } from '../add-recaudador/add-recaudador.component';
import { DialgAnswerSiNoComponent } from 'src/app/shared/dialg-answer-si-no/dialg-answer-si-no.component';

@Component({
    selector: 'app-list-recaudador',
    templateUrl: './list-recaudador.component.html',
    styleUrls: ['./list-recaudador.component.css'],
    changeDetection: ChangeDetectionStrategy.Eager,
    standalone: false
})
export class ListRecaudadorComponent implements OnInit {

  recaudadores: Recaudador[];
  displayOrNot: boolean = true;
  stringBusqueda: string;
  isLoading: boolean = false;
  readonly columnas = ['codigo', 'nombre', 'estado', 'condona', 'acciones'];
  dataSource = new MatTableDataSource<Recaudador>([]);
  private filtro = new RecaudadorPipe();

  @ViewChild(MatSort) set sort(sort: MatSort) {
    this.dataSource.sort = sort;
  }

  constructor(private service: RecaudadorService, public dialog: MatDialog, private aviso: AvisoService) {
    this.dataSource.sortingDataAccessor = (o: Recaudador, columna: string) =>
      columna == 'codigo' ? o.codigoRecaudador : (o.nombreRecaudador || '').toLocaleLowerCase();
  }

  /** La tabla se alimenta con el resultado del pipe de filtro existente */
  filtrar() {
    this.dataSource.data = this.filtro.transform(this.recaudadores, this.stringBusqueda);
  }

  ngOnInit() {

    this.getRecaudadores();

  };


  getRecaudadores() {
    this.isLoading = true;
    this.service.getTodos()
      .subscribe(
        (data: Recaudador[]) => { //start of (1)
          this.recaudadores = data;
          this.filtrar();
          if (this.recaudadores.length > 0)
            this.displayOrNot = false;
          else
            this.displayOrNot = true;
        }, //end of (1)
        (error: any) => { console.log(error); this.isLoading = false; }, //(2) second argument
        () => { console.log('all data gets'); this.isLoading = false } //(3) second argument
      );
  }



  deleteRecaudador(codigoRecaudador: string) {
    var respuesta: Boolean = false;
    const dConfig = new MatDialogConfig();
    dConfig.data = {
      title: "Eliminar recaudador",
      message: '¿Seguro que desea eliminar el recaudador ' + codigoRecaudador + '? Esta acción no se puede deshacer.',
      textoAceptar: 'Eliminar',
      destructivo: true,
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
            (res: any) => { this.aviso.exito('Recaudador eliminado'); this.getRecaudadores(); }, //(1)
            (error: any) => console.log(error), //(2)
            () => console.log('deleted') //(3)
          );
      }

    });


  }



  openDialog(): void {
    let dialogRef = this.dialog.open(AddRecaudadorComponent,
      {
        width: '560px',
        maxWidth: '95vw',
      });



    dialogRef.afterClosed().subscribe(result => {
      this.getRecaudadores();
      console.log('The dialog was closed');
    });
  }

  openDialogEdit(o: Recaudador): void {
    //console.log(op.numeroPresidenciaEmpleado);
    let dialogRef = this.dialog.open(AddRecaudadorComponent, {
      width: '560px',
      maxWidth: '95vw',
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