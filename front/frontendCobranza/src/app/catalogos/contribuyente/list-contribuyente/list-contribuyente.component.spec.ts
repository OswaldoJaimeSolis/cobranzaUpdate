import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { ListContribuyenteComponent } from './list-contribuyente.component';

describe('ListContribuyenteComponent', () => {
  let component: ListContribuyenteComponent;
  let fixture: ComponentFixture<ListContribuyenteComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ ListContribuyenteComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ListContribuyenteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
