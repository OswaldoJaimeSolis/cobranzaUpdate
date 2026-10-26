import { Component, Inject, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { MatDialog, MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';
import { TipoPlaza } from '../model/tipo-plaza';
import { ConfiguracionCobro, DiaMes, PeriodoAnual } from '../model/configuracion-cobro';
import { TiposplazaService } from '../service/tiposplaza.service';
import { DialogInformativoComponent } from 'src/app/shared/dialog-informativo/dialog-informativo.component';
import { AvisoService } from 'src/app/shared/aviso.service';

export interface DialogData {
  tipoPlaza: TipoPlaza;
}

@Component({
    selector: 'app-config-cobro',
    templateUrl: './config-cobro.component.html',
    styleUrls: ['./config-cobro.component.css'],
    changeDetection: ChangeDetectionStrategy.Eager,
    standalone: false
})
export class ConfigCobroComponent implements OnInit {
  readonly diasSemana = [
    { valor: 1, nombre: 'Lunes' }, { valor: 2, nombre: 'Martes' }, { valor: 3, nombre: 'Miércoles' },
    { valor: 4, nombre: 'Jueves' }, { valor: 5, nombre: 'Viernes' }, { valor: 6, nombre: 'Sábado' },
    { valor: 7, nombre: 'Domingo' }];
  readonly meses = ['Enero', 'Febrero', 'Marzo', 'Abril', 'Mayo', 'Junio', 'Julio', 'Agosto', 'Septiembre',
    'Octubre', 'Noviembre', 'Diciembre'];
  // Febrero admite 29 por los años bisiestos
  readonly diasPorMes = [31, 29, 31, 30, 31, 30, 31, 31, 30, 31, 30, 31];

  config: ConfiguracionCobro = new ConfiguracionCobro();
  // Día que se está capturando para cada mes (índice 0 = enero)
  diaNuevo: number[] = new Array(12).fill(null);
  readonly anios = Array.from({ length: 31 }, (_, i) => 2020 + i);
  // Calendario mensual en edición: null = todos los años
  anioSeleccionado: number = new Date().getFullYear();
  isLoading = false;

  constructor(public dialogRef: MatDialogRef<ConfigCobroComponent>,
    @Inject(MAT_DIALOG_DATA) public dd: DialogData,
    private service: TiposplazaService, private dialog: MatDialog, private aviso: AvisoService) {
    this.config.codigoTipoPlaza = dd.tipoPlaza.codigoTipoPlaza;
  }

  ngOnInit() {
    this.isLoading = true;
    this.service.getConfiguracionCobro(this.dd.tipoPlaza.codigoTipoPlaza).subscribe({
      next: (data: ConfiguracionCobro) => {
        this.config = data;
        this.isLoading = false;
      },
      error: (error: any) => { console.log(error); this.isLoading = false; }
    });
  }

  tieneDiaSemana(dia: number): boolean {
    return this.config.diasSemana.includes(dia);
  }

  cambiarDiaSemana(dia: number, activo: boolean) {
    this.config.diasSemana = this.config.diasSemana.filter(d => d != dia);
    if (activo) {
      this.config.diasSemana.push(dia);
    }
  }

  private esDelAnio(d: DiaMes, anio: number): boolean {
    return (d.anio ?? null) === anio;
  }

  tieneFechasPropias(anio: number): boolean {
    return this.config.diasMes.some(d => d.anio == anio);
  }

  quitarAnio(anio: number) {
    this.config.diasMes = this.config.diasMes.filter(d => !this.esDelAnio(d, anio));
  }

  /** Días del mes; con año elegido considera los bisiestos */
  largoMes(mes: number): number {
    return this.anioSeleccionado != null ? new Date(this.anioSeleccionado, mes, 0).getDate() : this.diasPorMes[mes - 1];
  }

  diasDelMes(mes: number): DiaMes[] {
    return this.config.diasMes.filter(d => d.mes == mes && this.esDelAnio(d, this.anioSeleccionado))
      .sort((a, b) => (a.ultimoDia ? 32 : a.dia) - (b.ultimoDia ? 32 : b.dia));
  }

  agregarDia(mes: number) {
    const dia = Number(this.diaNuevo[mes - 1]);
    const largo = this.largoMes(mes);
    if (!Number.isInteger(dia) || dia < 1 || dia > largo) {
      this.mensaje(this.meses[mes - 1] + (this.anioSeleccionado != null ? ' de ' + this.anioSeleccionado : '')
        + ' sólo admite días del 1 al ' + largo);
      return;
    }
    if (!this.config.diasMes.some(d => d.mes == mes && !d.ultimoDia && d.dia == dia && this.esDelAnio(d, this.anioSeleccionado))) {
      this.config.diasMes.push({ anio: this.anioSeleccionado, mes: mes, dia: dia, ultimoDia: false });
    }
    this.diaNuevo[mes - 1] = null;
  }

  agregarUltimoDia(mes: number) {
    if (!this.config.diasMes.some(d => d.mes == mes && d.ultimoDia && this.esDelAnio(d, this.anioSeleccionado))) {
      this.config.diasMes.push({ anio: this.anioSeleccionado, mes: mes, dia: null, ultimoDia: true });
    }
  }

  diasDelAnioVacio(): boolean {
    return !this.config.diasMes.some(d => this.esDelAnio(d, this.anioSeleccionado));
  }

  quitarDia(dia: DiaMes) {
    this.config.diasMes = this.config.diasMes.filter(d => d !== dia);
  }

  agregarPeriodo() {
    const periodo = new PeriodoAnual();
    const anios = this.config.periodosAnuales.map(p => p.anio);
    periodo.anio = anios.length > 0 ? Math.max(...anios) + 1 : new Date().getFullYear() + 1;
    this.config.periodosAnuales.push(periodo);
  }

  quitarPeriodo(periodo: PeriodoAnual) {
    this.config.periodosAnuales = this.config.periodosAnuales.filter(p => p !== periodo);
  }

  mensaje(texto: string) {
    this.dialog.open(DialogInformativoComponent, { data: { title: 'Revise la configuración', message: texto, tipo: 'aviso' } });
  }

  onCancel(): void {
    this.dialogRef.close();
  }

  onSave() {
    this.isLoading = true;
    this.service.saveConfiguracionCobro(this.config).subscribe({
      next: () => {
        this.isLoading = false;
        this.aviso.exito('Configuración de cobro guardada');
        this.dialogRef.close(true);
      },
      error: (error: any) => {
        this.isLoading = false;
        // El backend valida la configuración completa y responde el motivo
        this.mensaje(error.error && error.error.mensaje ? error.error.mensaje : 'No se pudo guardar la configuración');
      }
    });
  }
}
