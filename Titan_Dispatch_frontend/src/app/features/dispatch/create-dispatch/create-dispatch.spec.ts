import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CreateDispatch } from './create-dispatch';

describe('CreateDispatch', () => {
  let component: CreateDispatch;
  let fixture: ComponentFixture<CreateDispatch>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CreateDispatch],
    }).compileComponents();

    fixture = TestBed.createComponent(CreateDispatch);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
