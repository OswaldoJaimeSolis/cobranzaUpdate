import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { DatePipe } from '@angular/common';
import { ReportesService } from '../service/reportes.service';

@Component({
    selector: 'app-bitacora-condonaciones',
    templateUrl: './bitacora-condonaciones.component.html',
    styleUrls: ['./bitacora-condonaciones.component.css'],
    changeDetection: ChangeDetectionStrategy.Eager,
    standalone: false
})
export class BitacoraCondonacionesComponent implements OnInit {
  fechaInicial: Date = new Date();
  fechaFinal: Date = new Date();
  isLoading: boolean = false;
  consultado: boolean = false;
  condonaciones: any[] = [];
  total: number = 0;
  columnas = ['fecha', 'usuario', 'plaza', 'propietario', 'concepto', 'monto', 'motivo', 'origen'];

  constructor(private service: ReportesService, private datePipe: DatePipe) {
    this.fechaInicial.setDate(1);
  }

  ngOnInit() {
    this.consultar();
  }

  /** Descarga en PDF la bitácora del período seleccionado */
  exportarPdf() {
    const inicial = this.datePipe.transform(this.fechaInicial, 'dd-MM-yyyy');
    const final = this.datePipe.transform(this.fechaFinal, 'dd-MM-yyyy');
    this.isLoading = true;
    this.service.getCondonacionesReporte(inicial, final).subscribe({
      next: (data: ArrayBuffer) => {
        const url = URL.createObjectURL(new Blob([data], { type: 'application/pdf' }));
        const a = document.createElement('a');
        a.href = url;
        a.download = 'condonaciones_' + inicial + '_' + final + '.pdf';
        document.body.appendChild(a);
        a.click();
        document.body.removeChild(a);
        URL.revokeObjectURL(url);
        this.isLoading = false;
      },
      error: (error: any) => { console.log(error); this.isLoading = false; }
    });
  }

  consultar() {
    this.isLoading = true;
    this.service.getCondonaciones(this.datePipe.transform(this.fechaInicial, 'dd-MM-yyyy'),
      this.datePipe.transform(this.fechaFinal, 'dd-MM-yyyy')).subscribe({
        next: (data: any[]) => {
          this.condonaciones = data;
          this.total = data.reduce((suma, c) => suma + c.montoCondonado, 0);
          this.consultado = true;
          this.isLoading = false;
        },
        error: (error: any) => { console.log(error); this.isLoading = false; }
      });
  }
}
