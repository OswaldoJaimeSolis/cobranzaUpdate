import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { ListPropietarioPlazaComponent } from './list-propietario-plaza.component';

describe('ListPropietarioPlazaComponent', () => {
  let component: ListPropietarioPlazaComponent;
  let fixture: ComponentFixture<ListPropietarioPlazaComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ ListPropietarioPlazaComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ListPropietarioPlazaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
