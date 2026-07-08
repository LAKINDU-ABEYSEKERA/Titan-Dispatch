import { Component } from '@angular/core';
import { CompleteDispatchDrawer } from '../../features/dispatch/components/complete-dispatch-drawer/complete-dispatch-drawer';

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CompleteDispatchDrawer], // 1. Import the drawer
  template: `
    <div class="space-y-6">
      <h2 class="text-xl font-black text-white uppercase tracking-wider">Titan Core Dispatch Console</h2>
      
      <div class="p-6 bg-slate-900/40 border border-slate-800 rounded-xl flex justify-between items-center">
        <div>
          <p class="text-white font-bold">Dispatch #DSP-1234</p>
          <p class="text-sm text-slate-400">DOZER-D8T - Site Alpha</p>
        </div>
        
        <button (click)="drawer.open('DSP-1234', 1450.0)"
                class="px-4 py-2 bg-blue-600 hover:bg-blue-700 text-white text-sm font-bold rounded transition-colors shadow-lg">
          Complete Dispatch
        </button>
      </div>

      <app-complete-dispatch-drawer 
        #drawer 
        (dispatchCompleted)="refreshData()">
      </app-complete-dispatch-drawer>
    </div>
  `
})
export class Dashboard {
  
  // 4. This fires when the drawer emits success
  refreshData() {
    console.log('[Dashboard] Dispatch completed successfully. Refreshing list...');
    // Future step: Call this.dispatchService.getAll() here to update the UI
  }
}