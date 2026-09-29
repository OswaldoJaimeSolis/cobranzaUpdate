import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { BusquedaRecaudadoresComponent } from './busqueda-recaudadores.component';

describe('BusquedaRecaudadoresComponent', () => {
  let component: BusquedaRecaudadoresComponent;
  let fixture: ComponentFixture<BusquedaRecaudadoresComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ BusquedaRecaudadoresComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(BusquedaRecaudadoresComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
