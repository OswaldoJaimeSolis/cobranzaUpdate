import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { BusquedaTiposPlazaComponent } from './busqueda-tipos-plaza.component';

describe('BusquedaTiposPlazaComponent', () => {
  let component: BusquedaTiposPlazaComponent;
  let fixture: ComponentFixture<BusquedaTiposPlazaComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ BusquedaTiposPlazaComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BusquedaTiposPlazaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
