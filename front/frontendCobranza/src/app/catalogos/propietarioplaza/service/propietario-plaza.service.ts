import { Injectable } from '@angular/core';
import { Constantes } from 'src/app/shared/constantes';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { PropietarioPlaza } from '../model/propietario-plaza';

@Injectable({
  providedIn: 'root'
})
export class PropietarioPlazaService {
  base_url = Constantes.base_url + "/propietarioPlaza";

  constructor(private http: HttpClient) { }

  getTodos(): Observable<PropietarioPlaza[]> {
    return this.http.get<PropietarioPlaza[]>(this.base_url+'JB');
  }

  getOne(id: string): Observable<PropietarioPlaza> {
    return this.http.get<PropietarioPlaza>(`${this.base_url}/${id}`);
  }



  addPropietarioPlaza(newO: PropietarioPlaza): Observable<PropietarioPlaza> {
    return this.http.post<PropietarioPlaza>(`${this.base_url}`, newO, {
      headers: {
        'Content-Type': 'application/json'
      }
    });
  }

  editPropietarioPlaza(newPP: PropietarioPlaza, idPP: string): Observable<PropietarioPlaza> {
    return this.http.put<PropietarioPlaza>(`${this.base_url}/${idPP}`, newPP, {
      headers: {
        'Content-Type': 'application/json'
      }
    });
  }

}