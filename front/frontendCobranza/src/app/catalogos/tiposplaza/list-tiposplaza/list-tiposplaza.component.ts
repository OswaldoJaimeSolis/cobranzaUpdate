import { Component, OnInit, ChangeDetectionStrategy, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { TiposplazaPipe } from '../filter/tiposplaza.pipe';
import { AvisoService } from 'src/app/shared/aviso.service';
import { DialgAnswerSiNoComponent } from 'src/app/shared/dialg-answer-si-no/dialg-answer-si-no.component';
import { TipoPlaza } from '../model/tipo-plaza';
import { TiposplazaService } from '../service/tiposplaza.service';
import { MatDialog } from '@angular/material/dialog';
import { AddTiposplazaComponent } from '../add-tiposplaza/add-tiposplaza.component';
import { ConfigCobroComponent } from '../config-cobro/config-cobro.component';
import { ListTiposplazaVigenciaComponent } from '../list-tiposplaza-vigencia/list-tiposplaza-vigencia.component';

@Component({
    selector: 'app-list-tiposplaza',
    templateUrl: './list-tiposplaza.component.html',
    styleUrls: ['./list-tiposplaza.component.css'],
    changeDetection: ChangeDetectionStrategy.Eager,
    standalone: false
})
export class ListTiposplazaComponent implements OnInit {
  tiposPlaza: TipoPlaza[];
  displayOrNot: boolean = true;
  stringBusqueda: string;
  isLoading:boolean=false;
  readonly columnas = ['codigo', 'descripcion', 'importe', 'addLocal', 'acciones'];
  dataSource = new MatTableDataSource<TipoPlaza>([]);
  private filtro = new TiposplazaPipe();

  @ViewChild(MatSort) set sort(sort: MatSort) {
    this.dataSource.sort = sort;
  }

  constructor(private service: TiposplazaService, private dialog: MatDialog, private aviso: AvisoService) {
    this.dataSource.sortingDataAccessor = (o: TipoPlaza, columna: string) =>
      columna == 'codigo' ? o.codigoTipoPlaza : (o.descripcionTipoPlaza || '').toLocaleLowerCase();
  }

  /** La tabla se alimenta con el resultado del pipe de filtro existente */
  filtrar() {
    this.dataSource.data = this.filtro.transform(this.tiposPlaza, this.stringBusqueda);
  }

  ngOnInit() {
    this.getTiposPlaza();
  }

  getTiposPlaza() {
    this.isLoading=true;
    this.service.getTodos()
      .subscribe(
        (data: TipoPlaza[]) => { //start of (1)
          this.tiposPlaza = data;
          this.filtrar();
          if (this.tiposPlaza.length > 0)
            this.displayOrNot = false;
          else
            this.displayOrNot = true;
        }, //end of (1)
        (error: any) => { console.log(error); this.isLoading = false; }, //(2) second argument
        () => {console.log('all data gets');this.isLoading=false;} //(3) second argument
      );
  }

  confirmarEliminar(codigoTipoPlaza: string) {
    this.dialog.open(DialgAnswerSiNoComponent, {
      data: {
        title: 'Eliminar tipo de plaza',
        message: '¿Seguro que desea eliminar el tipo de plaza ' + codigoTipoPlaza + '? Esta acción no se puede deshacer.',
        respuesta: false,
        textoAceptar: 'Eliminar',
        destructivo: true
      }
    }).afterClosed().subscribe(data => {
      if (data?.respuesta) {
        this.deleteO(codigoTipoPlaza);
      }
    });
  }

  deleteO(codigoTipoPlaza: string) {
    this.service.deleteTipoPlaza(codigoTipoPlaza)
      .subscribe(
        (res: any) => { this.aviso.exito('Tipo de plaza eliminado'); this.getTiposPlaza(); }, //(1)
        (error: any) => console.log(error), //(2)
        () => console.log('deleted') //(3)
      )
  }

  openDialog(): void {
    let dialogRef = this.dialog.open(AddTiposplazaComponent,
      {
        width: '560px',
        maxWidth: '95vw',
      });



    dialogRef.afterClosed().subscribe((creado: TipoPlaza) => {
      this.getTiposPlaza();
      // Recién creado con importe global: primero sus vigencias (de ahí salen los precios) y
      // después si el cobro es diario, mensual o anual
      if (creado && creado.porImporteGlobalTipoPlaza) {
        this.dialog.open(ListTiposplazaVigenciaComponent, {
          width: '560px',
          maxWidth: '95vw',
          data: { tipoPlaza: creado, service: this.service }
        }).afterClosed().subscribe(() => this.openDialogCobro(creado));
      }
    });
  }

  openDialogCobro(o: TipoPlaza): void {
    this.dialog.open(ConfigCobroComponent, {
      width: '900px',
      maxWidth: '95vw',
      data: {
        tipoPlaza: o
      }
    });
  }

  openDialogEdit(o: TipoPlaza): void {
    //console.log(op.numeroPresidenciaEmpleado);
    let dialogRef = this.dialog.open(AddTiposplazaComponent, {
      width: '560px',
      maxWidth: '95vw',
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