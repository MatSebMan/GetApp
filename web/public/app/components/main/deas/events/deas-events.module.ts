import { NgModule } from '@angular/core';
import { TableModule } from '../../../shared/table/table.module';
import { DeasEventsComponent } from './deas-events.component';

@NgModule({
    imports: [ TableModule ],
    declarations: [ DeasEventsComponent ],
    exports: [ DeasEventsComponent ],
})

export class DeasEventsModule {
}
