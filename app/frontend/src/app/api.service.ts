import { Injectable } from '@angular/core';
import { HttpClient, HttpErrorResponse } from '@angular/common/http';
import { Observable, throwError, timer } from 'rxjs';
import { catchError, retry, timeout } from 'rxjs/operators';
import {
  AccessoResponse,
  AnalisiResponse,
  ApiError,
  CaricamentoDatiResponse,
  DatiAttuali,
  ProfiloResponse,
  ProfiloSalvaRequest,
  ProfiloSalvaResponse,
} from './api.models';

const TIMEOUT_MS = 10000;

/** Unico servizio HTTP dell'app. Applica timeout a tutte le chiamate, 1 retry solo sulle GET,
 * e trasforma ogni errore in un ApiError con codice (mai il messaggio tecnico del server). */
@Injectable({ providedIn: 'root' })
export class ApiService {
  constructor(private readonly http: HttpClient) {}

  accesso(nomeUtente: string, password: string): Observable<AccessoResponse> {
    return this.http
      .post<AccessoResponse>('/api/accesso', { nomeUtente, password })
      .pipe(timeout(TIMEOUT_MS), catchError(this.mapError));
  }

  getDatiAttuali(): Observable<DatiAttuali> {
    return this.http
      .get<DatiAttuali>('/api/dati')
      .pipe(timeout(TIMEOUT_MS), this.retryGet(), catchError(this.mapError));
  }

  caricaDati(file: File): Observable<CaricamentoDatiResponse> {
    const formData = new FormData();
    formData.append('file', file);
    return this.http
      .post<CaricamentoDatiResponse>('/api/dati', formData)
      .pipe(timeout(TIMEOUT_MS), catchError(this.mapError));
  }

  getProfilo(nomeUtente: string): Observable<ProfiloResponse> {
    return this.http
      .get<ProfiloResponse>(`/api/profilo?nomeUtente=${encodeURIComponent(nomeUtente)}`)
      .pipe(timeout(TIMEOUT_MS), this.retryGet(), catchError(this.mapError));
  }

  salvaProfilo(nomeUtente: string, corpo: ProfiloSalvaRequest): Observable<ProfiloSalvaResponse> {
    return this.http
      .put<ProfiloSalvaResponse>(`/api/profilo?nomeUtente=${encodeURIComponent(nomeUtente)}`, corpo)
      .pipe(timeout(TIMEOUT_MS), catchError(this.mapError));
  }

  getAnalisi(nomeUtente: string): Observable<AnalisiResponse> {
    return this.http
      .get<AnalisiResponse>(`/api/analisi?nomeUtente=${encodeURIComponent(nomeUtente)}`)
      .pipe(timeout(TIMEOUT_MS), this.retryGet(), catchError(this.mapError));
  }

  /** 1 retry, solo se l'errore è di rete (stato 0), timeout, o stato ≥ 500. */
  private retryGet<T>() {
    return retry<T>({
      count: 1,
      delay: (error: unknown) => {
        if (this.eRetryable(error)) {
          return timer(0);
        }
        throw error;
      },
    });
  }

  private eRetryable(error: unknown): boolean {
    if (error instanceof HttpErrorResponse) {
      return error.status === 0 || error.status >= 500;
    }
    return (error as { name?: string })?.name === 'TimeoutError';
  }

  private mapError = (error: unknown): Observable<never> => {
    const apiError: ApiError = this.toApiError(error);
    return throwError(() => apiError);
  };

  private toApiError(error: unknown): ApiError {
    if (error instanceof HttpErrorResponse) {
      if (error.status === 0) {
        return { codice: 'RETE_NON_DISPONIBILE', messaggio: '', dettagli: [] };
      }
      const corpo = error.error;
      if (corpo && typeof corpo === 'object' && typeof corpo.codice === 'string') {
        return {
          codice: corpo.codice,
          messaggio: corpo.messaggio ?? '',
          dettagli: Array.isArray(corpo.dettagli) ? corpo.dettagli : [],
        };
      }
      return { codice: 'ERRORE_INTERNO', messaggio: '', dettagli: [] };
    }
    if ((error as { name?: string })?.name === 'TimeoutError') {
      return { codice: 'RETE_NON_DISPONIBILE', messaggio: '', dettagli: [] };
    }
    return { codice: 'ERRORE_INTERNO', messaggio: '', dettagli: [] };
  }
}
