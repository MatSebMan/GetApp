var db = require('../config_db').db;
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
    db.connect("getapp", function(client) {
        if(!req.query.cantidad || !req.query.latitud || !req.query.longitud )
            res.send(500, "Parametros incorrectos");
        var queryString = 
        'SELECT id,zona_protegida as nombre, ST_Y(location) as latitud, ST_X(location) as longitud FROM dea WHERE ST_Distance_Sphere(location,st_makepoint($2,$1))<1000 ORDER BY location <-> st_makepoint($2,$1) LIMIT $3';
        console.log(queryString);
        const values = [];
        values.push(req.query.latitud);
        values.push(req.query.longitud);
        values.push(req.query.cantidad);
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
    db.connect("getapp", function(client) {
        // Delete query (parametrizada para evitar sql injection)
        var queryString = 'DELETE FROM dea WHERE id = $1';
        const values = [];
        values.push(req.params.id);
        client.query(queryString, values).then(dbRes => {
            db.disconnect(client)
            res.status(200).send();
        }).catch(err => {
            db.disconnect(client)
            res.status(500).send(err.message);
        });
    }, function(err){
        res.status(500).send(err.message);
    });
}