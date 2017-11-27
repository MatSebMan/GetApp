CREATE TABLE scheduleAvailability (
	id SERIAL PRIMARY KEY,
	idDea INT NOT NULL REFERENCES dea (id),
    	weekday INT NOT NULL CHECK (weekday >= 0) CHECK (weekday < 8),
	weekday INT NOT NULL,
	startTime TIME NOT NULL,
	endTime TIME NOT NULL
);