import { Component, OnInit } from '@angular/core';
import { MatDialog } from '@angular/material';
import { TipoPlaza } from 'src/app/catalogos/tiposplaza/model/tipo-plaza';
import { BusquedaTiposPlazaComponent } from 'src/app/shared/busqueda/busqueda-tipos-plaza/busqueda-tipos-plaza.component';
import { ReportesService } from '../service/reportes.service';
import { DialogInformativoComponent } from 'src/app/shared/dialog-informativo/dialog-informativo.component';
export interface BusResultTP {
  tipoPlazaSelect: TipoPlaza;
}
@Component({
  selector: 'app-reporte-contribuyentes',
  templateUrl: './reporte-contribuyentes.component.html',
  styleUrls: ['./reporte-contribuyentes.component.css']
})
export class ReporteContribuyentesComponent implements OnInit {
  tipoPlaza: TipoPlaza = new TipoPlaza();
  todosTP: boolean = true;
  isLoading: boolean = false;
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
  recuperaReporte(formato: number) {
    let fechasCorrectas: boolean = false;

    if ((!this.todosTP && this.tipoPlaza.codigoTipoPlaza.length > 0) || (this.todosTP)) {


      this.isLoading = true;
      this.service.getContribuyentesTPReporte(formato, this.todosTP, this.tipoPlaza.codigoTipoPlaza)
        .subscribe((data) => {
          var ieEDGE = navigator.userAgent.match(/Edge/g);
          var ie = navigator.userAgent.match(/.NET/g); // IE 11+
          var oldIE = navigator.userAgent.match(/MSIE/g);
          var name = "contribuyentes_";
          if(this.todosTP){
            name=name+"Todos";
          }
          else{
            name= name+this.tipoPlaza.codigoTipoPlaza;
          }
          

          var extension = ".pdf";
          var typefile = 'application/pdf';
          if (formato == 2) {
            typefile = 'application/vnd.ms-excel';

            extension = ".xls"
          }
          var blob = new window.Blob([data], { type: typefile });

          if (ie || oldIE || ieEDGE) {
            var fileName = name + extension;
            window.navigator.msSaveBlob(blob, fileName);
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
          this.isLoading = false;

        }, //end of (1)
          (error: any) => { console.log(error), this.isLoading = false }, //(2) second argument
          () => console.log('all data gets') //(3) second argument
        );
    }

    else {
      
      let dInfo = this.dialog.open(DialogInformativoComponent, {
        data: {
          title: 'Atención!',
          message: 'Debe seleccionar un tipo de plaza'
        }
      });
    }





  }
}
