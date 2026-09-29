import { MatButtonModule } from '@angular/material/button';
import { MatCheckboxModule } from '@angular/material/checkbox';
import { MatNativeDateModule, MAT_DATE_LOCALE } from '@angular/material/core';
import { MatDatepickerModule } from '@angular/material/datepicker';
import { MatDialogModule } from '@angular/material/dialog';
import { MatIconModule } from '@angular/material/icon';
import { MatInputModule } from '@angular/material/input';
import { MatListModule } from '@angular/material/list';
import { MatMenuModule } from '@angular/material/menu';
import { MatProgressBarModule } from '@angular/material/progress-bar';
import { MatProgressSpinnerModule } from '@angular/material/progress-spinner';
import { MatSelectModule } from '@angular/material/select';
import { MatTableModule } from '@angular/material/table';
import { MatToolbarModule } from '@angular/material/toolbar';
import { HttpClientModule } from '@angular/common/http';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { FormsModule } from '@angular/forms';  //for the form element

import { BrowserModule } from '@angular/platform-browser';
import { NgModule } from '@angular/core';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { MenuPrincipalComponent } from './menu-principal/menu-principal.component';
import { AddContribuyenteComponent } from './catalogos/contribuyente/add-contribuyente/add-contribuyente.component';
import { ListContribuyenteComponent } from './catalogos/contribuyente/list-contribuyente/list-contribuyente.component';

import { FilterPipe } from './catalogos/contribuyente/filter.pipe';
import { ContribuyenteService } from './catalogos/contribuyente/service/contribuyente.service';
import { ListRecaudadorComponent } from './catalogos/recaudador/list-recaudador/list-recaudador.component';
import { AddRecaudadorComponent } from './catalogos/recaudador/add-recaudador/add-recaudador.component';
import { RecaudadorPipe } from './catalogos/recaudador/filter/recaudador.pipe';
import { RecaudadorService } from './catalogos/recaudador/service/recaudador.service';
import { AddTiposplazaComponent } from './catalogos/tiposplaza/add-tiposplaza/add-tiposplaza.component';
import { ListTiposplazaComponent } from './catalogos/tiposplaza/list-tiposplaza/list-tiposplaza.component';
import { TiposplazaPipe } from './catalogos/tiposplaza/filter/tiposplaza.pipe';
import { AddTiposplazaVigenciaComponent } from './catalogos/tiposplaza/add-tiposplaza-vigencia/add-tiposplaza-vigencia.component';
import { DialogInformativoComponent } from './shared/dialog-informativo/dialog-informativo.component';
import { ListTiposplazaVigenciaComponent } from './catalogos/tiposplaza/list-tiposplaza-vigencia/list-tiposplaza-vigencia.component';
import { TiposplazaService } from './catalogos/tiposplaza/service/tiposplaza.service';
import { DialgAnswerSiNoComponent } from './shared/dialg-answer-si-no/dialg-answer-si-no.component';
import { BusquedaTiposPlazaComponent } from './shared/busqueda/busqueda-tipos-plaza/busqueda-tipos-plaza.component';
import { ListTipoPlazaRecaudadorComponent } from './catalogos/recaudador/list-tipo-plaza-recaudador/list-tipo-plaza-recaudador.component';
import { ListPropietarioPlazaComponent } from './catalogos/propietarioplaza/list-propietario-plaza/list-propietario-plaza.component';
import { AddPropietarioPlazaComponent } from './catalogos/propietarioplaza/add-propietario-plaza/add-propietario-plaza.component';
import { PropietarioPlazaPipe } from './catalogos/propietarioplaza/filter/propietario-plaza.pipe';
import { BusquedaContribuyentesComponent } from './shared/busqueda/busqueda-contribuyentes/busqueda-contribuyentes.component';
import { PropietarioPlazaService } from './catalogos/propietarioplaza/service/propietario-plaza.service';
import { DatePipe } from '@angular/common';
import { ReporteContribucionesComponent } from './reportes/reporte-contribuciones/reporte-contribuciones.component';
import { ReportesService } from './reportes/service/reportes.service';
import { ReporteContribuyentesComponent } from './reportes/reporte-contribuyentes/reporte-contribuyentes.component';
import { ListPropietarioPlazaJbComponent } from './catalogos/propietarioplaza/list-propietario-plaza-jb/list-propietario-plaza-jb.component';
import { BusquedaRecaudadoresComponent } from './shared/busqueda/busqueda-recaudadores/busqueda-recaudadores.component';



@NgModule({
    declarations: [
        AppComponent,
        MenuPrincipalComponent,
        AddContribuyenteComponent,
        ListContribuyenteComponent,
        FilterPipe,
        ListRecaudadorComponent,
        AddRecaudadorComponent,
        RecaudadorPipe,
        AddTiposplazaComponent,
        ListTiposplazaComponent,
        TiposplazaPipe,
        AddTiposplazaVigenciaComponent,
        DialogInformativoComponent,
        ListTiposplazaVigenciaComponent,
        DialgAnswerSiNoComponent,
        BusquedaTiposPlazaComponent,
        ListTipoPlazaRecaudadorComponent,
        ListPropietarioPlazaComponent,
        AddPropietarioPlazaComponent,
        PropietarioPlazaPipe,
        BusquedaContribuyentesComponent,
        ReporteContribucionesComponent,
        ReporteContribuyentesComponent,
        ListPropietarioPlazaJbComponent,
        BusquedaRecaudadoresComponent
    ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        MatTableModule,
        MatNativeDateModule,
        MatDatepickerModule,
        MatMenuModule,
        BrowserModule,
        MatToolbarModule,
        MatButtonModule,
        MatListModule,
        MatIconModule,
        MatDialogModule,
        MatInputModule,
        MatCheckboxModule,
        MatSelectModule,
        BrowserAnimationsModule,
        FormsModule,
        BrowserModule,
        HttpClientModule,
        MatProgressBarModule,
        MatProgressSpinnerModule
    ],
    providers: [ContribuyenteService, RecaudadorService, TiposplazaService, PropietarioPlazaService, ReportesService, { provide: MAT_DATE_LOCALE, useValue: 'es-ES' }, DatePipe],
    bootstrap: [AppComponent]
})
export class AppModule { }
