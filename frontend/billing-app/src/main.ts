import { bootstrapApplication, provideProtractorTestingSupport } from '@angular/platform-browser';
import { provideHttpClient, withFetch } from '@angular/common/http';
import { provideRouter, Route } from '@angular/router';

import { AppComponent } from './app/app.component';
import { ClientListComponent } from './app/clients/client-list/client-list.component';
import { TrunkListComponent } from './app/trunks/trunk-list/trunk-list.component';
/* маршруты */
const routes: Route[] = [
  { path: '',   redirectTo: 'clients', pathMatch: 'full' },
  { path: 'clients', component: ClientListComponent },
  { path: 'trunks',  component: TrunkListComponent },
  { path: '**', redirectTo: 'clients' }
];

bootstrapApplication(AppComponent, {
  providers: [
    provideHttpClient(withFetch()),
    provideRouter(routes),
    provideProtractorTestingSupport()    // игнорируйте, если не нужен e2e
  ]
}).catch(console.error);
