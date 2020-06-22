-- !Ups

-- Number of available spots for participants
ALTER TABLE offers
ADD COLUMN  spots   INTEGER;

UPDATE offers SET spots = 10 WHERE offer_id = 1;

-- !Downs
COMMENT ON TABLE play_evolutions IS 'Down 4';

ALTER TABLE offers
DROP COLUMN spots;