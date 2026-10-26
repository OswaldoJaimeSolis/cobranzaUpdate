import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Constantes } from 'src/app/shared/constantes';
import { Usuario } from '../model/usuario';

@Injectable({
  providedIn: 'root'
})
export class UsuarioService {
  base_url = Constantes.base_url + "/usuarios";
  constructor(private http: HttpClient) { }

  getTodos(): Observable<Usuario[]> {
    return this.http.get<Usuario[]>(this.base_url);
  }

  addUsuario(usuario: Usuario): Observable<Usuario> {
    return this.http.post<Usuario>(this.base_url, usuario);
  }

  updateUsuario(usuario: Usuario): Observable<Usuario> {
    return this.http.put<Usuario>(`${this.base_url}/${usuario.usuario}`, usuario);
  }

  deleteUsuario(usuario: string): Observable<any> {
    return this.http.delete(`${this.base_url}/${usuario}`);
  }
}
