import { Component, OnInit, Inject, ChangeDetectionStrategy } from '@angular/core';
import { MatDialogRef, MAT_DIALOG_DATA } from '@angular/material/dialog';

export type TipoMensaje = 'info' | 'aviso' | 'error' | 'exito';

export interface DialogData {
  message: string;
  title: string;
  /** Define el icono y su color; si no se indica se deduce del título */
  tipo?: TipoMensaje;
}

const ICONOS: Record<TipoMensaje, string> = {
  info: 'info',
  aviso: 'warning',
  error: 'error',
  exito: 'check_circle'
};

@Component({
    selector: 'app-dialog-informativo',
    templateUrl: './dialog-informativo.component.html',
    styleUrls: ['./dialog-informativo.component.css'],
    changeDetection: ChangeDetectionStrategy.Eager,
    standalone: false
})
export class DialogInformativoComponent implements OnInit {
  message: string;
  title: string;
  tipo: TipoMensaje;

  constructor(private dialogRef: MatDialogRef<DialogInformativoComponent>,
    @Inject(MAT_DIALOG_DATA) dd: DialogData) {
    this.message = dd.message;
    // Títulos heredados como "Atención!" se muestran en formato de oración
    this.title = (dd.title || '').replace(/^Atención!?$/, 'Atención');
    this.tipo = dd.tipo || this.deducirTipo(dd.title);
  }

  get icono(): string {
    return ICONOS[this.tipo];
  }

  private deducirTipo(titulo: string): TipoMensaje {
    const t = (titulo || '').toLowerCase();
    if (t.startsWith('atención') || t.startsWith('cuidado') || t.includes('incorrect')) {
      return 'aviso';
    }
    if (t.startsWith('error')) {
      return 'error';
    }
    if (t.startsWith('listo')) {
      return 'exito';
    }
    return 'info';
  }

  ngOnInit() {
  }

  close() {
    this.dialogRef.close();
  }

}
