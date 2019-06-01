INSERT INTO user
    (id, password, username)
values (1,'$2a$11$SxLjGe1kDnjRapbFYuJzhu9uR89vU4CR4Tdk9VekH2J8EQSM7bDJm', 'admin');

INSERT INTO user_role
    (user_id, roles)
VALUES (1, 'ADMIN'),
       (1, 'USER');

INSERT INTO stage
    (id, name, required_teams_count)
VALUES (2, '1/32 фіналу', 64),
       (3, '1/16 фіналу', 32),
       (4, '1/8 фіналу', 16),
       (5, 'Чвертьфінал', 8),
       (6, 'Півфінал', 4),
       (7, 'Фінал', 2);