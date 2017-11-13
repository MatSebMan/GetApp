import { Component } from '@angular/core'

import { Schedule } from './schedule'
import { Weekday } from './weekday'
import { TimeBlock } from './timeblock'

import { DefaultServices } from '../defaultServices/defaultServices'

@Component({
    selector: 'modal-schedule',
    moduleId: module.id,
    templateUrl: 'modal-schedule.html',
    styleUrls: ['style.css'],
})

export class ModalScheduleComponent {

    private scheduleURL : string = '/api/v1/dea_schedule'

    private idDea : number

    public constructor(defaultService: DefaultServices) {
        this.defaultService = defaultService
        this.currentTimeBlock = new TimeBlock()
        this.schedule = new Schedule()
    }

    public schedule: Schedule

    public modalVisible:boolean = false

    private defaultService: DefaultServices

    private weekdays = [
        {index: '0', day: 'Lunes'}, 
        {index: '1', day: 'Martes'},
        {index: '2', day: 'Miércoles'},
        {index: '3', day: 'Jueves'},
        {index: '4', day: 'Viernes'},
        {index: '5', day: 'Sábado'},
        {index: '6', day: 'Domingo'}
    ]

    public currentTimeBlock: TimeBlock

    private weekday: Weekday = -1

    public startTime : string

    public endTime : string

    public error: Error

    public isVisible():boolean {
        return this.modalVisible
    }

    public toggle($event, id_dea, row = undefined){
        if (event.currentTarget === event.target) {
            this.modalVisible = !this.modalVisible
        }

        if ( this.modalVisible && id_dea != undefined ){
            this.loadSchedule(id_dea)
            this.idDea = id_dea
        }

        if ( !this.modalVisible ) {
            this.idDea = undefined
        }
    }

    public selectedWeekday(value:any):void {
        console.log('Selected value is: ', value);

        this.weekday = value
    }
    
    public removedWeekday(value:any):void {

        console.log('Removed value is: ', value)

        this.weekday = -1
    }

    public loadSchedule(id_dea: number) : void {
        this.defaultService
            .getData(this.scheduleURL + "/" + id_dea)
            .subscribe( data => this.loadScheduleFromJSON(data) , err => this.showErrorMessage(err))
    }

    private loadScheduleFromJSON(data) {
        console.log(data)

        this.schedule = new Schedule()

        data.forEach( dbTimeBlock => {
            
            let timeblock = new TimeBlock()

            timeblock.Id = dbTimeBlock.id
            timeblock.Weekday = dbTimeBlock.weekday
            timeblock.TimeStart = dbTimeBlock.starttime.substring(0,5)
            timeblock.TimeEnd = dbTimeBlock.endtime.substring(0,5)

            this.schedule.Add(timeblock)
        });
    }

    public updateSchedule($event) {

        let data = {
            id: this.idDea,
            content: this.schedule.GetHours()
        }

        try{   
            this.defaultService
                .putJsonData(this.scheduleURL, data )
                .subscribe( data => this.modalVisible = false , err => this.showErrorMessage(err))
        }

        catch  (error) {
            this.showErrorMessage(error)
        }
    }

    public addTimeBlock(): void {

        this.closeErrorMessage()

        try {
            this.currentTimeBlock.ValidateTimeStart()
            this.currentTimeBlock.ValidateTimeEnd()

            var timeBlock = new TimeBlock()
            timeBlock.Weekday = this.currentTimeBlock.Weekday
            timeBlock.TimeStart = this.currentTimeBlock.TimeStart
            timeBlock.TimeEnd = this.currentTimeBlock.TimeEnd

            this.schedule.Add(timeBlock)
        }

        catch( error ) {
            this.showErrorMessage(error)
        }
    }

    public removeTimeBlock(index) : void {

        this.closeErrorMessage()

        try {
            this.schedule.Remove(index)
        }

        catch(error) {
            this.showErrorMessage(error)
        }

    }

    public showErrorMessage(error:Error) : void{
        this.error = error
    }

    public closeErrorMessage(): void {
        this.error = undefined
    }
}