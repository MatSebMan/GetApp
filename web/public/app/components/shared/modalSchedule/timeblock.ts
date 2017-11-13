import { Weekday } from './weekday'
import { DiaDeSemana } from './weekday'

export class TimeBlock {

    public Id : number

    public Weekday : Weekday

    public TimeStart : String

    public TimeEnd : String

    private static TimePattern = /^((0[0-9]|1[0-9]|2[0-3]):[0-5][0-9])|24:00$/

    public constructor() {
        this.Id = undefined

        this.TimeStart = "09:00"
        this.TimeEnd = "18:00"

        this.Weekday = Weekday.Monday
    }
    
    public ValidateTimeStart() : void {
        
        if( !TimeBlock.TimePattern.test(this.TimeStart.toString()) ){
            throw new Error("La hora de inicio no respeta el formato ( 00:00 a 24:00)")
        }
    }
    
    public ValidateTimeEnd() : void {

        if( !TimeBlock.TimePattern.test(this.TimeEnd.toString()) ){
            throw new Error("La hora de fin no respeta el formato ( 00:00 a 24:00)")
        }
    }

    public Validate() {

        if ( this.TimeEnd < this.TimeStart) {
            throw new Error("La fecha de fin debe ser superior a la de inicio")
        }

        return true;
    }

    public GetDayName(): string {
        return DiaDeSemana[this.Weekday];
    }

    public GetTimeStart(): String {
        return this.TimeStart.substring(0, 5)
    }

    public GetTimeEnd(): String {
        return this.TimeEnd.substring(0,5)
    }

}