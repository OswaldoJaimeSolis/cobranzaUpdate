import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { BusquedaContribuyentesComponent } from './busqueda-contribuyentes.component';

describe('BusquedaContribuyentesComponent', () => {
  let component: BusquedaContribuyentesComponent;
  let fixture: ComponentFixture<BusquedaContribuyentesComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ BusquedaContribuyentesComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BusquedaContribuyentesComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
