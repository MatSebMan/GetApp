import { NgModule } from '@angular/core';
import { Route, RouterModule } from '@angular/router';
import { CommonModule } from '@angular/common';
import { DeasComponent } from './adminDeas/deas.component';
import { HomeComponent } from './home/home.component';

import { AdminDeasModule } from './adminDeas/adminDeas.module';

@NgModule({
    imports: [
        AdminDeasModule,
        CommonModule,
        RouterModule.forChild(
            [
                { path: 'home', component: HomeComponent },
                { path: 'adminDea', component: DeasComponent },
                { path: '', redirectTo: 'home', pathMatch: 'full' }
            ]
        )
    ],
    declarations: [
        HomeComponent,
        DeasComponent,
    ]
})

export class DashboardModule{}
