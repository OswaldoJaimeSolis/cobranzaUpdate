import { Component, ChangeDetectionStrategy, EventEmitter, Input, Output } from '@angular/core';
import { Router } from '@angular/router';
import { MatDialog } from '@angular/material/dialog';
import { AuthService } from '../auth/auth.service';
import { CambiarPasswordComponent } from '../auth/cambiar-password/cambiar-password.component';
import { AvisoService } from '../shared/aviso.service';

interface OpcionMenu {
  ruta: string;
  texto: string;
  icono: string;
}

@Component({
    selector: 'app-menu-principal',
    templateUrl: './menu-principal.component.html',
    styleUrls: ['./menu-principal.component.css'],
    changeDetection: ChangeDetectionStrategy.Eager,
    standalone: false
})
export class MenuPrincipalComponent {
  /** barra: menús + cuenta en la barra superior; cuenta: sólo cuenta (móvil); lateral: lista del panel lateral */
  @Input() modo: 'barra' | 'cuenta' | 'lateral' = 'barra';
  @Output() navego = new EventEmitter<void>();

  readonly catalogosMenu: OpcionMenu[] = [
    { ruta: 'contribuyentes', texto: 'Contribuyentes', icono: 'people' },
    { ruta: 'recaudadores', texto: 'Recaudadores', icono: 'badge' },
    { ruta: 'tiposPlaza', texto: 'Tipos de plaza', icono: 'category' },
    { ruta: 'propietarioPlaza', texto: 'Plazas', icono: 'storefront' },
    { ruta: 'usuarios', texto: 'Usuarios', icono: 'manage_accounts' },
  ];
  readonly reportesMenu: OpcionMenu[] = [
    { ruta: 'reporteContribuciones', texto: 'Reporte de contribuciones', icono: 'receipt_long' },
    { ruta: 'reporteContribuyentes', texto: 'Reporte de contribuyentes', icono: 'assignment_ind' },
    { ruta: 'reporteMorosidad', texto: 'Morosidad y requerimientos de pago', icono: 'warning' },
    { ruta: 'bitacoraCondonaciones', texto: 'Bitácora de condonaciones', icono: 'volunteer_activism' },
  ];
  readonly rutasCatalogos = this.catalogosMenu.map(o => o.ruta);
  readonly rutasReportes = this.reportesMenu.map(o => o.ruta);

  constructor(public auth: AuthService, private dialog: MatDialog, private router: Router,
    private aviso: AvisoService) { }

  esActivo(rutas: string[]): boolean {
    const actual = this.router.url.split(/[/?#;]/)[1] || '';
    return rutas.includes(actual);
  }

  nombreRol(): string {
    const rol = this.auth.sesion?.rol;
    return rol == 'ADMIN' ? 'Administrador' : rol == 'CONSULTA' ? 'Consulta' : '';
  }

  cambiarPassword() {
    this.dialog.open(CambiarPasswordComponent, { width: '440px', maxWidth: '95vw' }).afterClosed().subscribe(cambiada => {
      if (cambiada) {
        this.aviso.exito('Contraseña actualizada');
      }
    });
  }
}
