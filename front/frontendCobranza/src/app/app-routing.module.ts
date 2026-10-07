import { NgModule } from '@angular/core';
import { Routes, RouterModule } from '@angular/router';
import { ListContribuyenteComponent } from './catalogos/contribuyente/list-contribuyente/list-contribuyente.component';
import { ListRecaudadorComponent } from './catalogos/recaudador/list-recaudador/list-recaudador.component';
import { ListTiposplazaComponent } from './catalogos/tiposplaza/list-tiposplaza/list-tiposplaza.component';
import { ListPropietarioPlazaComponent } from './catalogos/propietarioplaza/list-propietario-plaza/list-propietario-plaza.component';
import { ReporteContribucionesComponent } from './reportes/reporte-contribuciones/reporte-contribuciones.component';
import { ReporteContribuyentesComponent } from './reportes/reporte-contribuyentes/reporte-contribuyentes.component';
import { ListPropietarioPlazaJbComponent } from './catalogos/propietarioplaza/list-propietario-plaza-jb/list-propietario-plaza-jb.component';

const routes: Routes = [
  { path: 'contribuyentes', component: ListContribuyenteComponent },
  { path: 'recaudadores', component: ListRecaudadorComponent },
  { path: 'tiposPlaza', component: ListTiposplazaComponent },
  { path: 'propietarioPlaza', component: ListPropietarioPlazaJbComponent },
  { path: 'reporteContribuciones', component: ReporteContribucionesComponent },
  { path: 'reporteContribuyentes', component: ReporteContribuyentesComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes, {})],
  exports: [RouterModule]
})
export class AppRoutingModule { }
