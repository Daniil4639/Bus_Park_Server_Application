CREATE EXTENSION IF NOT EXISTS pgcrypto;

INSERT INTO bus_users
VALUES ('admin_1', crypt('admin_password_1', gen_salt('bf')), ARRAY['WORKER', 'ADMIN']),
       ('admin_2', crypt('admin_password_2', gen_salt('bf')), ARRAY['WORKER', 'ADMIN']),
       ('user_1', crypt('user_password_1', gen_salt('bf')), ARRAY['WORKER']),
       ('user_2', crypt('user_password_2', gen_salt('bf')), ARRAY['WORKER']);