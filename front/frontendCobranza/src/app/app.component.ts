import { Component, ChangeDetectionStrategy, OnDestroy, ViewChild } from '@angular/core';
import { MatSidenav } from '@angular/material/sidenav';
import { BreakpointObserver } from '@angular/cdk/layout';
import { Subscription } from 'rxjs';
import { AuthService } from './auth/auth.service';

@Component({
    selector: 'app-root',
    templateUrl: './app.component.html',
    styleUrls: ['./app.component.css'],
    changeDetection: ChangeDetectionStrategy.Eager,
    standalone: false
})
export class AppComponent implements OnDestroy {
  title = 'cobranzaCli';
  /** Menos de 600px: el menú pasa a un panel lateral con botón hamburguesa */
  esMovil = false;
  private sub: Subscription;
  @ViewChild('nav') nav: MatSidenav;

  constructor(public auth: AuthService, breakpoints: BreakpointObserver) {
    this.sub = breakpoints.observe('(max-width: 599.98px)')
      .subscribe(estado => {
        this.esMovil = estado.matches;
        if (!this.esMovil) {
          this.nav?.close();
        }
      });
  }

  ngOnDestroy() {
    this.sub.unsubscribe();
  }
}
