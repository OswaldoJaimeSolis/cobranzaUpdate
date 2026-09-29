import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ListTiposplazaComponent } from './list-tiposplaza.component';

describe('ListTiposplazaComponent', () => {
  let component: ListTiposplazaComponent;
  let fixture: ComponentFixture<ListTiposplazaComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ListTiposplazaComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ListTiposplazaComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
