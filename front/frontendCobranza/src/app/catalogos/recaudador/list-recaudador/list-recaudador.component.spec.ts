import { ComponentFixture, TestBed, waitForAsync } from '@angular/core/testing';

import { ListRecaudadorComponent } from './list-recaudador.component';

describe('ListRecaudadorComponent', () => {
  let component: ListRecaudadorComponent;
  let fixture: ComponentFixture<ListRecaudadorComponent>;

  beforeEach(waitForAsync(() => {
    TestBed.configureTestingModule({
      declarations: [ ListRecaudadorComponent ]
    })
    .compileComponents();
  }));

  beforeEach(() => {
    fixture = TestBed.createComponent(ListRecaudadorComponent);
    component = fixture.componentInstance;
    fixture.detectChanges();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
