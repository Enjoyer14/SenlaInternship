CREATE TABLE event
(
	event_id BIGSERIAL PRIMARY KEY,
    venue_id BIGINT NOT NULL,
    organizer_id BIGINT NOT NULL, --не делаем реф, будет в другом микросервисе
	title VARCHAR(255) NOT NULL,
	description TEXT,
	event_date TIMESTAMP WITH TIME ZONE,
	capacity INTEGER NOT NULL CHECK (capacity > 0),
	booked_seats INTEGER NOT NULL CHECK(booked_seats >= 0) DEFAULT 0,
	price NUMERIC(10,2) NOT NULL CHECK(price >= 0),
	status VARCHAR(50) NOT NULL CHECK(status IN ('PUBLISHED', 'CANCELLED', 'FINISHED')),
	created_at TIMESTAMP WITH TIME ZONE NOT NULL,
	updated_at TIMESTAMP WITH TIME ZONE,
    FOREIGN KEY (venue_id) REFERENCES venue(venue_id)
);