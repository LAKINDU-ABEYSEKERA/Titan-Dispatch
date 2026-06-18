import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { Navbar } from '../navbar/navbar';
import { Sidebar } from '../sidebar/sidebar';

@Component({
  selector: 'app-layout',
  standalone: true,
  // FIX: Explicitly importing the child components and the routing directive
  imports: [RouterOutlet, Navbar, Sidebar],
  templateUrl: './layout.html'
})
export class Layout {}