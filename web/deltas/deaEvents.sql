CREATE TABLE deaEvent (
	id SERIAL PRIMARY KEY,
    idDea INT NOT NULL, 
    eventType INT NOT NULL,
    deaProtectedZoneName VARCHAR(200) NOT NULL,
    deaLocation VARCHAR(200) NOT NULL,
    eventTime TIMESTAMP NOT NULL,
    username VARCHAR(50) NOT NULL
);

