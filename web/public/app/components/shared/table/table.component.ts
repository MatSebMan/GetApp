import { Component, Input, SimpleChange } from '@angular/core';
import { NgZone } from '@angular/core'
import { DefaultServices } from '../defaultServices/defaultServices'

var $:any;

@Component({
    selector: 'table-cmp',
    moduleId: module.id,
    templateUrl: 'table.component.html',
    styleUrls: ['style.css']
})

export class TableComponent {
    @Input() title:string;
    @Input() subtitle:string;
    @Input() data:any;
    @Input() urlGet:any;
    @Input() urlPost:any;
    @Input() editableRows:boolean = false;
    @Input() editTitle: string = "Editar";
    @Input() createTitle: string = "Crear Nuevo";
    @Input() createTitleTooltip: string;
    @Input() camposEdicion;
    @Input() camposCreacion;
    @Input() color;
    @Input() addItemOption:boolean = false;
    @Input() exportableOption:boolean = true;
    public keys:string[];
    public keysEdicion:string[];
    public keysCreacion:string[];
    showModalEdicion:boolean = false;
    showModalCreacion:boolean = false;
    isDataAvailable: boolean = false;
    reverseSort = false;
    createData:any = {};
    editData:any = {};

    constructor(private defaultService: DefaultServices) {}

    ngOnInit() {
        this.init();
    }

    init() {
        this.showModalEdicion = false;
        this.showModalCreacion = false;
        this.reverseSort = false;
        this.createData = {};
        this.editData = {};
        if(this.urlGet) {
            this.loadURLData();
        } else {
            if(this.data && this.data.length > 0)
                this.keys = Object.keys(this.data[0]);
            else
                this.keys = [];
            if(this.camposEdicion && this.camposEdicion.text)
                this.keysEdicion = Object.keys(this.camposEdicion.text);
            else
                this.keysEdicion = [];
            if(this.camposCreacion && this.camposCreacion.text)
                this.keysCreacion = Object.keys(this.camposCreacion.text);
            else
                this.keysCreacion = [];
            this.isDataAvailable = true
        }
    }

    loadURLData() {
        this.defaultService.getData(this.urlGet).subscribe(
            (data) => {
                this.data = data.splice(0);
                if(this.data && this.data.length > 0)
                    this.keys = Object.keys(this.data[0]);
                else
                    this.keys = [];
                if(this.camposEdicion && this.camposEdicion.text)
                    this.keysEdicion = Object.keys(this.camposEdicion.text);
                else
                    this.keysEdicion = [];
                if(this.camposCreacion && this.camposCreacion.text)
                    this.keysCreacion = Object.keys(this.camposCreacion.text);
                else
                    this.keysCreacion = [];
                this.isDataAvailable = true
            },
            err => console.error("EL ERROR FUE: ", err)
        );
    }

    handleSelection(event, modo) {
        if(modo=='crear') {
            this.createData[event.src] = event.value.id;
            var changeField = this.camposCreacion.types[event.src].on_change_field;
            if(!changeField) return;
            this.camposCreacion.types[changeField].url_get = this.camposCreacion.types[event.src].on_change_value_set+'?'+event.src+'='+event.value.id;
        }
        if(modo=='editar') {
            this.editData[event.src] = event.value.id;
            var changeField = this.camposEdicion.types[event.src].on_change_field;
            if(!changeField) return;
            this.camposEdicion.types[changeField].url_get = this.camposEdicion.types[event.src].on_change_value_set+'?'+event.src+'='+event.value.id;
        }
    }

    toogleModal(event, accion, row = undefined) {
        if (event.currentTarget === event.target) {
            if(accion=='editar') {
                if(row && row.Id) {
                    var keys = Object.keys(row);
                    var keysInverse = Object.keys(this.camposEdicion.text);
                    
                    this.defaultService.getData(this.urlGet+'/'+row.Id).subscribe(
                        (data) => {
                            keys.forEach(key => {
                                keysInverse.forEach(keyInv => {
                                    if(key == this.camposEdicion.text[keyInv])
                                        this.editData[keyInv] = data[0][key];
                                });
                            });
                            this.editData.id = row.Id;           
                        },
                        err => console.error("EL ERROR FUE: ", err)
                    );
                }
                this.showModalEdicion= !this.showModalEdicion;
            }
            if(accion=='crear') {
                this.showModalCreacion= !this.showModalCreacion;
            }
        }
    }

    crear(event) {
        if(this.urlPost) {
            this.defaultService.postJsonData(this.urlPost, this.createData).subscribe(
                (res) => {
                    alert("Alta exitosa");
                    this.init();
                },
                err => {
                    alert("Error al crear el objeto");
                    console.error("EL ERROR FUE: ", err)
                }
            );
            this.toogleModal(event, 'crear');
        }
    }
    editar(event) {
        if(this.urlPost) {
            this.defaultService.putJsonData(this.urlPost, this.editData).subscribe(
                (res) => {
                    alert("Modificación exitosa");
                    this.init();
                },
                err => {
                    alert("Error al crear el objeto");
                    console.error("EL ERROR FUE: ", err)
                }
            );
            this.toogleModal(event, 'editar');
        }
        // console.log(this.editData);
        // this.toogleModal(event, 'editar');
    }

    isCombo(item) {
        return (typeof item == 'object' && item.type == 'select')
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
        if(this.color=='vittal')
            return 'vittal';
        return 'danger';
    }

    compare(key, reverse) {
        return function(a, b) {
            if (a[key] < b[key])
                return reverse?1:-1;
            if (a[key] > b[key])
                return reverse?-1:1;
            return 0;
        }
    }

    sortBy(key) {
        this.data.sort(this.compare(key, this.reverseSort));
        this.reverseSort = !this.reverseSort;
    }

    json_to_csv() {
        var str = '';
        
        var line = '';
        for (var index in this.keys) {
            if (line != '') line += ','
            line += this.keys[index];
        }
        str += line + '\r\n';
        for (var i = 0; i < this.data.length; i++) {
            var line = '';
            for (var index in this.keys) {
                if (line != '') line += ','
                line += this.data[i][this.keys[index]];
            }
            str += line + '\r\n';
        }
        let strReplace = [
            {from: 'á', to: 'a'},
            {from: 'é', to: 'e'},
            {from: 'í', to: 'i'},
            {from: 'ó', to: 'o'},
            {from: 'ú', to: 'u'},
            {from: 'null', to: ''}
        ]
        var res = str;
        strReplace.forEach(element => {
            var myRegExp = new RegExp(element.from,'gim');
            res = res.replace(myRegExp, element.to);
        });        
        return res;
    }

    download_as_file = function (data, filename) {
        var hiddenElement = document.createElement('a');

        document.body.appendChild(hiddenElement);
        hiddenElement.href = 'data:attachment/text,' + encodeURIComponent(data);
        hiddenElement.target = '_blank';
        hiddenElement.download = filename;
        hiddenElement.click();
    }

    export() {
        this.download_as_file(this.json_to_csv(), this.title+'.csv');
    }
}
