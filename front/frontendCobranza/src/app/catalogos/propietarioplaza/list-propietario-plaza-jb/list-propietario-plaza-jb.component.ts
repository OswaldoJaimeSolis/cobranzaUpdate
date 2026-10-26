import { Component, OnInit, ChangeDetectionStrategy, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { MatPaginator } from '@angular/material/paginator';
import { PropietarioPlazaPipe } from '../filter/propietario-plaza.pipe';
import { PropietarioPlaza } from '../model/propietario-plaza';
import { PropietarioPlazaService } from '../service/propietario-plaza.service';
import { MatDialog } from '@angular/material/dialog';
import { AddPropietarioPlazaComponent } from '../add-propietario-plaza/add-propietario-plaza.component';
import { DialogInformativoComponent } from 'src/app/shared/dialog-informativo/dialog-informativo.component';

@Component({
    selector: 'app-list-propietario-plaza-jb',
    templateUrl: './list-propietario-plaza-jb.component.html',
    styleUrls: ['./list-propietario-plaza-jb.component.css'],
    changeDetection: ChangeDetectionStrategy.Eager,
    standalone: false
})
export class ListPropietarioPlazaJbComponent implements OnInit {
  propietariosPlaza: PropietarioPlaza[];
  displayOrNot: boolean = true;
  stringBusqueda: string;
  isLoading: boolean = false;
  readonly columnas = ['plaza', 'tipoPlaza', 'contribuyente', 'vigencia', 'licencia', 'adeudo', 'acciones'];
  dataSource = new MatTableDataSource<PropietarioPlaza>([]);
  private filtro = new PropietarioPlazaPipe();

  @ViewChild(MatSort) set sort(sort: MatSort) {
    this.dataSource.sort = sort;
  }
  @ViewChild(MatPaginator) set paginator(paginator: MatPaginator) {
    this.dataSource.paginator = paginator;
  }

  constructor(private service: PropietarioPlazaService, private dialog: MatDialog) {
    // En la lista, plaza/tipoPlaza/contribuyente llegan como texto
    this.dataSource.sortingDataAccessor = (pp: any, columna: string) => {
      switch (columna) {
        case 'vigencia': return new Date(pp.vigenciaInicial).getTime();
        case 'adeudo': return Number(pp.adeudoAnterior) || 0;
        default: return String(pp[columna] ?? '').toLocaleLowerCase();
      }
    };
  }

  /** La tabla se alimenta con el resultado del pipe de filtro existente */
  filtrar() {
    this.dataSource.data = this.filtro.transform(this.propietariosPlaza, this.stringBusqueda);
  }

  ngOnInit() {
    this.getPropietariosPlaza();
  }
  getPropietariosPlaza() {
    this.isLoading = true;
    this.service.getTodos()
      .subscribe(
        (data: PropietarioPlaza[]) => { //start of (1)
          this.propietariosPlaza = data;
          this.filtrar();
          console.log(this.propietariosPlaza)
          if (this.propietariosPlaza.length > 0) {
            console.log(this.propietariosPlaza);
            this.displayOrNot = false;
          }
          else
            this.displayOrNot = true;
        }, //end of (1)
        (error: any) => { console.log(error); this.isLoading = false; }, //(2) second argument
        () => { console.log('all data gets'), this.isLoading = false; } //(3) second argument
      );
  }



  openDialog(): void {
    let dialogRef = this.dialog.open(AddPropietarioPlazaComponent,
      {
        width: '560px',
        maxWidth: '95vw',
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
            width: '560px',
            maxWidth: '95vw',
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
          title: 'No se puede editar',
          message: 'Sólo se puede modificar el registro vigente (con fecha final abierta).',
          tipo: 'aviso'
        }
      });
    }

  }
}