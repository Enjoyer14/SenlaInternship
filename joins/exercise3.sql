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


-- DML
INSERT INTO cd.members(surname, firstname, address, zipcode, telephone, recommendedby, joindate)
VALUES 
	('Sarwin', 'Ramnaresh', '123 Main St', 12345, '555-1234', NULL, '2012-09-01 08:44:42'),
	('David', 'Farrell', '456 Oak Ave', 67890, '555-5678', 1, '2023-01-02 00:00:00'),
	('Williams', 'Bob', '789 Pine Rd', 11223, '555-9012', 1, '2023-01-03 00:00:00');


-- SELECT 
SELECT firstname, surname FROM cd.members
WHERE memid IN (
    SELECT DISTINCT recommendedby FROM cd.members
    WHERE recommendedby IS NOT NULL
)
ORDER BY surname, firstname;