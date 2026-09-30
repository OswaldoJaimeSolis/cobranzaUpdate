import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { TiposplazaService } from 'src/app/catalogos/tiposplaza/service/tiposplaza.service';
import { TipoPlaza } from 'src/app/catalogos/tiposplaza/model/tipo-plaza';
import { MatDialogRef, MatDialog } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { AddContribuyenteComponent } from 'src/app/catalogos/contribuyente/add-contribuyente/add-contribuyente.component';
import { AddTiposplazaComponent } from 'src/app/catalogos/tiposplaza/add-tiposplaza/add-tiposplaza.component';

@Component({
    selector: 'app-busqueda-tipos-plaza',
    templateUrl: './busqueda-tipos-plaza.component.html',
    styleUrls: ['./busqueda-tipos-plaza.component.css'],
    changeDetection: ChangeDetectionStrategy.Eager,
    standalone: false
})
export class BusquedaTiposPlazaComponent implements OnInit {
  tiposPlaza: TipoPlaza[] = [];
  displayedColumns: string[] = ['codigo', 'descripcion'];
  dataSource = new MatTableDataSource(this.tiposPlaza);
  constructor(private service: TiposplazaService, private dialogRef: MatDialogRef<BusquedaTiposPlazaComponent>, private dialog: MatDialog) { }

  ngOnInit() {
    this.getTiposPlaza();
  }

  applyFilter(filterValue: string) {

    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  getTiposPlaza() {
    this.service.getTodos()
      .subscribe((data: TipoPlaza[]) => {
        this.tiposPlaza = data;
        this.dataSource = new MatTableDataSource(this.tiposPlaza);
      },
        (error: any) => console.log(error), //(2) second argument
      );
  }

  openAddTipoPlaza() {
    let dAdd = this.dialog.open(AddTiposplazaComponent);
    dAdd.afterClosed().subscribe((result) => {
      this.getTiposPlaza(), console.error();

    }, (error: any) => console.log(error));
  }

  selectRow(row: TipoPlaza) {
    console.log(row);

    this.dialogRef.close({ tipoPlazaSelect: row });
  }

  aceptar() {

  }

  cancelar() {

  }

}
