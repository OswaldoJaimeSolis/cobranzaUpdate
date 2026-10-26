import { Component, Inject, ChangeDetectionStrategy } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { Usuario } from '../model/usuario';
import { UsuarioService } from '../service/usuario.service';

export interface DialogData {
  usuario: Usuario;
}

@Component({
    selector: 'app-add-usuario',
    templateUrl: './add-usuario.component.html',
    styleUrls: ['./add-usuario.component.css'],
    changeDetection: ChangeDetectionStrategy.Eager,
    standalone: false
})
export class AddUsuarioComponent {
  oFinal: Usuario = new Usuario();
  encabezado = "Agregar usuario";
  editarId = false;
  error = "";
  roles = [{ valor: 'ADMIN', nombre: 'Administrador' }, { valor: 'CONSULTA', nombre: 'Consulta (sólo reportes)' }];

  constructor(public dialogRef: MatDialogRef<AddUsuarioComponent>,
    @Inject(MAT_DIALOG_DATA) public dd: DialogData,
    private service: UsuarioService) {
    if (this.dd != null) {
      this.oFinal.usuario = dd.usuario.usuario;
      this.oFinal.nombre = dd.usuario.nombre;
      this.oFinal.rol = dd.usuario.rol;
      this.oFinal.activo = dd.usuario.activo === true;
      this.editarId = true;
      this.encabezado = "Editar usuario";
    }
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onSave() {
    const peticion = this.editarId ? this.service.updateUsuario(this.oFinal) : this.service.addUsuario(this.oFinal);
    peticion.subscribe({
      next: () => this.dialogRef.close(true),
      error: (error: any) => this.error = error.error?.mensaje || 'No se pudo guardar el usuario'
    });
  }
}
