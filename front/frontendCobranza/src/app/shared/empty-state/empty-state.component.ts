import { Component, Input, ChangeDetectionStrategy } from '@angular/core';

/** Estado vacío: icono y texto (y acciones opcionales proyectadas). */
@Component({
    selector: 'app-empty-state',
    templateUrl: './empty-state.component.html',
    styleUrls: ['./empty-state.component.css'],
    changeDetection: ChangeDetectionStrategy.Eager,
    standalone: false
})
export class EmptyStateComponent {
  @Input() icono = 'inbox';
  @Input() texto = '';
}
