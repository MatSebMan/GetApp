const { Pool, Client } = require('pg')

exports.db_uppercase = function(str) {
    if(str)
        return str.toUpperCase();
    else
        return str;
}

exports.db = {
    databases: {
        
    },
    connect: function(dbName) {
        
    },
    disconnect: function(client) {
        
    },
    query: function(query, client, res, err) {
        
    }
}