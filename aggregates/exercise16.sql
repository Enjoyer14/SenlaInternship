-- DDL
CREATE SCHEMA cd;

CREATE TABLE cd.members (
    memid SERIAL PRIMARY KEY,
    surname VARCHAR(100),
    firstname VARCHAR(100),
    address VARCHAR(255),
    zipcode INTEGER,
    telephone VARCHAR(20),
    recommendedby INTEGER,
    joindate TIMESTAMP
);

CREATE TABLE cd.facilities (
    facid SERIAL PRIMARY KEY,
    name VARCHAR(100),
    membercost NUMERIC,
    guestcost NUMERIC,
	initialoutlay NUMERIC,
	monthlymaintenance NUMERIC
);

CREATE TABLE cd.bookings (
    facid INTEGER REFERENCES cd.facilities(facid),
    memid INTEGER REFERENCES cd.members(memid),
    starttime TIMESTAMP,
    slots INTEGER
);


-- DML
INSERT INTO cd.members(surname, firstname, address, zipcode, telephone, recommendedby, joindate)
VALUES 
	('Sarwin', 'Ramnaresh', '123 Main St', 12345, '555-1234', NULL, '2012-09-01 08:44:42'),
	('David', 'Farrell', '456 Oak Ave', 67890, '555-5678', 1, '2023-01-02 00:00:00'),
	('Williams', 'Bob', '789 Pine Rd', 11223, '555-9012', 1, '2023-01-03 00:00:00');


INSERT INTO cd.facilities(name, membercost, guestcost, initialoutlay, monthlymaintenance)
VALUES 
	('Tennis Court 1', 5, 25, 10000, 200),
	('Tennis Court 2', 5, 25, 8000, 200),
	('Badminton Court', 0, 15.5, 4000, 50),
    ('Table Tennis', 0, 5, 320, 10),
    ('Massage Room 1', 35, 80, 4000, 3000),
    ('Massage Room 2', 35, 80, 4000, 3000),
    ('Squash Court', 3.5, 17.5, 5000, 80),
    ('Snooker Table', 0, 5, 450, 15),
    ('Pool Table', 0, 5, 400, 15);
    
INSERT INTO cd.bookings(facid, memid, starttime, slots)
VALUES 
    (1, 1, '2023-01-01 10:00:00', 2),
    (2, 2, '2023-01-02 14:00:00', 1),
    (3, 3, '2023-01-03 16:00:00', 3);

-- SELECT 
SELECT ROW_NUMBER() OVER(ORDER BY memid) AS row_number, firstname, surname FROM cd.members
ORDER BY joindate;