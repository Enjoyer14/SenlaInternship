EXPLAIN
SELECT * FROM event_category ec
JOIN event e ON ec.event_id = e.id;