var db = require('../config_db').db;
var TIPO_CENTRO = 0;

exports.findAll = function(req, res) {
    var deas = [
        {"Id": 1, "Nombre": 'Gimnasio Anabolic', "Fecha de Entrega": '08-07-2017'},
        {"Id": 2, "Nombre": 'Facultad de Ingenieria UBA', "Fecha de Entrega": '01-02-2015'},
        {"Id": 3, "Nombre": 'Hospital Álvarez', "Fecha de Entrega": '03-12-2017'},
        {"Id": 4, "Nombre": 'Hospital Churruca', "Fecha de Entrega": '21-09-2009'},
        {id: 5, "Nombre": 'Vittal', "Fecha de Entrega": '19-10-2009'},
    ]
    res.status(200).jsonp(deas);
};

exports.findLocations = function(req, res) {
    var deas = [
        {id: 1, nombre: 'Gimnasio Anabolic', latitud: -34.594171, longitud: -58.4561782},
        {id: 2, nombre: 'Facultad de Ingenieria UBA', latitud: -34.5931761, longitud: -58.4618553},
        {id: 3, nombre: 'Hospital Álvarez', latitud: -34.5922679, longitud: -58.4640788},
        {id: 4, nombre: 'Hospital Churruca', latitud: -34.5893298, longitud: -58.4643988},
        {id: 5, nombre: 'Vittal', latitud: -34.5785948, longitud: -58.4579118},
    ]
    res.status(200).jsonp(deas);
};
