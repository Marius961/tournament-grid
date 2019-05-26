INSERT INTO user
    (id, active, password, username)
values (1, '1', '$2a$11$SxLjGe1kDnjRapbFYuJzhu9uR89vU4CR4Tdk9VekH2J8EQSM7bDJm', 'admin');

INSERT INTO user_role
    (user_id, roles)
VALUES (1, 'ADMIN'),
       (1, 'USER');

INSERT INTO stage
    (id, code, name, required_teams_count)
VALUES (2, '0', '1/32 фіналу', 64),
       (3, '1', '1/16 фіналу', 32),
       (4, '2', '1/8 фіналу', 16),
       (5, '3', 'Чвертьфінал', 8),
       (6, '4', 'Півфінал', 4),
       (7, '5', 'Фінал', 2);