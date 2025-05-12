// src/main.server.ts
import 'zone.js/node';

import { bootstrapApplication } from '@angular/platform-browser';
import { provideHttpClient } from '@angular/common/http';
import { AppComponent } from './app/app.component';

/**
 * Vite‑SSR вызывает экспорт по‑умолчанию.
 * Возвращаем промис от bootstrapApplication —
 * это и есть Promise<ApplicationRef>, которого ждёт движок.
 */
export default () =>
  bootstrapApplication(AppComponent, {
    providers: [provideHttpClient()]   // + другие провайдеры при необходимости
  });
