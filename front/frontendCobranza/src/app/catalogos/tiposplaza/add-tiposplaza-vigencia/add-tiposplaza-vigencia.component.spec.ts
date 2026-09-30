import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { AddTiposplazaVigenciaComponent } from './add-tiposplaza-vigencia.component';

describe('AddTiposplazaVigenciaComponent', () => {
  let component: AddTiposplazaVigenciaComponent;
  let fixture: ComponentFixture<AddTiposplazaVigenciaComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ AddTiposplazaVigenciaComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddTiposplazaVigenciaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
