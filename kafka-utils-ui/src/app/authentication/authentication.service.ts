import {Injectable} from "@angular/core";

const TOKEN_LOCAL_STORAGE_KEY = 'Authorization';

@Injectable({providedIn: 'root'})
export class AuthenticationService {
  getToken(): string | undefined {
    return window.localStorage.getItem(TOKEN_LOCAL_STORAGE_KEY) || undefined;
  }
}
