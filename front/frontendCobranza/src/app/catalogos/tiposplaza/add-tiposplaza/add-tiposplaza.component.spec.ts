import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AddTiposplazaComponent } from './add-tiposplaza.component';

describe('AddTiposplazaComponent', () => {
  let component: AddTiposplazaComponent;
  let fixture: ComponentFixture<AddTiposplazaComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AddTiposplazaComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddTiposplazaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
