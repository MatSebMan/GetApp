import { NgModule } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';

import { Schedule } from './schedule'
import { ModalScheduleComponent } from './modal-schedule.component'

@NgModule({
    imports: [ CommonModule, FormsModule ],
    declarations: [ ModalScheduleComponent, Schedule ],
    exports: [ ModalScheduleComponent ],
    providers: [ Schedule ]
})

export class ModalScheduleModule {
}