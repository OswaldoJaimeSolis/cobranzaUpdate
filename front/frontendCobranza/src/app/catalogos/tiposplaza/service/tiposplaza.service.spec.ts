import { TestBed } from '@angular/core/testing';

import { TiposplazaService } from './tiposplaza.service';

describe('TiposplazaService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: TiposplazaService = TestBed.inject(TiposplazaService);
    expect(service).toBeTruthy();
  });
});
