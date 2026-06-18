import { TestBed } from '@angular/core/testing';

import { Dispatch } from './dispatch';

describe('Dispatch', () => {
  let service: Dispatch;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(Dispatch);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
