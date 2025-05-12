import { Component } from '@angular/core';
import { ClientListComponent } from './clients/client-list/client-list.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [ClientListComponent],          // импортируем stand‑alone‑дочерний компонент
  template: `<app-client-list></app-client-list>`
})
export class AppComponent {}
