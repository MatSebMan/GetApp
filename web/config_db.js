const { Pool, Client } = require('pg')

exports.db_uppercase = function(str) {
    if(str)
        return str.toUpperCase();
    else
        return str;
}

exports.db = {
    databases: {
        getapp: {
            host: '127.0.0.1',
            database: 'getapp',
            user: 'admin',
            password: 'admin',
            port: 5432,
        }
    },
    connect: function(dbName) {
        const client = new Client(this.databases[dbName]);
        client.connect();
        return client;
    },
    connect: function(dbName, success, error) {
        const client = new Client(this.databases[dbName]);
        client.connect();
        client.on('error', function (err) {
            error(err);
        });
        success(client);
    },
    disconnect: function(client) {
        client.end();
    },
    query: function(query, client, res, err) {
        client.query(query)
        .then(res)
        .catch(err)
    }
}