import { Component, OnInit } from '@angular/core';
import { TipoPlaza } from 'src/app/catalogos/tiposplaza/model/tipo-plaza';
import { MatDialogRef, MatDialog } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { RecaudadorService } from 'src/app/catalogos/recaudador/service/recaudador.service';
import { Recaudador } from 'src/app/catalogos/recaudador/model/recaudador';
import { AddRecaudadorComponent } from 'src/app/catalogos/recaudador/add-recaudador/add-recaudador.component';

@Component({
  selector: 'app-busqueda-recaudadores',
  templateUrl: './busqueda-recaudadores.component.html',
  styleUrls: ['./busqueda-recaudadores.component.css']
})
export class BusquedaRecaudadoresComponent implements OnInit {
  recaudadores: Recaudador[];
  displayedColumns: string[] = ['codigo', 'descripcion'];
  dataSource = new MatTableDataSource(this.recaudadores);
  constructor(private service: RecaudadorService, private dialogRef: MatDialogRef<BusquedaRecaudadoresComponent>, private dialog: MatDialog) { }

  ngOnInit() {
    this.getRecaudadores();
  }

  applyFilter(filterValue: string) {

    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  getRecaudadores() {
    this.service.getTodos()
      .subscribe((data: Recaudador[]) => {
        this.recaudadores = data;
        this.dataSource = new MatTableDataSource(this.recaudadores);
      },
        (error: any) => console.log(error), //(2) second argument
      );
  }

  openAddRecaudador() {
    let dAdd = this.dialog.open(AddRecaudadorComponent);
    dAdd.afterClosed().subscribe((result) => {
      this.getRecaudadores(), console.error();

    }, (error: any) => console.log(error));
  }

  selectRow(row: Recaudador) {
    console.log(row);

    this.dialogRef.close({ recaudadorSelect: row });
  }

  aceptar() {

  }

  cancelar() {

  }

}
