import { NgModule }      from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule } from '@angular/router';
import { HttpModule } from '@angular/http';
import { FormsModule }   from '@angular/forms';

import { AppComponent }   from './app.component';
import { DashboardComponent } from './main/dashboard.component';

import { DashboardModule } from './main/dashboard.module';
import { SidebarModule } from './sidebar/sidebar.module';
import { FooterModule } from './shared/footer/footer.module';
import { NavbarModule} from './shared/navbar/navbar.module';

import { PathLocationStrategy, LocationStrategy } from '@angular/common';

@NgModule({
    imports: [
        FormsModule,
        BrowserModule,
        DashboardModule,
        SidebarModule,
        NavbarModule,
        FooterModule,
        RouterModule.forRoot([])
    ],
    declarations: [ AppComponent, DashboardComponent ],
    providers: [{provide: LocationStrategy, useClass: PathLocationStrategy}],
    bootstrap:    [ AppComponent ]
})
export class AppModule { }
