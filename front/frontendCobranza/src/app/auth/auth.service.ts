import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, tap } from 'rxjs';
import { Constantes } from 'src/app/shared/constantes';

export interface Sesion {
  token: string;
  /** epoch en milisegundos */
  expira: number;
  usuario: string;
  nombre: string;
  rol: string;
}

const CLAVE_SESION = 'sesion';

/** Sesión del usuario web: el JWT vive en sessionStorage (se pierde al cerrar la pestaña). */
@Injectable({
  providedIn: 'root'
})
export class AuthService {
  static readonly URL_LOGIN = Constantes.base_url + '/auth/login';

  constructor(private http: HttpClient, private router: Router) { }

  login(usuario: string, password: string): Observable<Sesion> {
    return this.http.post<Sesion>(AuthService.URL_LOGIN, { usuario: usuario, password: password })
      .pipe(tap(sesion => this.guardar(sesion)));
  }

  logout() {
    try { sessionStorage.removeItem(CLAVE_SESION); } catch (e) { }
    this.router.navigate(['/login']);
  }

  cambiarPassword(actual: string, nueva: string): Observable<any> {
    return this.http.put(Constantes.base_url + '/auth/password', { actual: actual, nueva: nueva });
  }

  /** Sesión vigente o null si no hay o ya expiró. */
  get sesion(): Sesion | null {
    let sesion: Sesion | null = null;
    try {
      const texto = sessionStorage.getItem(CLAVE_SESION);
      sesion = texto ? JSON.parse(texto) : null;
    } catch (e) { }
    return sesion && sesion.expira > Date.now() ? sesion : null;
  }

  estaAutenticado(): boolean {
    return this.sesion != null;
  }

  tieneRol(roles: string[]): boolean {
    const sesion = this.sesion;
    return sesion != null && roles.includes(sesion.rol);
  }

  esAdmin(): boolean {
    return this.tieneRol(['ADMIN']);
  }

  private guardar(sesion: Sesion) {
    try { sessionStorage.setItem(CLAVE_SESION, JSON.stringify(sesion)); } catch (e) { }
  }
}
