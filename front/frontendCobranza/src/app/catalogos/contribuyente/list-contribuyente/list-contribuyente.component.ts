import { Component, OnInit, ChangeDetectionStrategy, ViewChild } from '@angular/core';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { FilterPipe } from '../filter.pipe';
import { AvisoService } from 'src/app/shared/aviso.service';
import { Contribuyente } from '../model/contribuyente';
import { ContribuyenteService } from '../service/contribuyente.service';
import { AddContribuyenteComponent } from '../add-contribuyente/add-contribuyente.component';
import { DialgAnswerSiNoComponent } from 'src/app/shared/dialg-answer-si-no/dialg-answer-si-no.component';

@Component({
    selector: 'app-list-contribuyente',
    templateUrl: './list-contribuyente.component.html',
    styleUrls: ['./list-contribuyente.component.css'],
    changeDetection: ChangeDetectionStrategy.Eager,
    standalone: false
})
export class ListContribuyenteComponent implements OnInit {
  contribuyentes: Contribuyente[];
  displayOrNot: boolean = true;
  stringBusqueda: string;
  isLoading:boolean=false;
  readonly columnas = ['codigo', 'nombre', 'rfc', 'acciones'];
  dataSource = new MatTableDataSource<Contribuyente>([]);
  private filtro = new FilterPipe();

  @ViewChild(MatSort) set sort(sort: MatSort) {
    this.dataSource.sort = sort;
  }
  @ViewChild(MatPaginator) set paginator(paginator: MatPaginator) {
    this.dataSource.paginator = paginator;
  }

  constructor(private service: ContribuyenteService, public dialog: MatDialog, private aviso: AvisoService) {
    this.dataSource.sortingDataAccessor = (con: Contribuyente, columna: string) =>
      columna == 'codigo' ? con.codigoContribuyente
        : columna == 'rfc' ? (con.rfcContribuyente || '')
        : (con.nombre + ' ' + con.apePaterno + ' ' + con.apeMaterno).toLocaleLowerCase();
  }

  /** La tabla se alimenta con el resultado del pipe de filtro existente */
  filtrar() {
    this.dataSource.data = this.filtro.transform(this.contribuyentes, this.stringBusqueda);
  }

  ngOnInit() {

    this.getContribuyentes();

  };


  getContribuyentes() {
    this.isLoading=true;
    this.service.getTodos()
      .subscribe(
        (data: Contribuyente[]) => { //start of (1)
          this.contribuyentes = data;
          this.filtrar();
          if (this.contribuyentes.length > 0)
            this.displayOrNot = false;
          else
            this.displayOrNot = true;
        }, //end of (1)
        (error: any) => { console.log(error); this.isLoading = false; }, //(2) second argument
        () => {console.log('all data gets'), this.isLoading=false;} //(3) second argument
      );
  }


  deleteContribuyente(codigoContribuyente: string) {
    var respuesta: Boolean = false;
    const dConfig = new MatDialogConfig();
    dConfig.data = {
      title: "Eliminar contribuyente",
      message: '¿Seguro que desea eliminar el contribuyente ' + codigoContribuyente + '? Esta acción no se puede deshacer.',
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
        this.service.deleteContribuyente(codigoContribuyente)
          .subscribe(
            (res: any) => { this.aviso.exito('Contribuyente eliminado'); this.getContribuyentes(); }, //(1)
            (error: any) => console.log(error), //(2)
            () => console.log('deleted') //(3)
          );
      }

    });


  }




  openDialog(): void {
    let dialogRef = this.dialog.open(AddContribuyenteComponent,
      {
        width: '560px',
        maxWidth: '95vw',
      });



    dialogRef.afterClosed().subscribe(result => {
      this.getContribuyentes();
      console.log('The dialog was closed');
    });
  }

  openDialogEdit(con: Contribuyente): void {
    //console.log(op.numeroPresidenciaEmpleado);
    let dialogRef = this.dialog.open(AddContribuyenteComponent, {
      width: '560px',
      maxWidth: '95vw',
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
