-- !Ups

-- This scripts creates the initial tables needed for the basic functionality of the service.
-- Any changes to the table structure, as well as inserting initial or sample values, are in subsequent scripts.

-- tables for users
CREATE TABLE IF NOT EXISTS account_types(
    account_type_id INTEGER         PRIMARY KEY NOT NULL,
    account_type    VARCHAR(50)     NOT NULL
);

CREATE TABLE IF NOT EXISTS users(
    user_id         INTEGER         PRIMARY KEY NOT NULL,
    username        VARCHAR(50)     UNIQUE NOT NULL,
    email           VARCHAR(255)    UNIQUE,
    password_hash   VARCHAR(255)    NOT NULL,
    date_created    TIMESTAMP,
    account_type_id INTEGER         REFERENCES account_types(account_type_id)
);

-- tables for offers
CREATE TABLE IF NOT EXISTS offers(
    offer_id        INTEGER         PRIMARY KEY NOT NULL,
    name            VARCHAR(255)    NOT NULL,
    description     TEXT,
    single_price    DECIMAL(8, 2)   NOT NULL,
    is_first_free   BOOLEAN         DEFAULT FALSE,
    dates           TIMESTAMP [],
    provider_id     INTEGER         REFERENCES users(user_id)
);

CREATE TABLE IF NOT EXISTS categories(
    category_id     INTEGER         PRIMARY KEY NOT NULL,
    name            VARCHAR(255)    NOT NULL
);

CREATE TABLE IF NOT EXISTS offer_category(
    offer_id        INTEGER         REFERENCES offers(offer_id),
    category_id     INTEGER         REFERENCES categories(category_id)
);

CREATE TABLE IF NOT EXISTS addresses(
    address_id      INTEGER         PRIMARY KEY NOT NULL,
    address_line_1  VARCHAR(255)    NOT NULL,
    address_line_2  VARCHAR(255),
    address_line_3  VARCHAR(255),
    city            VARCHAR(255),
    country         VARCHAR(255),
    postal_code     VARCHAR(10)
);

CREATE TABLE IF NOT EXISTS locations(
    location_id     INTEGER         PRIMARY KEY NOT NULL,
    name            VARCHAR(255)    NOT NULL,
    address_id      INTEGER         REFERENCES addresses(address_id)
);

CREATE TABLE IF NOT EXISTS offer_location(
    offer_id        INTEGER         REFERENCES offers(offer_id),
    location_id     INTEGER         REFERENCES locations(location_id)
);

CREATE TABLE IF NOT EXISTS opinions(
    opinion_id      INTEGER         PRIMARY KEY NOT NULL,
    rating          INTEGER         NOT NULL CHECK (rating BETWEEN 0 AND 10),
    contents        TEXT,
    user_id         INTEGER         REFERENCES users(user_id),
    offer_id        INTEGER         REFERENCES offers(offer_id)
);

-- tables for reservations
CREATE TABLE IF NOT EXISTS reservations(
    reservation_id  INTEGER         PRIMARY KEY NOT NULL,
    event_date      TIMESTAMP       NOT NULL,
    user_id         INTEGER         REFERENCES users(user_id),
    offer_id        INTEGER         REFERENCES offers(offer_id)
);

CREATE TABLE IF NOT EXISTS invoices(
    invoice_id      INTEGER         PRIMARY KEY NOT NULL,
    invoice_number  VARCHAR(50)     NOT NULL,
    invoice_date    TIMESTAMP       NOT NULL,
    tax_rate        DECIMAL(5, 2)   DEFAULT 000.00,
    before_tax      DECIMAL(8, 2)   NOT NULL,
    after_tax       DECIMAL(8, 2)   NOT NULL,
    reservation_id  INTEGER         REFERENCES reservations(reservation_id)
);

-- Create sequences for appropriate tables
-- IDs from 1 to 99 are reserved for sample values and for testing purposes
CREATE SEQUENCE IF NOT EXISTS seq_users START 100;
CREATE SEQUENCE IF NOT EXISTS seq_offers START 100;
CREATE SEQUENCE IF NOT EXISTS seq_categories START 100;
CREATE SEQUENCE IF NOT EXISTS seq_addresses START 100;
CREATE SEQUENCE IF NOT EXISTS seq_locations START 100;
CREATE SEQUENCE IF NOT EXISTS seq_opinions START 100;
CREATE SEQUENCE IF NOT EXISTS seq_reservations START 100;
CREATE SEQUENCE IF NOT EXISTS seq_invoices START 100;

-- !Downs
COMMENT ON TABLE play_evolutions IS 'Down 1';

DROP TABLE offer_category;
DROP TABLE offer_location;
DROP TABLE locations CASCADE;
DROP TABLE opinions;
DROP TABLE invoices;
DROP TABLE reservations;
DROP TABLE users CASCADE;
DROP TABLE offers CASCADE;
DROP TABLE categories CASCADE;
DROP TABLE addresses CASCADE;
DROP TABLE account_types CASCADE;
