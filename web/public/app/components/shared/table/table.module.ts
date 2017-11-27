import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { HttpModule } from '@angular/http';

import { TableComponent } from './table.component';
import { DropdownComponent } from '../../shared/dropdown/dropdown.component';
import { ButtonLineComponent } from '../../shared/buttonLine/buttonLine.component';

import { DefaultServices } from '../../shared/defaultServices/defaultServices';
import { DropdownServices } from '../../shared/dropdown/dropdownServices';

import { ModalScheduleModule } from '../modalSchedule/modal-schedule.module';

@NgModule({
    imports: [ CommonModule, FormsModule, HttpModule, ModalScheduleModule ],
    declarations: [ TableComponent, DropdownComponent, ButtonLineComponent ],
    exports: [ TableComponent, ModalScheduleModule ],
    providers: [ DropdownServices, DefaultServices ]
})

export class TableModule {
    constructor( dropdownServices: DropdownServices , defaultServices: DefaultServices) { }
}
