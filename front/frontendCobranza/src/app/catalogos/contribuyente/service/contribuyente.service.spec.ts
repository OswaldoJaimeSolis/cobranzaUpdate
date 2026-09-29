import { TestBed } from '@angular/core/testing';

import { ContribuyenteService } from './contribuyente.service';

describe('ContribuyenteService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: ContribuyenteService = TestBed.get(ContribuyenteService);
    expect(service).toBeTruthy();
  });
});
