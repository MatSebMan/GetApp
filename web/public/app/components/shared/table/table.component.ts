import { Component, Input, Output, EventEmitter, SimpleChanges } from '@angular/core';
import { NgZone } from '@angular/core'
import { DefaultServices } from '../defaultServices/defaultServices'
import { ModalScheduleComponent } from '../modalSchedule/modal-schedule.component'

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
    @Input() urlGetParameters:any;
    @Input() urlPost:any;
    @Input() editableRows:boolean = false;
    @Input() deletableRows:boolean = false;
    @Input() modifySchedule:boolean = false;
    @Input() editTitle: string = "Editar";
    @Input() createTitle: string = "Crear Nuevo";
    @Input() createTitleTooltip: string;
    @Input() camposEdicion;
    @Input() camposCreacion;
    @Input() color;
    @Input() addItemOption:boolean = false;
    @Input() exportableOption:boolean = true;
    @Input() headerButtons:any;
    @Input() bodyButtons:any;
    @Input() fillEdit:boolean = true;
    @Output() event = new EventEmitter();
    public keys:string[];
    public keysEdicion:string[];
    public keysCreacion:string[];
    showModalEdicion:boolean = false;
    showModalCreacion:boolean = false;
    showModalEliminar:boolean = false;
    showModalScheduler:boolean = false;
    isDataAvailable: boolean = false;
    reverseSort = false;
    createData:any = {};
    editData:any = {};
    deleteId:any;

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
        this.isDataAvailable = false;
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
        this.defaultService.getData(this.urlGet+(this.urlGetParameters?this.urlGetParameters:'')).subscribe(
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

    emitSelect(event) {
        this.event.emit(event);
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

    toggleModal(action, row = undefined) {
        if (action = 'schedule'){
            this.showModalScheduler = !this.showModalScheduler;            
        }
    }

    toogleModal(event, accion, row = undefined) {
        if (event.currentTarget === event.target) {
            if(accion=='editar') {
                if(row && row.Id != undefined) {
                    this.isDataAvailable = false;
                    this.editData = {};
                    if(this.fillEdit) {
                        var keysInverse = Object.keys(this.camposEdicion.text);
                        this.editData = {};
                        this.defaultService.getData(this.urlGet+'/'+row.Id).subscribe(
                            (data) => {
                                var keys = Object.keys(data[0]);
                                keys.forEach(key => {
                                    keysInverse.forEach(keyInv => {
                                        if(key == this.camposEdicion.text[keyInv])
                                            {
                                                this.editData[keyInv] = data[0][key];
                                            }
                                    });
                                });
                                this.editData.id = row.Id;
                                this.isDataAvailable = true;
                            },
                            err => console.error("EL ERROR FUE: ", err)
                        );
                    } else {
                        this.editData.id = undefined;
                    }
                }
                this.showModalEdicion= !this.showModalEdicion;
            }

            if(accion=='crear') {
                this.createData = {};
                this.showModalCreacion= !this.showModalCreacion;
            }

            if(accion=='eliminar') {
                if(row && row.Id != undefined) {
                    this.deleteId = row.Id;
                } else {
                    this.editData.id = undefined;
                }
                this.showModalEliminar= !this.showModalEliminar;
            }

            if(accion=='schedule') {
                this.showModalScheduler = !this.showModalScheduler;
            }
        }
    }

    eliminar(event) {
        console.log('Eliminando: ' + this.deleteId);
        if(this.urlPost) {
            this.defaultService.deleteData(this.urlPost, this.deleteId).subscribe(
                (res) => {
                    alert("Elemento eliminado");
                    this.init();
                },
                err => {
                    alert("Error al eliminar el objeto");
                    console.error("EL ERROR FUE: ", err)
                }
            );
            this.toogleModal(event, 'eliminar');
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
    }

    schedule(event) {
        if(this.urlPost) {

        }
    }

    ngOnChanges(changes: any) {        
        if(changes.urlGet || changes.urlGetParameters) {
            this.loadURLData();            
        }
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

    getOverlayButtonColor() {
        if(this.color=='red')
            return '#e53935';
        if(this.color=='purple')
            return '#8e24aa';
        if(this.color=='green')
            return '#43a047';
        if(this.color=='yellow')
            return '#8e24aa';
        // if(this.color=='vittal')
        //     return 'rgb(51, 177, 166)';
        return '#e53935';
    }

    isEmpty(key) {           
        return ((!this.editData[key]) && (this.camposEdicion.types[key] != 'date'));
    }

    geolocalizar(data, key, campos) {
        console.log('data');
        console.log(data);
        console.log('campos');
        console.log(campos);
        console.log('key');
        console.log(key);

        var url = 'https://maps.googleapis.com/maps/api/geocode/json?address=Argentina%20';
        //+data.calle + ' ' + data.numero;
        campos.geoloc[key].forEach(element => {
            if(data[element] != undefined)
                url += data[element] + '%20';
        });
        url += '&key=AIzaSyAXsFcYvoKDNREomDdq5lOhV4pS7AmZXvA';

        console.log(url);

        this.defaultService.getData(url).subscribe(
            (res) => {
                if(res.results.length == 0) {
                    data.geoloc_info = 'No se encontraron resultados. Especifique mejor la dirección.';
                } else if(res.results.length > 1) {
                    data.geoloc_info = 'Demasiados resultados. Especifique mejor la dirección.';
                }
                else {
                    data.latitud = res.results[0].geometry.location.lat;
                    data.longitud = res.results[0].geometry.location.lng;
                    data.geoloc_info = res.results[0].formatted_address;   
                }
            },
            err => console.error("EL ERROR FUE: ", err)
        );
        
    }
}
