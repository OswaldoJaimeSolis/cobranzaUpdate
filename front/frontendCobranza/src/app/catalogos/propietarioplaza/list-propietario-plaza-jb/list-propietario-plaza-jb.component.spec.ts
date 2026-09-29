import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ListPropietarioPlazaJbComponent } from './list-propietario-plaza-jb.component';

describe('ListPropietarioPlazaJbComponent', () => {
  let component: ListPropietarioPlazaJbComponent;
  let fixture: ComponentFixture<ListPropietarioPlazaJbComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ListPropietarioPlazaJbComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ListPropietarioPlazaJbComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
