EXPLAIN
SELECT * FROM event_category ec
JOIN category c ON ec.category_id = c.id;