import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ReporteContribucionesComponent } from './reporte-contribuciones.component';

describe('ReporteContribucionesComponent', () => {
  let component: ReporteContribucionesComponent;
  let fixture: ComponentFixture<ReporteContribucionesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ReporteContribucionesComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ReporteContribucionesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
