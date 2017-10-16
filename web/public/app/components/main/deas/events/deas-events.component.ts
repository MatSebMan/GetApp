import { Component } from '@angular/core';
import { TableComponent } from '../../../shared/table/table.component'
import { DeasEventsServices } from './deas-events.services';

import {URL_DEAS_EVENTS} from '../../../rutas';

declare var jQuery:any;

@Component({
    moduleId: module.id,
    selector: 'deas-event-cmp',
    templateUrl: 'deas-events.html',
    providers: [ DeasEventsServices ],
})

export class DeasEventsComponent {

    constructor( deasEventsServices: DeasEventsServices ) { }

    DeasEventsURL : string = URL_DEAS_EVENTS;
}
