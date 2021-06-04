import {Injectable} from "@angular/core";
import {BehaviorSubject} from "rxjs";

@Injectable({providedIn: 'root'})
export class ThemeService {
  private readonly isDarkSubject$ = new BehaviorSubject(false)
  readonly isDark$ = this.isDarkSubject$.asObservable()
  isDark: boolean;

  constructor() {
    this.isDark = JSON.parse(localStorage.getItem('isDarkTheme') || 'false');
  }


  setDarkTheme(isDark: boolean): void {
    this.isDark = isDark
    localStorage.setItem('isDarkTheme', JSON.stringify(isDark));
    this.isDarkSubject$.next(isDark);
  }

}
