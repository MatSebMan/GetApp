var controllerHelper = require('./helpers/controllerHelper')
var eventEnum = require('./helpers/deaEventsTypes')

exports.findAll = function(req, res) {

    controllerHelper.create(res)
    
        .catch( err => {
            console.log(err.message);
            res.status(500).send(err.message);
        })
        
        .then( dbHelper => {
        
            let query = 'SELECT eventtype as "Evento", deaprotectedzonename as "Zona Protegida", '
            +' deaLocation as "Ubicación", to_char(eventtime, \'DD-MM-YYYY\') as "Fecha", '
            +' to_char(eventtime, \'HH24:MI:SS\') as "Hora", username as "Usuario" '
            +' FROM deaEvent ORDER BY id DESC'

            dbHelper
                .doQuery(query, null)
                .catch(dbHelper => dbHelper.responseWithError())
                .then( dbHelper => {
                    
                    dbHelper.result.rows.forEach( element => {
                        element.Evento = eventEnum.deaEventType.properties[element.Evento].name
                    })

                    dbHelper.responseResultsJson()
                })
        })
};

exports.addEvent = function (dbHelper, username, eventTypeNumber, iddea) {
    
    let fetchDeaProtectedZoneName = "SELECT * from dea where id = $1"
    let values = [iddea]

    dbHelper
        .doQuery( fetchDeaProtectedZoneName, values)
        .catch( dbHelper => dbHelper.responseWithError() )
        .then( dbHelper =>
            {
                let dea = dbHelper.result.rows[0]

                let fetchDeaProtectedZoneName = "INSERT INTO deaEvent (eventtype, iddea, deaprotectedzonename, deaLocation, eventtime, username) VALUES ($1, $2, $3, $4, clock_timestamp(), $5)"
                let ubicacion = dea.localidad
                
                if ( dea.calle_nombre != undefined && dea.calle_numero != undefined ) {
                    ubicacion = dea.calle_nombre + ' ' + dea.calle_numero + ', ' + dea.localidad
                }
            
                let insertValues = [eventTypeNumber, dea.id, dea.zona_protegida, ubicacion, username ]

                dbHelper
                    .doQuery(fetchDeaProtectedZoneName, insertValues)
                    .catch( dbHelper => dbHelper.responseWithError())
                    .then( dbHelper => dbHelper.responseOk())
            })
}

exports.addEventFromDEA = function (dbHelper, username, eventTypeNumber, dea) {

    let fetchDeaProtectedZoneName = "INSERT INTO deaEvent (eventtype, iddea, deaprotectedzonename, deaLocation, eventtime, username) VALUES ($1, $2, $3, $4, clock_timestamp(), $5)"
    
    let ubicacion = dea.localidad
    
    if ( dea.calle_nombre != undefined && dea.calle_numero != undefined ) {
        ubicacion = dea.calle_nombre + ' ' + dea.calle_numero + ', ' + dea.localidad
    }

    let insertValues = [eventTypeNumber, dea.id, dea.zona_protegida, ubicacion, username ]

    dbHelper
        .doQuery(fetchDeaProtectedZoneName, insertValues)
        .catch( dbHelper => dbHelper.responseWithError())
        .then( dbHelper => dbHelper.responseOk())
}