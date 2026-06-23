import { Component, signal } from '@angular/core';

@Component({
  selector: 'app-complete-dispatch-drawer',
  standalone: true,
  templateUrl: 'complete-dispatch-drawer.html'
})
export class CompleteDispatchDrawerComponent {
  // 1. The Single Source of Truth
  isOpen = signal(false);

  // 2. State Mutators
  open() { 
    this.isOpen.set(true); 
  }
  
  close() { 
    this.isOpen.set(false); 
  }
}