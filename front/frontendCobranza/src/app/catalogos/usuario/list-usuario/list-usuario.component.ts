import { Component, OnInit, ChangeDetectionStrategy, ViewChild } from '@angular/core';
import { MatTableDataSource } from '@angular/material/table';
import { MatSort } from '@angular/material/sort';
import { AvisoService } from 'src/app/shared/aviso.service';
import { MatDialog, MatDialogConfig } from '@angular/material/dialog';
import { DialgAnswerSiNoComponent } from 'src/app/shared/dialg-answer-si-no/dialg-answer-si-no.component';
import { DialogInformativoComponent } from 'src/app/shared/dialog-informativo/dialog-informativo.component';
import { AddUsuarioComponent } from '../add-usuario/add-usuario.component';
import { Usuario } from '../model/usuario';
import { UsuarioService } from '../service/usuario.service';

@Component({
    selector: 'app-list-usuario',
    templateUrl: './list-usuario.component.html',
    styleUrls: ['./list-usuario.component.css'],
    changeDetection: ChangeDetectionStrategy.Eager,
    standalone: false
})
export class ListUsuarioComponent implements OnInit {
  usuarios: Usuario[] = [];
  isLoading = false;
  readonly columnas = ['usuario', 'nombre', 'rol', 'estado', 'acciones'];
  dataSource = new MatTableDataSource<Usuario>([]);

  @ViewChild(MatSort) set sort(sort: MatSort) {
    this.dataSource.sort = sort;
  }

  constructor(private service: UsuarioService, public dialog: MatDialog, private aviso: AvisoService) {
    this.dataSource.sortingDataAccessor = (o: Usuario, columna: string) =>
      columna == 'rol' ? this.nombreRol(o.rol) : String(o[columna] ?? '').toLocaleLowerCase();
  }

  ngOnInit() {
    this.getUsuarios();
  }

  getUsuarios() {
    this.isLoading = true;
    this.service.getTodos().subscribe({
      next: (data: Usuario[]) => { this.usuarios = data; this.dataSource.data = data; this.isLoading = false; },
      error: (error: any) => { console.log(error); this.isLoading = false; }
    });
  }

  nombreRol(rol: string): string {
    return rol == 'ADMIN' ? 'Administrador' : 'Consulta';
  }

  openDialog(usuario?: Usuario): void {
    const dialogRef = this.dialog.open(AddUsuarioComponent, {
      width: '560px',
      maxWidth: '95vw',
      data: usuario ? { usuario: usuario } : null
    });
    dialogRef.afterClosed().subscribe(guardado => {
      if (guardado) {
        this.aviso.exito('Usuario guardado');
      }
      this.getUsuarios();
    });
  }

  deleteUsuario(usuario: string) {
    const dConfig = new MatDialogConfig();
    dConfig.data = {
      title: "Eliminar usuario",
      message: '¿Seguro que desea eliminar el usuario ' + usuario + '? Esta acción no se puede deshacer.',
      respuesta: false,
      textoAceptar: 'Eliminar',
      destructivo: true
    };
    this.dialog.open(DialgAnswerSiNoComponent, dConfig).afterClosed().subscribe(data => {
      if (data?.respuesta) {
        this.service.deleteUsuario(usuario).subscribe({
          next: () => { this.aviso.exito('Usuario eliminado'); this.getUsuarios(); },
          error: (error: any) => this.dialog.open(DialogInformativoComponent, {
            data: { title: 'No se pudo eliminar', message: error.error?.mensaje || 'No se pudo eliminar el usuario', tipo: 'error' }
          })
        });
      }
    });
  }
}
