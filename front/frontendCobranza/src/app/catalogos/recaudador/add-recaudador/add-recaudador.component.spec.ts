import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { AddRecaudadorComponent } from './add-recaudador.component';

describe('AddRecaudadorComponent', () => {
  let component: AddRecaudadorComponent;
  let fixture: ComponentFixture<AddRecaudadorComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ AddRecaudadorComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(AddRecaudadorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
