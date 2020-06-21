-- !Ups

-- Predefined account types
INSERT INTO account_types(account_type_id, account_type) VALUES
    (1, 'administrator'),
    (2, 'user'),
    (3, 'trainer'),
    (4, 'gym'),
    (5, 'sportclub')
    ON CONFLICT (account_type_id) DO UPDATE SET
        account_type = excluded.account_type;

-- Mock data for testing purposes
INSERT INTO users(user_id, username, email, password_hash, date_created, account_type_id) VALUES
    (1, 'test', 'test@test.com', 'test', '2000-01-01 00:00:00', 1),
    (2, 'test2', 'test2@test2.com', 'test2', '2000-01-01 00:00:00', 2)
    ON CONFLICT (user_id) DO UPDATE SET
        username = excluded.username,
        email = excluded.email,
        password_hash = excluded.password_hash,
        date_created = excluded.date_created,
        account_type_id = excluded.account_type_id;

INSERT INTO users(user_id, username, password_hash, date_created, account_type_id) VALUES
    (3, 'testNoEmail', 'test3', '2000-01-01 00:00:00', 2)
    ON CONFLICT (user_id) DO UPDATE SET
        username = excluded.username,
        password_hash = excluded.password_hash,
        date_created = excluded.date_created,
        account_type_id = excluded.account_type_id;

INSERT INTO users(user_id, username, password_hash, date_created, account_type_id) VALUES
    (4, 'trainer', 'trainer', '2000-01-01 00:00:00', 3)
    ON CONFLICT (user_id) DO UPDATE SET
        username = excluded.username,
        password_hash = excluded.password_hash,
        date_created = excluded.date_created,
        account_type_id = excluded.account_type_id;

INSERT INTO offers(offer_id, name, description, single_price, is_first_free, dates, provider_id) VALUES
    (1, 'test', 'test', 10.00, FALSE, ARRAY [to_timestamp('2020-01-01 00:00:00', 'YYYY-MM-DD HH24:MI:SS'), to_timestamp('2020-01-01 15:00:00', 'YYYY-MM-DD HH24:MI:SS')], 4)
    ON CONFLICT (offer_id) DO UPDATE SET
        name = excluded.name,
        description = excluded.description,
        single_price = excluded.single_price,
        is_first_free = excluded.is_first_free,
        dates = excluded.dates,
        provider_id = excluded.provider_id;

-- !Downs
COMMENT ON TABLE play_evolutions IS 'Down 2';

DELETE FROM users WHERE user_id >= 1 OR user_id <= 4;
DELETE FROM account_types WHERE account_type_id >= 1 AND account_type_id <= 5;
DELETE FROM offers WHERE offer_id = 1;
