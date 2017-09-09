import { Component, Input, SimpleChange } from '@angular/core';
import { NgZone } from '@angular/core'
// import { ModalComponent } from '../modal/modal.component';

var $:any;

@Component({
    selector: 'modal-editar-cmp',
    moduleId: module.id,
    templateUrl: 'modal.component.html',
    styleUrls: ['style.css']
})

export class ModalComponent {
    @Input() url:any;
    @Input() editTitle: string = "Editar";
    @Input() camposEdicion;
    @Input() color;
    public keysEdicion:string[];
    @Input() showModal:boolean = false;
    isDataAvailable: boolean = false;

    constructor() {}

    ngOnInit() {
        if(this.camposEdicion.text)
            this.keysEdicion = Object.keys(this.camposEdicion.text);
        else
            this.keysEdicion = [];
        this.isDataAvailable = true
    }
    
    toogleModal(event) {
        if (event.currentTarget === event.target) {
            this.showModal= !this.showModal;        
        }
    }

    isCombo(key) {
        return (typeof this.camposEdicion.types[key] == 'object' && this.camposEdicion.types[key].type == 'select')
    }

    getColor() {
        return this.color;
    }

    buttonType() {
        let color = this.getColorType();
        if(color!='')
            return 'btn-'+color;
        else
            return 'btn-danger';
    }

    getColorType() {
        if(this.color=='red')
            return 'danger';
        if(this.color=='purple')
            return 'primary';
        if(this.color=='green')
            return 'success';
        if(this.color=='yellow')
            return 'warning';
        return 'danger';
    }
}
