CREATE TABLE venues (
    id               BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    name             VARCHAR(255) NOT NULL,
    address          VARCHAR(255) NOT NULL,
    seating_capacity INTEGER      NOT NULL
);

CREATE TABLE seats (
    id          BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    venue_id    BIGINT       NOT NULL REFERENCES venues (id),
    section     VARCHAR(255) NOT NULL,
    seat_number VARCHAR(255) NOT NULL,
    tier        VARCHAR(50)  NOT NULL
);

CREATE TABLE events (
    id         BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    venue_id   BIGINT       NOT NULL REFERENCES venues (id),
    name       VARCHAR(255) NOT NULL,
    performer  VARCHAR(255) NOT NULL,
    category   VARCHAR(255) NOT NULL,
    start_time TIMESTAMPTZ  NOT NULL
);

CREATE TABLE event_seats (
    id       BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    event_id BIGINT        NOT NULL REFERENCES events (id),
    seat_id  BIGINT        NOT NULL REFERENCES seats (id),
    price    NUMERIC(10,2) NOT NULL,
    status   VARCHAR(50)   NOT NULL,
    CONSTRAINT uq_event_seats_event_seat UNIQUE (event_id, seat_id)
);
