import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';
import { Router } from '@angular/router';
import { AuthService } from '../auth.service';

@Component({
    selector: 'app-login',
    templateUrl: './login.component.html',
    styleUrls: ['./login.component.css'],
    changeDetection: ChangeDetectionStrategy.Eager,
    standalone: false
})
export class LoginComponent implements OnInit {
  usuario = '';
  password = '';
  error = '';
  isLoading = false;

  constructor(private auth: AuthService, private router: Router) { }

  ngOnInit() {
    if (this.auth.estaAutenticado()) {
      this.router.navigate(['/']);
    }
  }

  entrar() {
    if (!this.usuario || !this.password) {
      this.error = 'Capture el usuario y la contraseña';
      return;
    }
    this.isLoading = true;
    this.error = '';
    this.auth.login(this.usuario, this.password).subscribe({
      next: () => {
        this.isLoading = false;
        this.router.navigate(['/']);
      },
      error: (error: any) => {
        this.isLoading = false;
        this.password = '';
        this.error = error.status == 0 ? 'No se pudo conectar con el servidor'
          : (error.error?.mensaje || 'No se pudo iniciar sesión');
      }
    });
  }
}
