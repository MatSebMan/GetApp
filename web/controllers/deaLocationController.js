var db = require('../config_db').db;
var TIPO_CENTRO = 0;

exports.create = function(req, res) {
    console.log('POST/dea');
    console.log(req.body);
    // valido la entrada
    if(!req.body.zona_protegida || !req.body.latitud || !req.body.longitud )
        res.send(500);
    db.connect("getapp", function(client) {
        const queryString = 'INSERT INTO dea(zona_protegida,location) VALUES($1,ST_MakePoint($3,$2))';
        const values = [];
        values.push(req.body.zona_protegida);
        values.push(req.body.latitud);
        values.push(req.body.longitud);
        client.query(queryString, values, dbRes => {
            db.disconnect(client)
            res.status(200).send();
        },err => {
            db.disconnect(client)
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
        'SELECT id,zona_protegida as nombre, ST_Y(location) as latitud, ST_X(location) as longitud FROM dea ORDER BY location <-> st_makepoint($3,$2) LIMIT $1;';
        const values = [];
        values.push(req.body.cantidad);
        values.push(req.body.latitud);
        values.push(req.body.longitud);
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
        'SELECT id as "Id",zona_protegida as "Nombre", ST_Y(location) as "Latitud", ST_X(location) as "Longitud", \'\' as "Fecha de Entrega" FROM dea ORDER BY id';
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
        'SELECT id as "Id",zona_protegida as "Nombre Zona Protegida", ST_Y(location) as "Latitud", ST_X(location) as "Longitud", \'\' as "Fecha de Entrega" FROM dea WHERE id = $1';
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
        // Delete query (parametrizada para evitar sql injection)
        var queryString = 'UPDATE dea SET (zona_protegida,location) = ($2,ST_MakePoint($4,$3)) WHERE id = $1';
        const values = [];
        values.push(req.params.id);
        values.push(req.body.zona_protegida);
        values.push(req.body.latitud);
        values.push(req.body.longitud);
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