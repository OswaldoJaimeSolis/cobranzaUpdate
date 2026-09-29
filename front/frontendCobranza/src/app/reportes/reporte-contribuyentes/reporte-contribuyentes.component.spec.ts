import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ReporteContribuyentesComponent } from './reporte-contribuyentes.component';

describe('ReporteContribuyentesComponent', () => {
  let component: ReporteContribuyentesComponent;
  let fixture: ComponentFixture<ReporteContribuyentesComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ReporteContribuyentesComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ReporteContribuyentesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
