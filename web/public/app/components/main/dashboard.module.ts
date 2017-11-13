import { NgModule } from '@angular/core';
import { Route, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';

import { DeasAdminModule } from './deas/admin/deas-admin.module';
import { DeasAdminComponent } from './deas/admin/deas-admin.component';

import { DeasEventsModule } from './deas/events/deas-events.module';
import { DeasEventsComponent } from './deas/events/deas-events.component';

import { MapsComponent } from '../shared/maps/maps.component';

@NgModule({
    imports: [
        DeasAdminModule,
        DeasEventsModule,
        CommonModule,
        RouterModule.forChild(
            [
                { path: 'admin', component: DeasAdminComponent },
                { path: 'events', component: DeasEventsComponent },
                { path: '', redirectTo: 'admin', pathMatch: 'full' }
            ]
        )
    ],
    declarations: [
        MapsComponent
    ]
})

export class DashboardModule{}
