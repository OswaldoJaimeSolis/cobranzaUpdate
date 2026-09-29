import { Injectable } from '@angular/core';
import { Constantes } from 'src/app/shared/constantes';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TipoPlaza } from '../model/tipo-plaza';
import { TipoPlazaVigencia } from '../model/tipo-plaza-vigencia';

@Injectable({
  providedIn: 'root'
})
export class TiposplazaService {

  base_url = Constantes.base_url + "/tiposPlaza";
  base_url_historial = Constantes.base_url + "/tiposPlazaVigencia";
  //base_url_historialtt = Constantes.base_url + "/tiposPlazaVigencia/";

  constructor(private http: HttpClient) { }

  getTodos(): Observable<TipoPlaza[]> {
    return this.http.get<TipoPlaza[]>(this.base_url);
  }

  getTipoPlazaVigencias(codigoTipoPlaza:string): Observable<TipoPlazaVigencia[]>{
    //let params= new HttpParams().set('codigoTipoPlaza', codigoTipoPlaza);
    //return this.http.get<TipoPlazaVigencia[]>(`${this.base_url_historial}`, {params:params});
    return this.http.get<TipoPlazaVigencia[]>(`${this.base_url_historial}/${codigoTipoPlaza}`);
  }

  getTipoPlaza(id: string): Observable<TipoPlaza> {
    return this.http.get<TipoPlaza>(`${this.base_url}, ${id}`);
  }

  addTipoPlazaVigencia(newO: TipoPlazaVigencia): Observable<TipoPlazaVigencia> {
    return this.http.post<TipoPlazaVigencia>(`${this.base_url_historial}`, newO, {
      headers: {
        'Content-Type': 'application/json'
      }
    });
  }

  addTipoPlaza(newCon: TipoPlaza): Observable<TipoPlaza> {
    return this.http.post<TipoPlaza>(`${this.base_url}`, newCon, {
      headers: {
        'Content-Type': 'application/json'
      }
    });
  }
  updateTipoPlaza(editedTipoPlaza: TipoPlaza): Observable<TipoPlaza> {
    return this.http.put<TipoPlaza>(`${this.base_url}/${editedTipoPlaza.codigoTipoPlaza}`,
      editedTipoPlaza, {
        headers: {
          'Content-Type': 'application/json'
        }
      });
  }

  deleteTipoPlaza(id: string): Observable<TipoPlaza> {
    return this.http.delete<TipoPlaza>(`${this.base_url}/${id}`);
  }

  deleteTipoPlazaVigencia(id: number): Observable<TipoPlazaVigencia> {
    return this.http.delete<TipoPlazaVigencia>(`${this.base_url_historial}/${id}`);
  }
  
}