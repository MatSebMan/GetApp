import { Component, Input, Output, EventEmitter } from '@angular/core';

@Component({
    selector: 'button-line-cmp',
    moduleId: module.id,
    templateUrl: 'buttonLine.component.html'
})

export class ButtonLineComponent {
    @Input() data:any;
    @Output() selectionChanged = new EventEmitter();
    public selected = 0;
    constructor() {
        this.selectionChanged.emit(this.selected);
    }
    ngOnInit(){
    }
    getColor(item){
        return item.color;
    }

    select(id){
        this.selected = id;
        this.selectionChanged.emit(this.selected);
    }
}
