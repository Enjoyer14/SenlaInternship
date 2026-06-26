-- DDL
CREATE SCHEMA cd;

CREATE TABLE cd.facilities (
    facid SERIAL PRIMARY KEY,
    name VARCHAR(100),
    membercost NUMERIC,
    guestcost NUMERIC,
	initialoutlay NUMERIC,
	monthlymaintenance NUMERIC
);

-- DML
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

--SELECT
SELECT name, membercost FROM cd.facilities;
