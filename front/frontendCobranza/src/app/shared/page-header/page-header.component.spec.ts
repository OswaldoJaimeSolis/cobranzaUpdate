import { ComponentFixture, TestBed } from '@angular/core/testing';
import { PageHeaderComponent } from './page-header.component';

describe('PageHeaderComponent', () => {
  let fixture: ComponentFixture<PageHeaderComponent>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      declarations: [PageHeaderComponent]
    }).compileComponents();
    fixture = TestBed.createComponent(PageHeaderComponent);
  });

  it('muestra el título y el subtítulo', () => {
    fixture.componentInstance.titulo = 'Contribuyentes';
    fixture.componentInstance.subtitulo = 'Registrados';
    fixture.detectChanges();
    const el: HTMLElement = fixture.nativeElement;
    expect(el.querySelector('h1').textContent).toContain('Contribuyentes');
    expect(el.querySelector('p').textContent).toContain('Registrados');
  });
});
