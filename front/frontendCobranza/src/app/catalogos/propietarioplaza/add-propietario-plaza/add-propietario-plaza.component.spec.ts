import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { AddPropietarioPlazaComponent } from './add-propietario-plaza.component';

describe('AddPropietarioPlazaComponent', () => {
  let component: AddPropietarioPlazaComponent;
  let fixture: ComponentFixture<AddPropietarioPlazaComponent>;

  beforeEach(waitForAsync(() => {
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
