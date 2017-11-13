import { TimeBlock } from './timeblock'
import { Pipe, PipeTransform } from "@angular/core";

@Pipe({ name: 'defaultorder' })

export class Schedule implements PipeTransform {

    private Hours : TimeBlock[]

    public constructor() {

        this.Hours = []
    }

    public GetHours() {
        return this.Hours
    }

    public Add(timeBlock : TimeBlock) {
        
        console.log("valid")

        timeBlock.Validate()

        this.ValidateTimeSuperposition(timeBlock)
        
        this.Hours.push(timeBlock)

        this.Hours = this.transform(this.Hours)
    }

    private ValidateTimeSuperposition(timeBlock : TimeBlock) {

        for( let block of this.Hours) {

            var sameDay : boolean = ( block.Weekday == timeBlock.Weekday )

            if ( sameDay && timeBlock.TimeStart >= block.TimeStart && timeBlock.TimeStart < block.TimeEnd) {
                throw new Error("La hora de inicio se suporpone con la de otro horario ingresado")
            }

            if ( sameDay && timeBlock.TimeEnd > block.TimeStart && timeBlock.TimeEnd <= block.TimeEnd ) {
                throw new Error("La hora de fin se suporpone con la de otro horario ingresado")
            }

            if ( sameDay && timeBlock.TimeStart < block.TimeStart && timeBlock.TimeEnd > block.TimeEnd ) {
                throw new Error("El rango se superpone con otro rango ingresado previamente")
            }

        }
    }

    public Remove(timeBlock: TimeBlock): void {

        let index = this.Hours.indexOf(timeBlock)

        if ( index == - 1 ){
            throw new Error("No se puede eliminar el elemento deseado");
        }

        this.Hours.splice(index, 1)        
    }

    transform(hours: TimeBlock[], ...args: any[]) : TimeBlock[] {

        if ( hours ) {

            // ordena según la hora de inicio

            hours.sort( (timeBlock1, timeBlock2) => {

                if ( timeBlock1.Weekday > timeBlock2.Weekday ) {
                    return 1
                }

                if ( timeBlock1.Weekday == timeBlock2.Weekday && timeBlock1.TimeStart > timeBlock2.TimeStart ) {
                    return 1
                }

                return 0

            })
        }

        return hours
    }
}