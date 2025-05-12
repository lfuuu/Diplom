import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Client } from './clients';

@Injectable({ providedIn: 'root' })
export class ClientService {
  private readonly base = '/v1/api/billing-clients';

  constructor(private http: HttpClient) {}

  /** список */
  getClients(): Observable<Client[]> {
    return this.http.get<Client[]>(this.base);
  }

  /** создание (POST → 201) */
  createClient(body: Omit<Client, 'id' | 'dtCreate'>): Observable<Client> {
    return this.http.post<Client>(this.base, body);
  }

  /** удаление (DELETE → 204) */
  deleteClient(id: number): Observable<void> {
    return this.http.delete<void>(`${this.base}/${id}`);
  }
}
