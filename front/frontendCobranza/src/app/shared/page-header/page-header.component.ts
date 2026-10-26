import { Component, Input, ChangeDetectionStrategy } from '@angular/core';

/** Encabezado de página: título, subtítulo opcional y acciones (contenido proyectado). */
@Component({
    selector: 'app-page-header',
    templateUrl: './page-header.component.html',
    styleUrls: ['./page-header.component.css'],
    changeDetection: ChangeDetectionStrategy.Eager,
    standalone: false
})
export class PageHeaderComponent {
  @Input() titulo = '';
  @Input() subtitulo = '';
}
