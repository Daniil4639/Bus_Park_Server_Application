/* ------------------------ Таблицы соответствия маршрутов и остановок ----------------------- */

CREATE TABLE IF NOT EXISTS bus_users (
    username VARCHAR(30) NOT NULL PRIMARY KEY,
    password VARCHAR NOT NULL,
    roles VARCHAR ARRAY
);

COMMENT ON TABLE bus_users IS 'Таблица с данными о пользователях с правами доступа';
COMMENT ON COLUMN bus_users.username IS 'Имена пользователей';
COMMENT ON COLUMN bus_users.password IS 'Пароли пользователей';
COMMENT ON COLUMN bus_users.roles IS 'Роли пользователей';