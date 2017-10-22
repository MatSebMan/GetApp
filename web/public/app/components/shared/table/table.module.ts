import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';

import { TableComponent } from './table.component';
import { DropdownComponent } from '../../shared/dropdown/dropdown.component';
import { ButtonLineComponent } from '../../shared/buttonLine/buttonLine.component';

import { DefaultServices } from '../../shared/defaultServices/defaultServices';
import { DropdownServices } from '../../shared/dropdown/dropdownServices';

@NgModule({
    imports: [ CommonModule, FormsModule, HttpModule ],
    declarations: [ TableComponent, DropdownComponent, ButtonLineComponent ],
    exports: [ TableComponent ],
    providers: [ DropdownServices, DefaultServices ]
})

export class TableModule {
    constructor( dropdownServices: DropdownServices , defaultServices: DefaultServices) { }
}