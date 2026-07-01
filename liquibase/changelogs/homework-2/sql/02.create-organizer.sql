CREATE TABLE organizer
(
	organizer_id BIGSERIAL PRIMARY KEY,
	user_id BIGINT UNIQUE NOT NULL,
	company_name VARCHAR(255) UNIQUE NOT NULL,
	description TEXT,
	phone VARCHAR(30),
	FOREIGN KEY (user_id) REFERENCES users(user_id)
);
