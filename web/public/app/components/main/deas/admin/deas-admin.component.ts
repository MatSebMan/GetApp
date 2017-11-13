import { Component } from '@angular/core';
import { TableComponent } from '../../../shared/table/table.component'
import { DeasAdminServices } from './deas-admin.services';
import { URL_LISTA_DEAS } from '../../../rutas';
import { creacionDea } from './campos-creacion-deas'
import { edicionDea } from './campos-edicion-deas'

declare var jQuery:any;

@Component({
    moduleId: module.id,
    selector: 'admin-deas-cmp',
    templateUrl: 'deas-admin.html',
    providers: [DeasAdminServices],
})

export class DeasAdminComponent {

    constructor( deasAdminServices: DeasAdminServices) { }

    public ListadoDEAsURL:string = URL_LISTA_DEAS
    public camposEdicionDEA:any = edicionDea;
    public camposCreacionDEA:any = creacionDea;
    
}
