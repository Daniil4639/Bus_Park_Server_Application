/* ------------------------ Таблицы водителей ----------------------- */

CREATE TABLE IF NOT EXISTS drivers (
    id UUID DEFAULT gen_random_uuid(),
    full_name VARCHAR(50) NOT NULL,
    age INTEGER NOT NULL,
    phone VARCHAR(20) UNIQUE NOT NULL,
    email VARCHAR(100),
    license_number VARCHAR(30) UNIQUE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',

    PRIMARY KEY (id),
    CHECK (age >= 18)
);

COMMENT ON TABLE drivers IS 'Таблица с данными о водителях';
COMMENT ON COLUMN drivers.id IS 'Идентификатор водителя';
COMMENT ON COLUMN drivers.full_name IS 'ФИО водителя';
COMMENT ON COLUMN drivers.age IS 'Возраст водителя';
COMMENT ON COLUMN drivers.phone IS 'Телефон водителя';
COMMENT ON COLUMN drivers.email IS 'Почтовый адрес водителя';
COMMENT ON COLUMN drivers.license_number IS 'Номер лицензии водителя водителя';
COMMENT ON COLUMN drivers.status IS 'Текущий статус водителя';


/* ------------------------ Таблицы отделений ----------------------- */

CREATE TABLE IF NOT EXISTS departments (
    id UUID DEFAULT gen_random_uuid(),
    name VARCHAR(30) NOT NULL,
    address VARCHAR(60) NOT NULL,
    is_deleted BOOLEAN NOT NULL,

    PRIMARY KEY (id)
);

COMMENT ON TABLE departments IS 'Таблица с данными об автобусных отделениях';
COMMENT ON COLUMN departments.id IS 'Идентификатор отделения';
COMMENT ON COLUMN departments.name IS 'Название отделения';
COMMENT ON COLUMN departments.address IS 'Адрес отделения';


/* ------------------------ Таблицы остановок ----------------------- */

CREATE TABLE IF NOT EXISTS stations (
    id SERIAL NOT NULL PRIMARY KEY,
    name VARCHAR(30) NOT NULL,
    district VARCHAR(30) NOT NULL,
    is_deleted BOOLEAN NOT NULL
);

COMMENT ON TABLE stations IS 'Таблица с данными об остановках города';
COMMENT ON COLUMN stations.id IS 'Идентификатор остановки';
COMMENT ON COLUMN stations.name IS 'Название остановки';
COMMENT ON COLUMN stations.district IS 'Район, в котором расположена остановка';


/* ------------------------ Таблицы маршрутов ----------------------- */

CREATE TABLE IF NOT EXISTS paths (
    number VARCHAR(5) NOT NULL PRIMARY KEY,
    begin_station INT NOT NULL,
    end_station INT NOT NULL,
    duration INT NOT NULL,
    is_deleted BOOLEAN NOT NULL,

    FOREIGN KEY (begin_station) REFERENCES stations (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (end_station) REFERENCES stations (id) ON DELETE RESTRICT ON UPDATE CASCADE
);

COMMENT ON TABLE paths IS 'Таблица с данными о маршрутах следования автобусов';
COMMENT ON COLUMN paths.number IS 'Номер маршрута';
COMMENT ON COLUMN paths.begin_station IS 'Стартовая точка маршрута';
COMMENT ON COLUMN paths.end_station IS 'Конечная точка маршрута';
COMMENT ON COLUMN paths.duration IS 'Продолжительность маршрута';


/* ------------------------ Таблицы автобусов ----------------------- */

CREATE TABLE IF NOT EXISTS buses (
    number VARCHAR(20) NOT NULL PRIMARY KEY,
    path_number VARCHAR(5) NOT NULL,
    department_id INT NOT NULL,
    seats_number INT NOT NULL,
    type VARCHAR NOT NULL,
    is_active BOOLEAN NOT NULL,
    is_deleted BOOLEAN NOT NULL,

    CHECK (number ~ '^[0-9]*$'),

    FOREIGN KEY (path_number) REFERENCES paths (number) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (department_id) REFERENCES departments (id) ON DELETE RESTRICT ON UPDATE CASCADE
);

COMMENT ON TABLE buses IS 'Таблица с данными об автобусах';
COMMENT ON COLUMN buses.number IS 'Номер автобуса';
COMMENT ON COLUMN buses.path_number IS 'Номер маршрута автобуса';
COMMENT ON COLUMN buses.department_id IS 'Идентификатор отделения автобуса';
COMMENT ON COLUMN buses.seats_number IS 'Количество посадочных мест в автобусе';
COMMENT ON COLUMN buses.type IS 'Тип питания автобуса';


/* ------------------------ Таблицы соответствия маршрутов и остановок ----------------------- */

CREATE TABLE IF NOT EXISTS paths_stations (
    path_number VARCHAR(5) NOT NULL,
    station_id INT NOT NULL,
    time_spent_from_start INT NOT NULL,
    PRIMARY KEY (path_number, station_id),
    FOREIGN KEY (path_number) REFERENCES paths (number) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (station_id) REFERENCES stations (id) ON DELETE RESTRICT ON UPDATE CASCADE
);

COMMENT ON TABLE paths_stations IS 'Таблица с данными о соответствии маршрута и остановки';
COMMENT ON COLUMN paths_stations.path_number IS 'Номер маршрута';
COMMENT ON COLUMN paths_stations.station_id IS 'Идентификатор остановки';
COMMENT ON COLUMN paths_stations.time_spent_from_start IS 'Время, необходимое, чтобы доехать от стартовой точки маршрута до данной остановки';

/* ------------------------ Таблицы соответствия маршрутов и остановок ----------------------- */

CREATE TABLE IF NOT EXISTS buses_drivers (
    bus_number VARCHAR(20) NOT NULL,
    driver_id INT NOT NULL,
    PRIMARY KEY (bus_number, driver_id),
    FOREIGN KEY (bus_number) REFERENCES buses (number) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (driver_id) REFERENCES drivers (id) ON DELETE RESTRICT ON UPDATE CASCADE
);

COMMENT ON TABLE buses_drivers IS 'Таблица с данными о соответствии автобуса и водителей';
COMMENT ON COLUMN buses_drivers.bus_number IS 'Номер автобуса';
COMMENT ON COLUMN buses_drivers.driver_id IS 'Идентификатор водителя';

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