import { NgModule } from '@angular/core';
import { Route, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { HomeComponent } from './home/home.component';

import { DeasAdminModule } from './deas/admin/deas-admin.module';
import { DeasAdminComponent } from './deas/admin/deas-admin.component';

import { DeasEventsModule } from './deas/events/deas-events.module';
import { DeasEventsComponent } from './deas/events/deas-events.component';

@NgModule({
    imports: [
        DeasAdminModule,
        DeasEventsModule,
        CommonModule,
        RouterModule.forChild(
            [
                { path: 'home', component: HomeComponent },
                { path: 'deas/admin', component: DeasAdminComponent },
                { path: 'deas/events', component: DeasEventsComponent },
                { path: '', redirectTo: 'home', pathMatch: 'full' }
            ]
        )
    ],
    declarations: [
        HomeComponent,
    ]
})

export class DashboardModule{}
