import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { AddTiposplazaComponent } from './add-tiposplaza.component';

describe('AddTiposplazaComponent', () => {
  let component: AddTiposplazaComponent;
  let fixture: ComponentFixture<AddTiposplazaComponent>;

  beforeEach(waitForAsync(() => {
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
