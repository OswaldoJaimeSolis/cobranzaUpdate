import { TestBed } from '@angular/core/testing';

import { PropietarioPlazaService } from './propietario-plaza.service';

describe('PropietarioPlazaService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: PropietarioPlazaService = TestBed.get(PropietarioPlazaService);
    expect(service).toBeTruthy();
  });
});
