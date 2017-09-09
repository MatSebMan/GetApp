import { Component } from '@angular/core';
import { TableComponent } from '../../shared/table/table.component'
import { ButtonLineComponent } from '../../shared/buttonLine/buttonLine.component'
import { DefaultServices } from '../../shared/defaultServices/defaultServices'

import {URL_LISTA_DEAS} from '../../rutas';

declare var jQuery:any;

@Component({
    selector: 'admin-deas-cmp',
    moduleId: module.id,
    templateUrl: 'adminDeas.component.html'
})

export class AdminDeasComponent {
    public ListadoDEAsURL:string = URL_LISTA_DEAS
    public camposEdicionDEA:any = {}
    public camposCreacionDEA:any = {}
    
}
