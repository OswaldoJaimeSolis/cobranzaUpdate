import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { AddContribuyenteComponent } from './add-contribuyente.component';

describe('AddContribuyenteComponent', () => {
  let component: AddContribuyenteComponent;
  let fixture: ComponentFixture<AddContribuyenteComponent>;

  beforeEach(waitForAsync(() => {
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
