-- !Ups

-- This script inserts some fake values for presentation and testing purposes to users, sports and offers tables.
INSERT INTO sports(sport_id, name) VALUES
    (2, 'MMA'),
    (3, 'Swimming'),
    (4, 'Boxing'),
    (5, 'Climbing'),
    (6, 'Gym'),
    (7, 'Tennis'),
    (8, 'Ping-pong')
    ON CONFLICT(sport_id) DO UPDATE SET
        name = excluded.name;

INSERT INTO users(user_id, username, email, password_hash, date_created, account_type_id) VALUES
    (5, 'Personal Trainer', 'pt@gymate.com', 'test', '2020-06-01 00:00:00', 3),
    (6, 'Krakow Gym', 'krakowgym@gymate.com', 'test', '2020-06-01 00:00:00', 4),
    (7, 'Multi Sport Club', 'multisportclub@gymate.com', 'test', '2020-06-01 00:00:00', 5),
    (8, 'Politechnika Krakowska Silownia', 'pksilownia@gymate.com', 'test', '2020-06-01 00:00:00', 4),
    (9, 'Politechnika Krakowska Basen', 'pkbasen@gymate.com', 'test', '2020-06-01 00:00:00', 5),
    (10, 'Michael Phelps', 'mphelps@gymate.com', 'test', '2020-06-01 00:00:00', 3),
    (11, 'Mike Tyson', 'mtyson@gymate.com', 'test', '2020-06-01 00:00:00', 3)
    ON CONFLICT(user_id) DO UPDATE SET
        username = excluded.username,
        email = excluded.email,
        password_hash = excluded.password_hash,
        date_created = excluded.date_created,
        account_type_id = excluded.account_type_id;

INSERT INTO offers(offer_id, name, description, single_price, is_first_free, dates, provider_id,
    latitude, longitude, sport_id, spots) VALUES
    (2, 'MMA Morning Training', 'Train Mixed Martial Arts in Krakow', 50.00, FALSE,
    ARRAY [to_timestamp('2020-06-23 08:00:00', 'YYYY-MM-DD HH24:MI:SS'),
           to_timestamp('2020-06-24 08:00:00', 'YYYY-MM-DD HH24:MI:SS'),
           to_timestamp('2020-06-25 08:00:00', 'YYYY-MM-DD HH24:MI:SS'),
           to_timestamp('2020-06-26 08:00:00', 'YYYY-MM-DD HH24:MI:SS')],
        7, 50.07369, 20.02631, 2, 10),
    (3, 'Swimming with Michael Phelps', 'Learn swimming with the most successful Olympian of all time', 10000.00, FALSE,
        ARRAY [to_timestamp('2020-07-01 15:00:00', 'YYYY-MM-DD HH24:MI:SS')],
        10, 50.0544, 19.93057, 3, 100),
    (4, 'Boxing with Mike Tyson', 'Learn boxing with the man who won against Andrzej Golota', 1000.00, FALSE,
        ARRAY [to_timestamp('2020-07-02 16:00:00', 'YYYY-MM-DD HH24:MI:SS')],
        11, 50.07369, 20.02631, 4, 1),
    (5, 'Climbing lessons', 'Climbing lessons', 10.00, TRUE,
        ARRAY [to_timestamp('2020-06-25 07:00:00', 'YYYY-MM-DD HH24:MI:SS'),
               to_timestamp('2020-06-25 15:00:00', 'YYYY-MM-DD HH24:MI:SS'),
               to_timestamp('2020-06-26 07:00:00', 'YYYY-MM-DD HH24:MI:SS'),
               to_timestamp('2020-06-26 15:00:00', 'YYYY-MM-DD HH24:MI:SS'),
               to_timestamp('2020-06-27 07:00:00', 'YYYY-MM-DD HH24:MI:SS')],
        7, 50.05316, 19.93381, 5, 15),
    (6, 'Trening na silowni PK', 'Trening na silowni PK', 1.00, TRUE,
        ARRAY [to_timestamp('2020-06-23 10:00:00', 'YYYY-MM-DD HH24:MI:SS'),
               to_timestamp('2020-06-24 10:00:00', 'YYYY-MM-DD HH24:MI:SS'),
               to_timestamp('2020-06-25 10:00:00', 'YYYY-MM-DD HH24:MI:SS'),
               to_timestamp('2020-06-26 10:00:00', 'YYYY-MM-DD HH24:MI:SS'),
               to_timestamp('2020-06-27 10:00:00', 'YYYY-MM-DD HH24:MI:SS')],
        8, 50.07529, 19.94088, 6, 20),
    (7, 'Plywanie na basenie PK', 'Plywanie na basenie PK', 1.00, TRUE,
        ARRAY [to_timestamp('2020-06-23 10:00:00', 'YYYY-MM-DD HH24:MI:SS'),
               to_timestamp('2020-06-24 10:00:00', 'YYYY-MM-DD HH24:MI:SS'),
               to_timestamp('2020-06-25 10:00:00', 'YYYY-MM-DD HH24:MI:SS'),
               to_timestamp('2020-06-26 10:00:00', 'YYYY-MM-DD HH24:MI:SS'),
               to_timestamp('2020-06-27 10:00:00', 'YYYY-MM-DD HH24:MI:SS')],
        9, 50.07619, 19.99556, 3, 20),
    (8, 'Tenis PK', 'Tenis PK', 15.00, TRUE,
        ARRAY [to_timestamp('2020-06-23 10:00:00', 'YYYY-MM-DD HH24:MI:SS'),
               to_timestamp('2020-06-24 10:00:00', 'YYYY-MM-DD HH24:MI:SS'),
               to_timestamp('2020-06-25 10:00:00', 'YYYY-MM-DD HH24:MI:SS'),
               to_timestamp('2020-06-26 10:00:00', 'YYYY-MM-DD HH24:MI:SS'),
               to_timestamp('2020-06-27 10:00:00', 'YYYY-MM-DD HH24:MI:SS')],
        8, 50.07619, 19.99556, 7, 4),
    (9, 'Pingpong PK', 'Pingpong PK', 15.00, TRUE,
        ARRAY [to_timestamp('2020-06-23 10:00:00', 'YYYY-MM-DD HH24:MI:SS'),
               to_timestamp('2020-06-24 10:00:00', 'YYYY-MM-DD HH24:MI:SS'),
               to_timestamp('2020-06-25 10:00:00', 'YYYY-MM-DD HH24:MI:SS'),
               to_timestamp('2020-06-26 10:00:00', 'YYYY-MM-DD HH24:MI:SS'),
               to_timestamp('2020-06-27 10:00:00', 'YYYY-MM-DD HH24:MI:SS')],
        8, 50.07619, 19.99556, 8, 4),
    (10, 'Krakow Gym', 'Krakow Gym', 20.00, TRUE,
        ARRAY [to_timestamp('2020-06-23 10:00:00', 'YYYY-MM-DD HH24:MI:SS'),
               to_timestamp('2020-06-24 10:00:00', 'YYYY-MM-DD HH24:MI:SS'),
               to_timestamp('2020-06-25 10:00:00', 'YYYY-MM-DD HH24:MI:SS'),
               to_timestamp('2020-06-26 10:00:00', 'YYYY-MM-DD HH24:MI:SS'),
               to_timestamp('2020-06-27 10:00:00', 'YYYY-MM-DD HH24:MI:SS')],
        6, 50.061389, 19.938333, 6, 16)
    ON CONFLICT(offer_id) DO UPDATE SET
        name = excluded.name,
        description = excluded.description,
        single_price = excluded.single_price,
        is_first_free = excluded.is_first_free,
        dates = excluded.dates,
        provider_id = excluded.provider_id,
        latitude = excluded.latitude,
        longitude = excluded.longitude,
        sport_id = excluded.sport_id,
        spots = excluded.spots;

-- !Downs
COMMENT ON TABLE play_evolutions IS 'Down 5';

DELETE FROM users WHERE user_id >= 5 AND user_id <= 11;
DELETE FROM sports WHERE sport_id >= 2 AND sport_id <= 8;
DELETE FROM offers WHERE offer_id >= 2 AND offer_id <= 10;
