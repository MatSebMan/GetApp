import { NgModule } from '@angular/core';
import { TableModule } from '../../../shared/table/table.module';
import { DeasAdminComponent } from './deas-admin.component';

@NgModule({
    imports: [ TableModule ],
    declarations: [ DeasAdminComponent ],
    exports: [ DeasAdminComponent ],
})

export class DeasAdminModule {
}
