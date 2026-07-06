CREATE TABLE ticket
(
	id BIGSERIAL PRIMARY KEY,
    ticket_number VARCHAR(30) NOT NULL UNIQUE,
    user_id BIGINT NOT NULL, --не делаем реф, будет в другом микросервисе
	event_id BIGINT NOT NULL, --не делаем реф, будет в другом микросервисе
	status VARCHAR(50) NOT NULL CHECK (status IN ('ACTIVE', 'CANCELLED', 'USED')),
	paid_at TIMESTAMP WITH TIME ZONE NOT NULL,
	updated_at TIMESTAMP WITH TIME ZONE
);