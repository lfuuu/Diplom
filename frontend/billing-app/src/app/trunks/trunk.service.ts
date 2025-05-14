import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Trunk } from './trunk';

@Injectable({ providedIn: 'root' })
export class TrunkService {
  /** запрос Swagger → GET /v1/api/auth/trunks */
  private readonly base = '/v1/api/auth-trunks';

  constructor(private http: HttpClient) {}

  getTrunks(): Observable<Trunk[]> {
    return this.http.get<Trunk[]>(this.base);
  }
}
