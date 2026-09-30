import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { DialogInformativoComponent } from './dialog-informativo.component';

describe('DialogInformativoComponent', () => {
  let component: DialogInformativoComponent;
  let fixture: ComponentFixture<DialogInformativoComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ DialogInformativoComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(DialogInformativoComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
