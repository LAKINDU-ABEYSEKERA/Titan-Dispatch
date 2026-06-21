import { TestBed } from '@angular/core/testing';

import { DispatchForm } from './dispatch-form';

describe('DispatchForm', () => {
  let service: DispatchForm;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(DispatchForm);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
