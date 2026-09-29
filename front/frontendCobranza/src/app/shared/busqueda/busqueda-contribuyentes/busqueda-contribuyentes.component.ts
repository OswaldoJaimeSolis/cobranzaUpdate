import { Component, OnInit } from '@angular/core';
import { Contribuyente } from 'src/app/catalogos/contribuyente/model/contribuyente';
import { MatDialogRef, MatDialog } from '@angular/material/dialog';
import { MatTableDataSource } from '@angular/material/table';
import { ContribuyenteService } from 'src/app/catalogos/contribuyente/service/contribuyente.service';
import { AddContribuyenteComponent } from 'src/app/catalogos/contribuyente/add-contribuyente/add-contribuyente.component';

@Component({
    selector: 'app-busqueda-contribuyentes',
    templateUrl: './busqueda-contribuyentes.component.html',
    styleUrls: ['./busqueda-contribuyentes.component.css'],
    standalone: false
})
export class BusquedaContribuyentesComponent implements OnInit {
  contribuyentes: Contribuyente[] = [];
  displayedColumns: string[] = ['codigo', 'nombre', 'apePaterno', 'apeMaterno'];
  dataSource = new MatTableDataSource(this.contribuyentes);
  constructor(private service: ContribuyenteService, private dialogRef: MatDialogRef<BusquedaContribuyentesComponent>, private dialog: MatDialog) { }

  ngOnInit() {
    this.getContribuyentes();
  }

  applyFilter(filterValue: string) {

    this.dataSource.filter = filterValue.trim().toLowerCase();
  }

  getContribuyentes() {
    this.service.getTodos()
      .subscribe((data: Contribuyente[]) => {
        this.contribuyentes = data;
        this.dataSource = new MatTableDataSource(this.contribuyentes);
      },
        (error: any) => console.log(error), //(2) second argument
      );
  }

  openAddContribuyente() {
    let dAdd = this.dialog.open(AddContribuyenteComponent);
    dAdd.afterClosed().subscribe((result) => {
      this.getContribuyentes(), console.error();

    }, (error: any) => console.log(error));
  }

  selectRow(row: Contribuyente) {
    console.log(row);

    this.dialogRef.close({ contribuyenteSelect: row });
  }

  aceptar() {

  }

  cancelar() {

  }

}
