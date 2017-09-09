import { Component, Input, Output, EventEmitter, SimpleChanges } from '@angular/core';
import { DefaultServices } from '../defaultServices/defaultServices'

@Component({
    selector: 'dropdown-cmp',
    moduleId: module.id,
    templateUrl: 'dropdown.component.html'
})

export class DropdownComponent{
    @Input() id:string;
    @Input() title:string;
    @Input() url: string;
    @Input() data: any[];
    @Input() selectedItem;
    @Output() selectionChanged = new EventEmitter();
    isDataAvailable:boolean = false;
    loading = false;
    loadingText = 'Cargando...'
    
    constructor(private service: DefaultServices) {}

    ngOnInit(){
        if(this.url && this.url != '') {
            this.selectedItem = undefined;;
            this.loading = true;
            this.service.getData(this.url).subscribe(
                (data) => {
                    this.data = data.slice(0);
                    this.loading = false;
                    this.isDataAvailable = true;
                },
                err => {
                    this.loading = false;
                    console.error("EL ERROR FUE: ", err)
                }
            );
        } else {
            this.isDataAvailable = true;
        }
    }

    select(item) {
        this.selectedItem = item;
        this.selectionChanged.emit({src: this.id, value: this.selectedItem});
    }

    ngOnChanges(changes: any) {
        if(changes.selectedItem && this.data)
        {
            this.data.forEach(element => {
                if(element.id == changes.selectedItem.currentValue)
                    this.select(element);
            });
        }

        if(changes.url && changes.url.currentValue != '') {
            this.selectedItem = undefined;
            this.loading = true;
            this.url = changes.url.currentValue;
            this.data = [];
            this.service.getData(this.url).subscribe(
                (data) => {
                    this.data = data.slice(0);
                    if(changes.selectedItem)
                    {
                        this.data.forEach(element => {
                            if(element.id == changes.selectedItem.currentValue)
                                this.selectedItem = element;
                        });
                    }
                    this.loading = false;
                    this.isDataAvailable = true;
                },
                err => {
                    this.loading = false;
                    console.error("EL ERROR FUE: ", err)
                }
            );
        }
    }
}
