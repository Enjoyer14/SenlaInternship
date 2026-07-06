INSERT INTO "user" (email, username, password_hash, role, created_at)
VALUES
('ivan@mail.ru', 'ivan', '123', 'USER', NOW()),
('anna@mail.ru', 'anna', '123', 'USER', NOW()),
('admin@mail.ru', 'admin', '123', 'ORGANIZER', NOW());

INSERT INTO organizer(user_id, company_name, phone)
VALUES
(3, 'Event Group', '+79999999999');

INSERT INTO venue(name, address, city, region, capacity, created_at)
VALUES
('ВТБ Арена', 'ул. Молодежная, 21', 'Москва', 'Московская область', 7000, NOW()),
('IT Hall', 'Невский пр., 15', 'Санкт-Петербург', 'Ленинградская область', 1200, NOW());

INSERT INTO category (name)
VALUES
('Музыка'),
('IT'),
('Конференция');

INSERT INTO event ( venue_id, title, description, event_date, capacity, booked_seats, price, status, created_at)
VALUES
( 1, 'Java Conference', 'Spring Boot', '2026-09-10 12:00:00+03', 7000, 700, 1000.00, 'PUBLISHED', NOW()),
( 2, 'Rock Fest', 'Большой концерт', '2026-08-01 18:00:00+03', 1200, 200, 500.00, 'PUBLISHED', NOW());

INSERT INTO event_category(event_id, category_id)
VALUES
(1,2),
(1,3),
(2,1);

INSERT INTO ticket (ticket_number, user_id, event_id, status, paid_at )
VALUES
('T-000001',1, 1, 'ACTIVE', NOW()),
('T-000002', 2, 1, 'ACTIVE', NOW()),
('T-000003', 1, 2, 'ACTIVE', NOW());