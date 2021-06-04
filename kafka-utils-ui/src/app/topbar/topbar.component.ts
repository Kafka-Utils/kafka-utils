import {Component, OnInit, Output, EventEmitter} from '@angular/core';
import {ThemeService} from "../commons/theme.service";

@Component({
  selector: 'app-topbar',
  templateUrl: './topbar.component.html',
  styleUrls: ['./topbar.component.scss']
})
export class TopbarComponent implements OnInit {

  isDarkTheme = false

  @Output()
  menuClick = new EventEmitter<void>()

  constructor(
    private readonly themeService: ThemeService
  ) {
    themeService.isDark$.subscribe( isDarkTheme => this.isDarkTheme = isDarkTheme)
  }

  ngOnInit(): void {
    this.isDarkTheme = this.themeService.isDark
  }

  changeTheme() {
    this.themeService.setDarkTheme(!this.isDarkTheme)
  }
}
