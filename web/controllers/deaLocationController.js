var db = require('../config_db').db;
var controllerHelper = require('./helpers/controllerHelper')
var TIPO_CENTRO = 0;

exports.create = function(req, res) {
    console.log('POST/dea');
    console.log(req.body);
    // valido la entrada
    if(!req.body.zona_protegida || !req.body.latitud || !req.body.longitud )
        res.send(500);

    db.connect("getapp", function(client) {
        const queryString = 'INSERT INTO dea('
        +'zona_protegida,location,activo,provincia,localidad,partido,calle_nombre,calle_numero,telefono,referencia_interna,persona_contacto'
        +') VALUES($1,ST_MakePoint($3,$2),false,$4,$5,$6,$7,$8,$9,$10,$11)';
        const values = [];
        values.push(req.body.zona_protegida);
        values.push(req.body.latitud);
        values.push(req.body.longitud);
        values.push(req.body.provincia);
        values.push(req.body.localidad);
        values.push(req.body.partido);
        values.push(req.body.calle);
        values.push(req.body.numero);
        values.push(req.body.telefono);
        values.push(req.body.referencia_interna);
        values.push(req.body.persona_contacto);

        client.query(queryString, values, dbRes => {
            db.disconnect(client)
            res.status(200).send();
        },err => {
            db.disconnect(client)
            console.log(err.message);
            res.status(500).send(err.message);
        });
    }, function(err){
        res.status(500).send(err.message);
    });
};

exports.findNearestDeas = function(req, res) {
    console.log('GET/deaLocation');

    let currentDate = new Date();

    let currentDay = currentDate.getDay();
    let currentTime = currentDate.toLocaleTimeString();

    db.connect("getapp", function(client) {
        if(!req.query.cantidad || !req.query.latitud || !req.query.longitud )
            res.send(500, "Parametros incorrectos");
        var queryString = 
        'SELECT dea.id, dea.zona_protegida as nombre, ST_Y(dea.location) as latitud, ST_X(dea.location) as longitud FROM dea as dea, scheduleAvailability as sa WHERE ST_Distance_Sphere(dea.location,st_makepoint($2,$1))<1000 AND dea.id = sa.iddea AND sa.weekday = $5 AND sa.starttime <= $4 AND $4 < sa.endtime ORDER BY dea.location <-> st_makepoint($2,$1) LIMIT $3 ';
        console.log(queryString);
        const values = [];
        values.push(req.query.latitud);
        values.push(req.query.longitud);
        values.push(req.query.cantidad);
        values.push(currentTime);
        values.push(currentDay);
        client.query(queryString, values).then(dbRes => {
            db.disconnect(client)
            res.status(200).json(dbRes.rows);
        }).catch(err => {
            db.disconnect(client)
            res.status(500).send(err.message);
        });
    }, function(err){
        res.status(500).send(err.message);
    });
};

exports.findAll = function(req, res) {
    console.log('GET/deaList');
    var success = function(client) {
        var queryString = 
        'SELECT id as "Id",zona_protegida as "Nombre", calle_nombre || \' \' || calle_numero as "Calle", provincia as "Provincia", partido as "Partido", localidad as "Localidad",telefono as "Telefono",referencia_interna as "Ubicación",persona_contacto as "Contacto", activo as "Activo" FROM dea ORDER BY id';
        // 'ST_Y(location) as "Latitud", ST_X(location) as "Longitud", '
        client.query(queryString)
        .then(dbRes => {
            db.disconnect(client)
            res.status(200).json(dbRes.rows);
        })
        .catch(e => {
            db.disconnect(client)
            res.status(500).send(e.stack);
        })
    }
    var error = function(err){
        res.status(500).send(err.message);
    }
    db.connect("getapp",success, error);  
};

exports.findById = function(req, res) {
    console.log('GET/deaList/'+req.params.id);
    var success = function(client) {
        var queryString = 
        'SELECT id as "Id",zona_protegida as "Nombre Zona Protegida", ST_Y(location) as "Latitud", ST_X(location) as "Longitud",activo as "Activo", en_uso as "En Uso", '
        +' provincia as "Provincia",localidad as "Localidad",partido as "Partido",calle_nombre as "Calle",calle_numero as "Número",telefono as "Teléfono",referencia_interna as "Referencia de ubicación",persona_contacto as "Persona de Contacto"'
        +' FROM dea WHERE id = $1';
        const values = [];
        values.push(req.params.id);
        client.query(queryString, values)
        .then(dbRes => {
            db.disconnect(client)
            res.status(200).json(dbRes.rows);
        })
        .catch(e => {
            db.disconnect(client)
            res.status(500).send(e.stack);
        })
    }
    var error = function(err){
        res.status(500).send(err.message);
    }
    db.connect("getapp",success, error);  
};

exports.edit = function(req, res) {
    console.log('PUT/deaList/'+req.params.id);
    console.log(req.body);
    db.connect("getapp", function(client) {
        var queryString = 'UPDATE dea SET (zona_protegida,location,activo,en_uso,provincia,localidad,partido,calle_nombre,calle_numero,telefono,referencia_interna,persona_contacto) '
        +'= ($2,ST_MakePoint($4,$3),$5,$6,$7,$8,$9,$10,$11,$12,$13,$14) WHERE id = $1';
        const values = [];
        values.push(req.params.id);
        values.push(req.body.zona_protegida);
        values.push(req.body.latitud);
        values.push(req.body.longitud);
        values.push(req.body.activo);
        values.push(req.body.en_uso);
        values.push(req.body.provincia);
        values.push(req.body.localidad);
        values.push(req.body.partido);
        values.push(req.body.calle);
        values.push(req.body.numero);
        values.push(req.body.telefono);
        values.push(req.body.referencia_interna);
        values.push(req.body.persona_contacto);

        client.query(queryString, values).then(dbRes => {
            db.disconnect(client)
            res.status(200).send();
        }).catch(err => {
            db.disconnect(client)
            console.log(err);
            console.log(err.stack)
            res.status(500).send(err.stack);
        });
    }, function(err){
        res.status(500).send(err.stack);
    });
    res.status(200).send("");
}

exports.delete = function(req, res) {
    console.log('DELETE/deaList/'+req.params.id);

    try{
        controllerHelper
            .create()
            .then( dbHelper => {
    
                let idDea = req.params.id
                let queryString = "DELETE FROM ScheduleAvailability WHERE iddea = $1"
                const values = [idDea]
    
                dbHelper
                    .query(queryString, values)
                    .then( dbHelper => deleteDea(req, res, dbHelper))
                    .catch( err => res.status(500).send(err.message))
            })
            .catch( err => res.status(500).send(err.message))
    }

    catch(err) {
        res.status(500).send()
    }
}

var deleteDea = function(req, res, dbHelper) {

    let queryString = 'DELETE FROM dea WHERE id = $1'
    const values = []
    values.push(req.params.id)

    dbHelper
        .query(queryString, values)
        .then(dbHelper => dbHelper.commitAndResponse(res))
        .catch( err => err.status(500).send(err.message))
}