import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AddPropietarioPlazaComponent } from './add-propietario-plaza.component';

describe('AddPropietarioPlazaComponent', () => {
  let component: AddPropietarioPlazaComponent;
  let fixture: ComponentFixture<AddPropietarioPlazaComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AddPropietarioPlazaComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddPropietarioPlazaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
