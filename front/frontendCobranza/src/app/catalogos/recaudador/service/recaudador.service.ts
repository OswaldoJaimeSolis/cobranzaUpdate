import { Injectable } from '@angular/core';
import { Constantes } from 'src/app/shared/constantes';
import { HttpClient } from '@angular/common/http';
import { Recaudador } from '../model/recaudador';
import { Observable } from 'rxjs';
import { RecuadadorTipoPlaza } from '../model/recuadador-tipo-plaza';

@Injectable({
  providedIn: 'root'
})
export class RecaudadorService {
  base_url = Constantes.base_url + "/recaudadores/";
  base_url_tp = Constantes.base_url + "/recaudadoresTP";
  constructor(private http: HttpClient) { }

  getTodos(): Observable<Recaudador[]> {
    return this.http.get<Recaudador[]>(this.base_url);
  }

  getRecaudador(id: string): Observable<Recaudador> {
    return this.http.get<Recaudador>(`${this.base_url}, ${id}`);
  }

  addRecaudador(newCon: Recaudador): Observable<Recaudador> {
    return this.http.post<Recaudador>(`${this.base_url}`, newCon, {
      headers: {
        'Content-Type': 'application/json'
      }
    });
  }
  updateRecaudador(editedRecaudador: Recaudador): Observable<Recaudador> {
    return this.http.put<Recaudador>(`${this.base_url}/${editedRecaudador.codigoRecaudador}`,
      editedRecaudador, {
        headers: {
          'Content-Type': 'application/json'
        }
      });
  }

  deleteRecaudador(id: string): Observable<Recaudador> {
    return this.http.delete<Recaudador>(`${this.base_url}/${id}`);
  }

  getRecaudadorTP(codigoRecaudador: string): Observable<RecuadadorTipoPlaza[]> {
    return this.http.get<RecuadadorTipoPlaza[]>(`${this.base_url_tp}/${codigoRecaudador}`);
  }
  addRecaudadorTP(newO: RecuadadorTipoPlaza): Observable<RecuadadorTipoPlaza> {
    return this.http.post<RecuadadorTipoPlaza>(`${this.base_url_tp}`, newO, {
      headers: {
        'Content-Type': 'application/json'
      }
    });
  }


  deleteRecaudadorTP(id: number): Observable<RecuadadorTipoPlaza> {
    return this.http.delete<RecuadadorTipoPlaza>(`${this.base_url_tp}/${id}`);
  }

}
