import { Component, OnInit, ChangeDetectionStrategy } from '@angular/core';

@Component({
    selector: 'app-menu-principal',
    templateUrl: './menu-principal.component.html',
    styleUrls: ['./menu-principal.component.css'],
    changeDetection: ChangeDetectionStrategy.Eager,
    standalone: false
})
export class MenuPrincipalComponent implements OnInit {

  constructor() { }

  ngOnInit() {
  }

}
