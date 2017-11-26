var db = require('../config_db').db;
var controllerHelper = require('./helpers/controllerHelper')
var deaEventsController = require('./deaEventsController')
var editSchedule = require('./helpers/scheduleHelper')
var eventEnum = require('./helpers/deaEventsTypes')

exports.getScheduleByIdDea = function(req, res) {

    try {
        let idDea = req.params.id
        let queryString = " SELECT * FROM ScheduleAvailability WHERE iddea = $1"
        let values = [idDea]
    
        controllerHelper.resolveGetJson(res, queryString, values)
    }

    catch ( error){
        res.status(500).send(error.message)
    }
}

exports.editScheduleByIdDea = function(req, res){
    try {
        
        controllerHelper
            .create(res)
            .catch( err => res.status(500).json(500))
            .then( dbHelper => {

                let idDea = req.params.id
                
                let fetchCurrentSchedule = "SELECT id FROM ScheduleAvailability WHERE iddea = $1"
                let values = [idDea]

                dbHelper
                    .query(fetchCurrentSchedule, values)
                    .catch( err => res.status(500).json(err.message))
                    .then( dbHelper => 
                        {
                            let scheduleHelper = new editSchedule.ScheduleHelper(idDea, dbHelper.result.rows, req.body.content)
                            scheduleHelper.defineOutdatedSchedule()
                            updateSchedule(res, scheduleHelper, dbHelper)
                        })
                    })
    }

    catch ( error ) {
        res.status(500).send(error.message)
    }
}

var updateSchedule = function(res, scheduleHelper, dbHelper){
    
    if ( scheduleHelper.hasNextTimeBlock() ) {

        let timeBlock = scheduleHelper.nextTimeBlock()
    
        if ( timeBlock.Id == undefined ) {
    
            let queryString = "INSERT INTO ScheduleAvailability ( iddea, weekday, starttime, endtime) VALUES ($1, $2, $3, $4)"
            let values = [scheduleHelper.getIdDea(), timeBlock.Weekday, timeBlock.TimeStart, timeBlock.TimeEnd]
    
            dbHelper.query(queryString, values)
                .catch( err => res.status(500).json(err.message))
                .then( dbHelper => updateSchedule(res, scheduleHelper, dbHelper) )
        }
        
        else { updateSchedule(res, scheduleHelper, dbHelper) }
    }
    
    else { decideIfRemoveTimeBlockFromSchedule(res, scheduleHelper, dbHelper) }
}

var decideIfRemoveTimeBlockFromSchedule = function(res, scheduleHelper, dbHelper ) {

    if ( scheduleHelper.hasNextOutdatedTimeBlockId() ) { 

        removeFromSchedule(res, scheduleHelper, dbHelper) 
    }

    else { deaEventsController.addEvent(dbHelper, '', eventEnum.deaEventType.MODIFICACION_HORARIA, scheduleHelper.getIdDea() ) }
}

var removeFromSchedule = function(res, scheduleHelper, dbHelper) {

    let queryString = "DELETE FROM ScheduleAvailability WHERE id = $1"
    let values = [scheduleHelper.nextOutdatedTimeBlockId()]

    dbHelper
        .query(queryString, values)
        .catch( err => res.status(500).json(err.message))
        .then( dbRes => decideIfRemoveTimeBlockFromSchedule(res, scheduleHelper, dbHelper))
}
