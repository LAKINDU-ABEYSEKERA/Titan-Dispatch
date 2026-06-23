import { ComponentFixture, TestBed } from '@angular/core/testing';

import { CompleteDispatchDrawer } from './complete-dispatch-drawer';

describe('CompleteDispatchDrawer', () => {
  let component: CompleteDispatchDrawer;
  let fixture: ComponentFixture<CompleteDispatchDrawer>;

  beforeEach(async () => {
    await TestBed.configureTestingModule({
      imports: [CompleteDispatchDrawer],
    }).compileComponents();

    fixture = TestBed.createComponent(CompleteDispatchDrawer);
    component = fixture.componentInstance;
    await fixture.whenStable();
  });

  it('should create', () => {
    expect(component).toBeTruthy();
  });
});
