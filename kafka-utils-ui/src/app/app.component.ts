import {Component, OnInit, ViewChild} from '@angular/core';
import {ThemeService} from "./commons/theme.service";
import {MatDrawer, MatSidenav} from "@angular/material/sidenav";

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.scss']
})
export class AppComponent implements OnInit{
  title = 'kafka-utils-ui';
  isDarkTheme = false
  @ViewChild(MatDrawer, { static: false }) matDrawer!: MatDrawer;

  constructor(
    private readonly themeService: ThemeService,
  ) {
    themeService.isDark$.subscribe( isDarkTheme => this.isDarkTheme = isDarkTheme);
  }

  openMenu() {
    this.matDrawer.toggle();
  }

  ngOnInit(): void {
    this.isDarkTheme = this.themeService.isDark;
  }
}
