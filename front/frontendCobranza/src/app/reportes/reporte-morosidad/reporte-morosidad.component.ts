import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { MatDialog } from '@angular/material/dialog';
import { TipoPlaza } from 'src/app/catalogos/tiposplaza/model/tipo-plaza';
import { BusquedaTiposPlazaComponent } from 'src/app/shared/busqueda/busqueda-tipos-plaza/busqueda-tipos-plaza.component';
import { DialogInformativoComponent } from 'src/app/shared/dialog-informativo/dialog-informativo.component';
import { ReportesService } from '../service/reportes.service';

export interface BusResultTP {
  tipoPlazaSelect: TipoPlaza;
}

/** Adeudos de una plaza: una fila por plaza con sus obligaciones vencidas agrupadas */
export interface PlazaMorosa {
  codigoPlaza: string;
  tipoPlaza: string;
  codigoContribuyente: string;
  nombreContribuyente: string;
  licenciaComercio: string;
  tipoCobro: string;
  vencidas: number;
  total: number;
}

@Component({
    selector: 'app-reporte-morosidad',
    templateUrl: './reporte-morosidad.component.html',
    styleUrls: ['./reporte-morosidad.component.css'],
    changeDetection: ChangeDetectionStrategy.Eager,
    standalone: false
})
export class ReporteMorosidadComponent implements OnInit {
  tipoPlaza: TipoPlaza = new TipoPlaza();
  todosTP: boolean = true;
  isLoading: boolean = false;
  consultado: boolean = false;
  observaciones: string = "";
  plazas: PlazaMorosa[] = [];
  total: number = 0;
  columnas = ['plaza', 'tipoPlaza', 'propietario', 'licencia', 'tipoCobro', 'vencidas', 'total', 'acciones'];

  constructor(private dialog: MatDialog, private service: ReportesService) { }

  ngOnInit() {
  }

  buscarTipoPlaza() {
    let dBusTP = this.dialog.open(BusquedaTiposPlazaComponent);
    dBusTP.afterClosed().subscribe((data: BusResultTP) => {
      if (data != undefined) {
        this.tipoPlaza.codigoTipoPlaza = data.tipoPlazaSelect.codigoTipoPlaza;
      }
    });
  }

  private filtroValido(): boolean {
    if (this.todosTP || this.tipoPlaza.codigoTipoPlaza.length > 0) {
      return true;
    }
    this.dialog.open(DialogInformativoComponent, {
      data: { title: 'Atención!', message: 'Debe seleccionar un tipo de plaza' }
    });
    return false;
  }

  consultar() {
    if (!this.filtroValido()) {
      return;
    }
    this.isLoading = true;
    this.service.getMorosidad(this.todosTP, this.tipoPlaza.codigoTipoPlaza).subscribe({
      next: (adeudos: any[]) => {
        const porPlaza = new Map<string, PlazaMorosa>();
        this.total = 0;
        for (const a of adeudos) {
          let plaza = porPlaza.get(a.clavePlaza);
          if (!plaza) {
            plaza = {
              codigoPlaza: a.codigoPlaza, tipoPlaza: a.tipoPlaza, codigoContribuyente: a.codigoContribuyente,
              nombreContribuyente: a.nombreContribuyente, licenciaComercio: a.licenciaComercio,
              tipoCobro: a.tipoCobro, vencidas: 0, total: 0
            };
            porPlaza.set(a.clavePlaza, plaza);
          }
          plaza.vencidas++;
          plaza.total += a.monto;
          this.total += a.monto;
        }
        this.plazas = Array.from(porPlaza.values());
        this.consultado = true;
        this.isLoading = false;
      },
      error: (error: any) => { console.log(error); this.isLoading = false; }
    });
  }

  /** Tipo de plaza seleccionado para los requerimientos; vacío si son todos */
  tipoPlazaRequerimientos(): string {
    return this.todosTP ? "" : this.tipoPlaza.codigoTipoPlaza;
  }

  /** Descarga masiva: un solo PDF con una hoja por cada plaza con adeudo del tipo seleccionado, o de todos si no hay */
  requerimientosMasivo() {
    const codigoTP = this.tipoPlazaRequerimientos();
    const todos = codigoTP.length == 0;
    this.isLoading = true;
    this.service.getRequerimientoPagoReporte(todos, codigoTP, "", this.observaciones).subscribe({
      next: (data) => {
        this.entregar(data, 1, "requerimientos_pago_" + (todos ? "Todos" : codigoTP), true);
        this.isLoading = false;
      },
      error: (error: any) => { console.log(error); this.isLoading = false; }
    });
  }

  /** Requerimiento de una sola plaza: se descarga o se abre en otra pestaña para imprimirlo */
  requerimientoPlaza(plaza: PlazaMorosa, descargar: boolean) {
    this.isLoading = true;
    this.service.getRequerimientoPagoReporte(false, plaza.tipoPlaza, plaza.codigoPlaza, this.observaciones).subscribe({
      next: (data) => {
        this.entregar(data, 1, "requerimiento_" + plaza.codigoPlaza, descargar);
        this.isLoading = false;
      },
      error: (error: any) => { console.log(error); this.isLoading = false; }
    });
  }

  private entregar(data: ArrayBuffer, formato: number, nombre: string, descargar: boolean) {
    const tipo = formato == 2 ? 'application/vnd.openxmlformats-officedocument.spreadsheetml.sheet' : 'application/pdf';
    const url = URL.createObjectURL(new Blob([data], { type: tipo }));
    if (!descargar) {
      window.open(url, '_blank');
      return;
    }
    const a = document.createElement('a');
    a.href = url;
    a.download = nombre + (formato == 2 ? ".xlsx" : ".pdf");
    document.body.appendChild(a);
    a.click();
    document.body.removeChild(a);
  }
}
