import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { ReporteContribucionesComponent } from './reporte-contribuciones.component';

describe('ReporteContribucionesComponent', () => {
  let component: ReporteContribucionesComponent;
  let fixture: ComponentFixture<ReporteContribucionesComponent>;

  beforeEach(waitForAsync(() => {
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
