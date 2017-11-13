exports.DbHelper = class {

    constructor(_db, _client){

        this.db = _db
        this.client = _client
        this.result = undefined
    }

    begin() {
        this.client.query("begin;")
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
                        rollback()
                        reject()
                    })
        })
    }

    commit() {
        this.client.query("commit;")
        this.db.disconnect(this.client)
    }

    rollback() {
        this.client.query("rollback;")
        this.db.disconnect(this.client)
    }

}