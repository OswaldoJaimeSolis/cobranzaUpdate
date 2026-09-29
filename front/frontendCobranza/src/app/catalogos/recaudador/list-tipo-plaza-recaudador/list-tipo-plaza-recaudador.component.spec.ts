import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ListTipoPlazaRecaudadorComponent } from './list-tipo-plaza-recaudador.component';

describe('ListTipoPlazaRecaudadorComponent', () => {
  let component: ListTipoPlazaRecaudadorComponent;
  let fixture: ComponentFixture<ListTipoPlazaRecaudadorComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ListTipoPlazaRecaudadorComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ListTipoPlazaRecaudadorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
