exports.ScheduleHelper = class {

    constructor(idDea, oldSchedule, newSchedule) {
        this.IdDea = idDea
        this.OldSchedule = oldSchedule ? oldSchedule : [];
        this.NewSchedule = newSchedule
        this.OutdatedSchedule = []
        this.CurrentNewTimeBlock = 0
        this.CurrentOutdatedTimeBlock = 0
    }

    defineOutdatedSchedule() {

        for( let index = 0; index < this.OldSchedule.length; index++ ) {
            
            let timeBlock = this.OldSchedule[index]
            
            let result = this.NewSchedule.findIndex( dbTimeBlock => dbTimeBlock.Id != undefined && dbTimeBlock.Id == timeBlock.id )

            if ( result == -1  ){

                this.OutdatedSchedule.push(timeBlock.id)
            }
        }

    }

    getIdDea() {
        return this.IdDea
    }

    hasNextTimeBlock(){
        return this.CurrentNewTimeBlock < this.NewSchedule.length
    }

    nextTimeBlock() {
        return this.NewSchedule[this.CurrentNewTimeBlock++]
    }

    hasNextOutdatedTimeBlockId() {
        return this.CurrentOutdatedTimeBlock < this.OutdatedSchedule.length
    }

    nextOutdatedTimeBlockId() {
        return this.OutdatedSchedule[this.CurrentOutdatedTimeBlock++]
    }

}