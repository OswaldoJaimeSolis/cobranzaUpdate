import { TestBed } from '@angular/core/testing';

import { ReportesService } from './reportes.service';

describe('ReportesService', () => {
  beforeEach(() => TestBed.configureTestingModule({}));

  it('should be created', () => {
    const service: ReportesService = TestBed.inject(ReportesService);
    expect(service).toBeTruthy();
  });
});
