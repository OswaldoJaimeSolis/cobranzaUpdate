import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Contribuyente } from '../model/contribuyente';
import { Constantes } from '../../../shared/constantes';

@Injectable({
  providedIn: 'root'
})
export class ContribuyenteService {
  base_url = Constantes.base_url + "/contribuyentes";
  constructor(private http: HttpClient) { }

  getTodos(): Observable<Contribuyente[]> {
    return this.http.get<Contribuyente[]>(this.base_url);
  }

  getContribuyente(id: string): Observable<Contribuyente> {
    return this.http.get<Contribuyente>(`${this.base_url}, ${id}`);
  }

  addContribuyente(newCon: Contribuyente): Observable<Contribuyente> {
    return this.http.post<Contribuyente>(`${this.base_url}`, newCon, {
      headers: {
        'Content-Type': 'application/json'
      }
    });
  }
  updateContribuyente(editedContribuyente: Contribuyente): Observable<Contribuyente> {
    return this.http.put<Contribuyente>(`${this.base_url}/${editedContribuyente.codigoContribuyente}`,
      editedContribuyente, {
        headers: {
          'Content-Type': 'application/json'
        }
      });
  }

  deleteContribuyente(id: string): Observable<Contribuyente> {
    return this.http.delete<Contribuyente>(`${this.base_url}/${id}`);
  }

}
