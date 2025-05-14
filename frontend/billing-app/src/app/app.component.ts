import { Component } from '@angular/core';
import { RouterModule } from '@angular/router';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [RouterModule],
  template: `
    <nav class="menu">
      <a routerLink="/clients" routerLinkActive="active">Клиенты</a>
      <a routerLink="/trunks"  routerLinkActive="active">Транки</a>
    </nav>

    <router-outlet></router-outlet>
  `,
  styles: [`
    .menu {
      display: flex;
      gap: 1rem;
      padding: .75rem 1rem;
      background: #1976d2;
    }
    .menu a {
      color: #fff;
      text-decoration: none;
      font-weight: 500;
    }
    .menu a.active {
      text-decoration: underline;
    }
  `]
})
export class AppComponent {}
