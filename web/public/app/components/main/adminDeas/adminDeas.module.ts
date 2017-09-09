import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { CommonModule } from '@angular/common';
import { RouterModule } from '@angular/router';
import { HttpModule } from '@angular/http';
import { FormsModule } from '@angular/forms';
import { AdminDeasComponent } from './adminDeas.component';
import { TableComponent } from '../../shared/table/table.component';
import { ButtonLineComponent } from '../../shared/buttonLine/buttonLine.component';
import { DropdownComponent } from '../../shared/dropdown/dropdown.component';

import { AdminDeasServices } from './adminDeasServices';
import { DefaultServices } from '../../shared/defaultServices/defaultServices';
import { DropdownServices } from '../../shared/dropdown/dropdownServices';


@NgModule({
    imports: [ 
        RouterModule, 
        CommonModule, 
        HttpModule,
        BrowserModule,
        FormsModule
    ],
    declarations: [ 
        AdminDeasComponent, 
        TableComponent,
        ButtonLineComponent,
        DropdownComponent
    ],
    exports: [ AdminDeasComponent ],
    providers: [
        AdminDeasServices,
        DropdownServices,
        DefaultServices
    ]
})

export class AdminDeasModule {}
