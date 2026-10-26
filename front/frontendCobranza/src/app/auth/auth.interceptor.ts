import { Injectable } from '@angular/core';
import { HttpErrorResponse, HttpEvent, HttpHandler, HttpInterceptor, HttpRequest } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';
import { AuthService } from './auth.service';

/** Agrega el token a cada petición; un 401 (token vencido o inválido) cierra la sesión. */
@Injectable()
export class AuthInterceptor implements HttpInterceptor {

  constructor(private auth: AuthService) { }

  intercept(req: HttpRequest<any>, next: HttpHandler): Observable<HttpEvent<any>> {
    const esLogin = req.url == AuthService.URL_LOGIN;
    const sesion = this.auth.sesion;
    if (sesion && !esLogin) {
      req = req.clone({ setHeaders: { Authorization: 'Bearer ' + sesion.token } });
    }
    return next.handle(req).pipe(catchError((error: HttpErrorResponse) => {
      if (error.status == 401 && !esLogin) {
        this.auth.logout();
      }
      return throwError(() => error);
    }));
  }
}
