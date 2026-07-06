EXPLAIN
SELECT * FROM event_organizer eo
JOIN organizer o ON eo.organizer_id = o.id;