import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
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

  constructor(private service: UsuarioService, public dialog: MatDialog) { }

  ngOnInit() {
    this.getUsuarios();
  }

  getUsuarios() {
    this.isLoading = true;
    this.service.getTodos().subscribe({
      next: (data: Usuario[]) => { this.usuarios = data; this.isLoading = false; },
      error: (error: any) => { console.log(error); this.isLoading = false; }
    });
  }

  nombreRol(rol: string): string {
    return rol == 'ADMIN' ? 'Administrador' : 'Consulta';
  }

  openDialog(usuario?: Usuario): void {
    const dialogRef = this.dialog.open(AddUsuarioComponent, {
      width: '500px',
      data: usuario ? { usuario: usuario } : null
    });
    dialogRef.afterClosed().subscribe(() => this.getUsuarios());
  }

  deleteUsuario(usuario: string) {
    const dConfig = new MatDialogConfig();
    dConfig.data = {
      title: "Eliminando",
      message: '¿Está seguro de eliminar el usuario ' + usuario + '?',
      respuesta: false
    };
    this.dialog.open(DialgAnswerSiNoComponent, dConfig).afterClosed().subscribe(data => {
      if (data?.respuesta) {
        this.service.deleteUsuario(usuario).subscribe({
          next: () => this.getUsuarios(),
          error: (error: any) => this.dialog.open(DialogInformativoComponent, {
            data: { title: 'Atención', message: error.error?.mensaje || 'No se pudo eliminar el usuario' }
          })
        });
      }
    });
  }
}
