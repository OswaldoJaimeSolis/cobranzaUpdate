import { TestBed } from '@angular/core/testing';

import { TiposplazaService } from './tiposplaza.service';

describe('TiposplazaService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: TiposplazaService = TestBed.get(TiposplazaService);
    expect(service).toBeTruthy();
  });
});
