import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { ListTiposplazaComponent } from './list-tiposplaza.component';

describe('ListTiposplazaComponent', () => {
  let component: ListTiposplazaComponent;
  let fixture: ComponentFixture<ListTiposplazaComponent>;

  beforeEach(waitForAsync(() => {
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
