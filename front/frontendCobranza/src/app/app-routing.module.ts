import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { ListContribuyenteComponent } from './catalogos/contribuyente/list-contribuyente/list-contribuyente.component';
import { ListRecaudadorComponent } from './catalogos/recaudador/list-recaudador/list-recaudador.component';
import { ListTiposplazaComponent } from './catalogos/tiposplaza/list-tiposplaza/list-tiposplaza.component';
import { ListPropietarioPlazaComponent } from './catalogos/propietarioplaza/list-propietario-plaza/list-propietario-plaza.component';
import { ReporteContribucionesComponent } from './reportes/reporte-contribuciones/reporte-contribuciones.component';
import { ReporteContribuyentesComponent } from './reportes/reporte-contribuyentes/reporte-contribuyentes.component';
import { ListPropietarioPlazaJbComponent } from './catalogos/propietarioplaza/list-propietario-plaza-jb/list-propietario-plaza-jb.component';
import { ReporteMorosidadComponent } from './reportes/reporte-morosidad/reporte-morosidad.component';
import { BitacoraCondonacionesComponent } from './reportes/bitacora-condonaciones/bitacora-condonaciones.component';
import { ListUsuarioComponent } from './catalogos/usuario/list-usuario/list-usuario.component';
import { LoginComponent } from './auth/login/login.component';
import { sesionGuard } from './auth/auth.guard';

const soloAdmin = { roles: ['ADMIN'] };

const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: '', pathMatch: 'full', canActivate: [sesionGuard], children: [] },
  { path: 'contribuyentes', component: ListContribuyenteComponent, canActivate: [sesionGuard], data: soloAdmin },
  { path: 'recaudadores', component: ListRecaudadorComponent, canActivate: [sesionGuard], data: soloAdmin },
  { path: 'tiposPlaza', component: ListTiposplazaComponent, canActivate: [sesionGuard], data: soloAdmin },
  { path: 'propietarioPlaza', component: ListPropietarioPlazaJbComponent, canActivate: [sesionGuard], data: soloAdmin },
  { path: 'usuarios', component: ListUsuarioComponent, canActivate: [sesionGuard], data: soloAdmin },
  { path: 'reporteContribuciones', component: ReporteContribucionesComponent, canActivate: [sesionGuard] },
  { path: 'reporteContribuyentes', component: ReporteContribuyentesComponent, canActivate: [sesionGuard] },
  { path: 'reporteMorosidad', component: ReporteMorosidadComponent, canActivate: [sesionGuard] },
  { path: 'bitacoraCondonaciones', component: BitacoraCondonacionesComponent, canActivate: [sesionGuard] },
  { path: '**', redirectTo: '' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {})],
  exports: [RouterModule]
})
export class AppRoutingModule { }
