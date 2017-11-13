var db = require('../config_db').db;
var controllerHelper = require('./helpers/controllerHelper')
var editSchedule = require('./helpers/scheduleHelper')

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

    let idDea = req.params.id

    let fetchCurrentSchedule = "SELECT id FROM ScheduleAvailability WHERE iddea = $1"
    let values = [idDea]

    try {
        controllerHelper
            .resolveQuery(fetchCurrentSchedule, values)
            .catch( err => res.status(500).json(err.message))
            .then( dbRes => 
                {
                    let scheduleHelper = new editSchedule.ScheduleHelper(idDea, dbRes.rows, req.body.content)
                    scheduleHelper.defineOutdatedSchedule()
                    updateSchedule(res, scheduleHelper)
                })
    }

    catch ( error ) {
        res.status(500).send(error.message)
    }
}

var updateSchedule = function(res, scheduleHelper){
    
    if ( scheduleHelper.hasNextTimeBlock() ) {

        let timeBlock = scheduleHelper.nextTimeBlock()
    
        if ( timeBlock.Id == undefined ) {
    
            let queryString = "INSERT INTO ScheduleAvailability ( iddea, weekday, starttime, endtime) VALUES ($1, $2, $3, $4)"
            let values = [scheduleHelper.getIdDea(), timeBlock.Weekday, timeBlock.TimeStart, timeBlock.TimeEnd]
    
            controllerHelper
                .resolveQuery(queryString, values)
                .catch( err => res.status(500).json(err.message))
                .then( dbRes => updateSchedule(res, scheduleHelper) )
        }
        
        else { updateSchedule(res, scheduleHelper) }
    }
    
    else { decideIfRemoveTimeBlockFromSchedule(res, scheduleHelper) }
}

var decideIfRemoveTimeBlockFromSchedule = function(res, scheduleHelper ) {

    if ( scheduleHelper.hasNextOutdatedTimeBlockId() ) { 

        removeFromSchedule(res, scheduleHelper) 
    }

    else { res.status(200).send() }
}

var removeFromSchedule = function(res, scheduleHelper) {

    let queryString = "DELETE FROM ScheduleAvailability WHERE id = $1"
    let values = [scheduleHelper.nextOutdatedTimeBlockId()]

    controllerHelper
        .resolveQuery(queryString, values)
        .catch( err => res.status(500).json(err.message))
        .then( dbRes => decideIfRemoveTimeBlockFromSchedule(res, scheduleHelper))
}
