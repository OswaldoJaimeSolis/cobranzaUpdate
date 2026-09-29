import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { DialgAnswerSiNoComponent } from './dialg-answer-si-no.component';

describe('DialgAnswerSiNoComponent', () => {
  let component: DialgAnswerSiNoComponent;
  let fixture: ComponentFixture<DialgAnswerSiNoComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ DialgAnswerSiNoComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DialgAnswerSiNoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
