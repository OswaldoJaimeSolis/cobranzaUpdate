import { Injectable } from '@angular/core';
import { Constantes } from 'src/app/shared/constantes';
import { HttpClient, HttpParams, HttpHeaders } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class ReportesService {
  base_url_reporte = Constantes.base_url + "/contribucionesreporte"
  base_url_reporte_mov = Constantes.base_url + "/contribucionespagoreporte"
  base_url_reporte_contribuyentes = Constantes.base_url + "/contribuyentesreporte"
  constructor(private http: HttpClient) { }

  getContribucionesPeriodoReporte(_fechaInicial: string, _fechaFinal: string,
    _formato: number, _todosTP: boolean, _codigoTP: string,
    _todosContribuyente: boolean, _codigoContribuyente: string, _estadoPago: number, contribuciones: boolean, recaudador:string) {
    console.log(_formato);


    let params = new HttpParams().set("fechaInicial", _fechaInicial).set("fechaFinal", _fechaFinal).set("formato", _formato.toString())
      .set("todosTP", _todosTP.toString())
      .set("codigoTP", _codigoTP)
      .set("todosContribuyente", _todosContribuyente.toString())
      .set("codigoContribuyente", _codigoContribuyente)
      .set("estadoPago", _estadoPago.toString())
      .set("recaudador", recaudador);


    let headers = new HttpHeaders();
    headers = headers.set('Accept', 'application/pdf');
    if (_formato == 2) {
      console.log("xls");
      headers = headers.set('Accept', 'application/vnd.ms-excel');
    }

    if (contribuciones) {
      return this.http.get(`${this.base_url_reporte}`, { headers: headers, params: params, responseType: 'arraybuffer' });
    }
    else {
      return this.http.get(`${this.base_url_reporte_mov}`, { headers: headers, params: params, responseType: 'arraybuffer' });
    }
  }

  getContribuyentesTPReporte(_formato: number, _todosTP: boolean, _codigoTP: string) {
    let params = new HttpParams().set("formato", _formato.toString())
      .set("todosTP", _todosTP.toString())
      .set("codigoTP", _codigoTP)

    let headers = new HttpHeaders();
    headers = headers.set('Accept', 'application/pdf');
    if (_formato == 2) {
      console.log("xls");
      headers = headers.set('Accept', 'application/vnd.ms-excel');
    }


    return this.http.get(`${this.base_url_reporte_contribuyentes}`, { headers: headers, params: params, responseType: 'arraybuffer' });
  }
}
