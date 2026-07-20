CREATE TABLE event_organizer
(
	event_id BIGINT NOT NULL,
    organizer_id BIGINT NOT NULL, -- будет ссылатся на таблицу из другой микрухи
    PRIMARY KEY (event_id, organizer_id),
    FOREIGN KEY (event_id) REFERENCES event(id)
);