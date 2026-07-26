CREATE TABLE event_category
(
	event_id BIGINT NOT NULL,
    category_id BIGINT NOT NULL,
    PRIMARY KEY (event_id, category_id),
    FOREIGN KEY (event_id) REFERENCES event(id),
    FOREIGN KEY (category_id) REFERENCES category(id)
);