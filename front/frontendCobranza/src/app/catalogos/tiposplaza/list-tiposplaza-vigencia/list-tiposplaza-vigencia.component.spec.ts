import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ListTiposplazaVigenciaComponent } from './list-tiposplaza-vigencia.component';

describe('ListTiposplazaVigenciaComponent', () => {
  let component: ListTiposplazaVigenciaComponent;
  let fixture: ComponentFixture<ListTiposplazaVigenciaComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ListTiposplazaVigenciaComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ListTiposplazaVigenciaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
