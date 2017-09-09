var db = require('../config_db').db;
var TIPO_CENTRO = 0;

exports.findAll = function(req, res) {
    var deas = [
        {"Id": 1, "Ubicacion": 'Gimnasio Anabolic', "Fecha de Entrega": '08-07-2017'},
        {"Id": 2, "Ubicacion": 'Facultad de Ingenieria UBA', "Fecha de Entrega": '01-02-2015'},
        {"Id": 3, "Ubicacion": 'Hospital Álvarez', "Fecha de Entrega": '03-12-2017'},
        {"Id": 4, "Ubicacion": 'Hospital Churruca', "Fecha de Entrega": '21-09-2009'},
    ]
    res.status(200).jsonp(deas);
};