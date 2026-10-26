import { ComponentFixture, TestBed } from '@angular/core/testing';
import { MatIconModule } from '@angular/material/icon';
import { EmptyStateComponent } from './empty-state.component';

describe('EmptyStateComponent', () => {
  let fixture: ComponentFixture<EmptyStateComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [MatIconModule],
      declarations: [EmptyStateComponent]
    }).compileComponents();
    fixture = TestBed.createComponent(EmptyStateComponent);
  });

  it('muestra el texto', () => {
    fixture.componentInstance.texto = 'No hay contribuyentes';
    fixture.detectChanges();
    expect(fixture.nativeElement.textContent).toContain('No hay contribuyentes');
  });
});
