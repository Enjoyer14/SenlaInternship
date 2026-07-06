EXPLAIN
SELECT * FROM event_organizer eo
JOIN event e ON eo.event_id = e.id;