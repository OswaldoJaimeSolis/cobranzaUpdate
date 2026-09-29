import { async, ComponentFixture, TestBed } from '@angular/core/testing';

import { AddContribuyenteComponent } from './add-contribuyente.component';

describe('AddContribuyenteComponent', () => {
  let component: AddContribuyenteComponent;
  let fixture: ComponentFixture<AddContribuyenteComponent>;

  beforeEach(async(() => {
    TestBed.configureTestingModule({
      declarations: [ AddContribuyenteComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddContribuyenteComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
