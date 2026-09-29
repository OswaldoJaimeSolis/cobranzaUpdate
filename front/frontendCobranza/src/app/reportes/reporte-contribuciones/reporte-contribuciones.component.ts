import { Component, OnInit } from '@angular/core';
import { MatLegacyDialog as MatDialog } from '@angular/material/legacy-dialog';
import { BusquedaTiposPlazaComponent } from 'src/app/shared/busqueda/busqueda-tipos-plaza/busqueda-tipos-plaza.component';
import { TipoPlaza } from 'src/app/catalogos/tiposplaza/model/tipo-plaza';
import { BusquedaContribuyentesComponent } from 'src/app/shared/busqueda/busqueda-contribuyentes/busqueda-contribuyentes.component';
import { Contribuyente } from 'src/app/catalogos/contribuyente/model/contribuyente';
import { DialogInformativoComponent } from 'src/app/shared/dialog-informativo/dialog-informativo.component';
import { DatePipe } from '@angular/common';
import { ReportesService } from '../service/reportes.service';
import { Recaudador } from 'src/app/catalogos/recaudador/model/recaudador';
import { BusquedaRecaudadoresComponent } from 'src/app/shared/busqueda/busqueda-recaudadores/busqueda-recaudadores.component';
export interface Estado {
  value: number;
  viewValue: string;
}


export interface BusResultTP {
  tipoPlazaSelect: TipoPlaza;
}
export interface BusResultContribuyente {
  contribuyenteSelect: Contribuyente;
}
export interface BusResultRecaudador {
  recaudadorSelect: Recaudador;
}
@Component({
  selector: 'app-reporte-contribuciones',
  templateUrl: './reporte-contribuciones.component.html',
  styleUrls: ['./reporte-contribuciones.component.css']
})
export class ReporteContribucionesComponent implements OnInit {
  fechaInicial: Date = new Date();
  fechaFinal: Date = new Date();
  todosTP: boolean = true;
  todosRecaudador:boolean=true;
  incluirSinRegistro:boolean=false;
  tipoPlaza: TipoPlaza = new TipoPlaza();
  todosContribuyente: boolean = true;
  contribuyente: Contribuyente = new Contribuyente();
  recaudador: Recaudador= new Recaudador();
  estadoSelect: number = 0;
  estadosPago: Estado[] = [{ value: 0, viewValue: 'Todos' },
  { value: 1, viewValue: 'Pagados' },
  { value: 2, viewValue: 'Pendientes' },
  { value: 3, viewValue: 'Ausente' },
  { value: 4, viewValue: 'Sin Registro' }
  ];


  formatoSelect:number=1;
  formatos:Estado[]=[{value:1, viewValue:'PDF'}, {value:2,viewValue:'EXCEL'}];
  isLoading:boolean=false;
  constructor(private dialog: MatDialog, private datePipe: DatePipe, private service: ReportesService) { }


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

  buscarContribuyente() {
    let dBusCon = this.dialog.open(BusquedaContribuyentesComponent);
    dBusCon.afterClosed().subscribe((data: BusResultContribuyente) => {
      if (data != undefined) {
        this.contribuyente = data.contribuyenteSelect;
      }
    });
  }
  buscarRecaudador() {
    let dBusCon = this.dialog.open(BusquedaRecaudadoresComponent);
    dBusCon.afterClosed().subscribe((data: BusResultRecaudador) => {
      if (data != undefined) {
        this.recaudador = data.recaudadorSelect;
      }
    });
  }

  actualizarTodos(activo: boolean, tp: boolean) {
    if (tp) {
      this.todosTP = activo;
    }
    else {
      this.todosContribuyente = activo;
    }
  }
  actualizarTodosR(activo: boolean) {
   this.todosRecaudador=activo;
  }



  recuperaReporte(formato: number, contribuciones:boolean) {
    console.log(this.todosRecaudador);
    let recaudadorFilter="";
    if(!this.todosRecaudador){
      recaudadorFilter=this.recaudador.codigoRecaudador; 
    }
    let fechasCorrectas: boolean = false;
    if (this.fechaInicial != null && this.fechaFinal != null) {
      if (this.fechaInicial.getTime() <= this.fechaFinal.getTime()) {
        if ((!this.todosTP && this.tipoPlaza.codigoTipoPlaza.length > 0) || (this.todosTP)) {

          if ((!this.todosContribuyente && this.contribuyente.codigoContribuyente.length > 0) || (this.todosContribuyente)) {
            this.isLoading=true;
            this.service.getContribucionesPeriodoReporte(this.datePipe.transform(this.fechaInicial, 'dd-MM-yyyy'),
              this.datePipe.transform(this.fechaFinal, 'dd-MM-yyyy'), formato, this.todosTP, this.tipoPlaza.codigoTipoPlaza,
              this.todosContribuyente, this.contribuyente.codigoContribuyente, this.estadoSelect, contribuciones, recaudadorFilter)
              .subscribe((data) => {
                var ieEDGE = navigator.userAgent.match(/Edge/g);
                var ie = navigator.userAgent.match(/.NET/g); // IE 11+
                var oldIE = navigator.userAgent.match(/MSIE/g);
                var name = "cargos" + this.datePipe.transform(this.fechaInicial, 'dd-MM-yyyy') + "_" + this.datePipe.transform(this.fechaFinal, 'dd-MM-yyyy');

                var extension = ".pdf";
                var typefile = 'application/pdf';
                if (formato == 2) {
                  typefile = 'application/vnd.ms-excel';

                  extension = ".xls"
                }
                var blob = new window.Blob([data], { type: typefile });

                if (ie || oldIE || ieEDGE) {
                  var fileName = name + extension;
                  // msSaveBlob only ever existed on IE / legacy Edge and was dropped from
                  // TypeScript's DOM typings; the runtime branch is kept as-is.
                  (window.navigator as any).msSaveBlob(blob, fileName);
                }
                else {
                  var file = new Blob([data], {
                    type: typefile
                  });
                  var fileURL = URL.createObjectURL(file);
                  var a = document.createElement('a');
                  a.href = fileURL;
                  a.target = '_blank';
                  a.download = name + extension;
                  document.body.appendChild(a);
                  a.click();
                }
                this.isLoading=false;

              }, //end of (1)
                (error: any) => {console.log(error), this.isLoading=false}, //(2) second argument
                () => console.log('all data gets') //(3) second argument
              );
          }
          else {
            console.log("AAAAAAAAAAAAAA");
            let dInfo = this.dialog.open(DialogInformativoComponent, {
              data: {
                title: 'Atención!',
                message: 'Debe seleccionar un un contribuyente'
              }
            });
          }
        }
        else {
          console.log("BBBBBBBBBBB");
          let dInfo = this.dialog.open(DialogInformativoComponent, {
            data: {
              title: 'Atención!',
              message: 'Debe seleccionar un tipo de plaza'
            }
          });
        }



      }
      else {
        let dInfo = this.dialog.open(DialogInformativoComponent, {
          data: {
            title: 'Atención!',
            message: 'Fechas incorrectas, la inicial debe ser menor a la final'
          }
        });
      }

    }

  }

}
