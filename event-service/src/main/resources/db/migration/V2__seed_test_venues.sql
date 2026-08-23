INSERT INTO venues (name, address, seating_capacity) VALUES
    ('The Grand Arena', '1 Stadium Way, Bengaluru', 100),
    ('Riverside Theatre', '22 Riverside Rd, Mumbai', 50);

INSERT INTO seats (venue_id, section, seat_number, tier)
SELECT v.id, 'Orchestra', 'A' || s.n, 'STANDARD'
FROM venues v
CROSS JOIN generate_series(1, 100) AS s(n)
WHERE v.name = 'The Grand Arena';

INSERT INTO seats (venue_id, section, seat_number, tier)
SELECT v.id, 'Main Floor', 'B' || s.n, 'STANDARD'
FROM venues v
CROSS JOIN generate_series(1, 50) AS s(n)
WHERE v.name = 'Riverside Theatre';
