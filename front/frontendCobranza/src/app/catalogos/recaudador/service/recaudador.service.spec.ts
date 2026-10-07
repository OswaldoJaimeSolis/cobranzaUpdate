import { TestBed } from '@angular/core/testing';

import { RecaudadorService } from './recaudador.service';

describe('RecaudadorService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: RecaudadorService = TestBed.inject(RecaudadorService);
    expect(service).toBeTruthy();
  });
});
