import { Component, ChangeDetectionStrategy } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { AuthService } from '../auth/auth.service';
import { CambiarPasswordComponent } from '../auth/cambiar-password/cambiar-password.component';
import { DialogInformativoComponent } from '../shared/dialog-informativo/dialog-informativo.component';

@Component({
    selector: 'app-menu-principal',
    templateUrl: './menu-principal.component.html',
    styleUrls: ['./menu-principal.component.css'],
    changeDetection: ChangeDetectionStrategy.Eager,
    standalone: false
})
export class MenuPrincipalComponent {

  constructor(public auth: AuthService, private dialog: MatDialog) { }

  cambiarPassword() {
    this.dialog.open(CambiarPasswordComponent, { width: '400px' }).afterClosed().subscribe(cambiada => {
      if (cambiada) {
        this.dialog.open(DialogInformativoComponent, { data: { title: 'Listo', message: 'Contraseña actualizada' } });
      }
    });
  }
}
