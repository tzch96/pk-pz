-- !Ups

CREATE TABLE IF NOT EXISTS sports(
    sport_id    INTEGER         PRIMARY KEY NOT NULL,
    name        VARCHAR(255)    NOT NULL
);

CREATE SEQUENCE IF NOT EXISTS seq_sports START 100;

-- lat: N..S 90..-90
-- long: E..W 180..-180
ALTER TABLE offers
ADD COLUMN  latitude    DECIMAL(10, 8)  CHECK (latitude > -90 AND latitude < 90),
ADD COLUMN  longitude   DECIMAL(11, 8)  CHECK (longitude > -180 AND longitude < 180),
ADD COLUMN  sport_id    INTEGER         REFERENCES sports(sport_id);

INSERT INTO sports(sport_id, name) VALUES
    (1, 'test')
    ON CONFLICT(sport_id) DO UPDATE SET
        name = excluded.name;

UPDATE offers SET latitude = 50.06138, longitude = 19.9383, sport_id = 1 WHERE offer_id = 1;

-- !Downs
COMMENT ON TABLE play_evolutions IS 'Down 3';

ALTER TABLE offers
DROP COLUMN latitude,
DROP COLUMN longitude,
DROP COLUMN sport_id;

DROP TABLE sports CASCADE;
DROP SEQUENCE seq_sports;