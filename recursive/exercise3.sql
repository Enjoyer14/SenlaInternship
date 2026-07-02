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
WITH RECURSIVE recommenders(recommender, member) AS (
    SELECT recommendedby, memid FROM cd.members
    UNION ALL
    SELECT m.recommendedby, r.member FROM recommenders r
    JOIN cd.members m ON m.memid = r.recommender
)
SELECT r.member, r.recommender, m.firstname, m.surname FROM recommenders r
JOIN cd.members m ON m.memid = r.recommender
WHERE r.member = 12 or r.member = 22
ORDER BY r.member;