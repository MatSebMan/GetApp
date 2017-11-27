var db = require('../../config_db').db
var db_Helper = require('./dbHelper')

exports.resolveGetJson = function(res, queryString, values) {

    db.connectDB("getapp")
        .then( client => getJSON( res, client, queryString, values))
        .catch( err => res.status(500).send(err.message) )
}

var getJSON = function( res, client, queryString, values ) {
    
        client    
            .query(queryString, values)

            .then( dbRes => {
                db.disconnect(client)
                res.status(200)
                res.json(dbRes.rows)
            })
    
            .catch( err => {
                db.disconnect(client)
                res.status(500)
                res.json(err.message)
            })
}

exports.create = function(res) {
    return new Promise( function(resolve, reject){

        db.connectDB("getapp")
            .then( client => {
                let dbHelper = new db_Helper.DbHelper(db, client, res)
                dbHelper.begin()
                resolve( dbHelper ) 
            })
            .catch( err => reject(err) )
    })
}

exports.resolveQuery = function(queryString, values) {  
    return new Promise( function(resolve, reject){

        db.connectDB("getapp")
            .then( client => dbQuery(client, queryString, values, resolve, reject))
            .catch( err => reject(err) )
    })
}

var dbQuery = function( client, queryString, values, resolve, reject ) {

    client
        .query(queryString, values)
        .then( dbRes => resolve(dbRes) )
        .catch( err => reject(err) )
}