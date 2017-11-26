exports.DbHelper = class {

    constructor(_db, _client, _res){

        this.db = _db
        this.client = _client
        this.res = _res
        this.err = undefined
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

    doQuery(queryString, values) {
        
        let thisClass = this 

        return new Promise( function(resolve, reject){

            thisClass.err = undefined

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
                        thisClass.err = err
                        reject(thisClass)
                    })
        })
    }

    commit() {
        let thisClass = this

        return new Promise( function(resolve, reject){

            thisClass.client
                .query("commit;")
                .then( response => { 
                    thisClass.db.disconnect(thisClass.client) 
                    resolve(response)
                })

                .catch( error => {
                    thisClass.db.disconnect(thisClass.client)
                    reject(error)
                })
        })  
    }

    responseResultsJson() {
        this.client
            .query("commit;")
            .then( response => { 
                this.db.disconnect(this.client)
                this.res.status(200).jsonp(this.result.rows)
            })

            .catch( error => {
                this.db.disconnect(this.client)
                this.res.status(500).send(error.message)
            })
    }

    responseOk() {
        this.client
            .query("commit;")
            .then( response => { 
                this.db.disconnect(this.client)
                this.res.status(200).send()
            })

            .catch( error => {
                this.db.disconnect(this.client)
                this.res.status(500).send(error.message)
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
                    thisClass.db.disconnect(thisClass.client) 
                    resolve(response)
                })

                .catch( error => {
                    thisClass.db.disconnect(thisClass.client)
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

    responseWithError() {
        this.res.status(500).json(this.err.message)
    }

}