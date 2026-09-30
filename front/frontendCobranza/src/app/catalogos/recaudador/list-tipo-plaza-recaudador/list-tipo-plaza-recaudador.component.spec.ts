import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { ListTipoPlazaRecaudadorComponent } from './list-tipo-plaza-recaudador.component';

describe('ListTipoPlazaRecaudadorComponent', () => {
  let component: ListTipoPlazaRecaudadorComponent;
  let fixture: ComponentFixture<ListTipoPlazaRecaudadorComponent>;

  beforeEach(waitForAsync(() => {
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
