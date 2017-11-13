exports.DbHelper = class {

    constructor(_db, _client, _res){

        this.db = _db
        this.client = _client
        this.result = undefined
    }

    begin() {
        return this.client.query("begin;")
    }

    query(queryString, values) {

        let thisClass = this 

        return new Promise( function(resolve, reject){

            thisClass.client
                    .query(queryString, values)
                    
                    .then( dbRes => {
                        thisClass.result = dbRes
                        resolve(thisClass)
                    })

                    .catch( err => {
                        console.log("error")
                        console.log(err.stack)
                        console.log(err.reason)
                        thisClass.rollback()
                        reject(err)
                    })
        })
    }

    commit() {
        let thisClass = this

        return new Promise( function(resolve, reject){

            thisClass.client
                .query("commit;")
                .then( response => { 
                    thisClass.db.disconnect(this.client) 
                    resolve(response)
                })

                .catch( error => {
                    thisClass.db.disconnect(this.client)
                    reject(error)
                })
        })  
    }

    commitAndResponse(res) {
        this.client
            .query("commit;")
            .then( response => { 
                this.db.disconnect(this.client) 
                res.status(200).send()
            })

            .catch( error => {
                this.db.disconnect(this.client)
                res.status(500).send(error.message)
            })
    }

    rollback() {
        let thisClass = this
        
        return new Promise( function(resolve, reject){

            thisClass.client
                .query("rollback;")
                .then( response => { 
                    thisClass.db.disconnect(this.client) 
                    resolve(response)
                })

                .catch( error => {
                    thisClass.db.disconnect(this.client)
                    reject(error)
                })
        })  
    }

    rollbackAndResponse(res) {
        this.client
            .query("rollback;")
            .then( response => { 
                this.db.disconnect(this.client) 
                res.status(500).send("rollback")
            })

            .catch( error => {
                this.db.disconnect(this.client)
                res.status(500).send(error.message)
            })
    }

}