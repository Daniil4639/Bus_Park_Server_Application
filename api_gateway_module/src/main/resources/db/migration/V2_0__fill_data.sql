INSERT INTO bus_users
VALUES ('admin_1', crypt('admin_password_1', gen_salt('bf')), ARRAY['USER', 'ADMIN']),
       ('admin_2', crypt('admin_password_2', gen_salt('bf')), ARRAY['USER', 'ADMIN']),
       ('user_1', crypt('user_password_1', gen_salt('bf')), ARRAY['USER']),
       ('user_2', crypt('user_password_2', gen_salt('bf')), ARRAY['USER']);