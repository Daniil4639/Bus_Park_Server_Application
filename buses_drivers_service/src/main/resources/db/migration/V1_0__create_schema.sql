/* ------------------------ Таблицы водителей ----------------------- */

CREATE TABLE IF NOT EXISTS drivers (
    id UUID DEFAULT gen_random_uuid(),
    full_name VARCHAR(50) NOT NULL,
    age INTEGER NOT NULL,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL,
    license_number VARCHAR(30) UNIQUE NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    is_deleted BOOLEAN NOT NULL,

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

    PRIMARY KEY (id),
    UNIQUE (name, address)
);

COMMENT ON TABLE departments IS 'Таблица с данными об автобусных отделениях';
COMMENT ON COLUMN departments.id IS 'Идентификатор отделения';
COMMENT ON COLUMN departments.name IS 'Название отделения';
COMMENT ON COLUMN departments.address IS 'Адрес отделения';


/* ------------------------ Таблицы автобусов ----------------------- */

CREATE TABLE IF NOT EXISTS buses (
    id UUID DEFAULT gen_random_uuid(),
    number VARCHAR(20) UNIQUE NOT NULL,
    path_id UUID,
    department_id UUID NOT NULL,
    seats_number INT NOT NULL,
    type VARCHAR NOT NULL,
    status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
    is_deleted BOOLEAN NOT NULL,

    PRIMARY KEY (id),
    FOREIGN KEY (department_id) REFERENCES departments (id) ON DELETE RESTRICT ON UPDATE CASCADE
);

COMMENT ON TABLE buses IS 'Таблица с данными об автобусах';
COMMENT ON COLUMN buses.id IS 'Идентификатор автобуса';
COMMENT ON COLUMN buses.path_id IS 'Идентификатор маршрута автобуса';
COMMENT ON COLUMN buses.department_id IS 'Идентификатор отделения автобуса';
COMMENT ON COLUMN buses.seats_number IS 'Количество посадочных мест в автобусе';
COMMENT ON COLUMN buses.type IS 'Тип питания автобуса';
COMMENT ON COLUMN buses.status IS 'Текущий статус автобуса';