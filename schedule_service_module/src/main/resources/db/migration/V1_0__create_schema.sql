/* ------------------------ Таблицы расписания ---------------------- */

CREATE TABLE IF NOT EXISTS working_logs (
    id UUID DEFAULT gen_random_uuid(),
    license_number VARCHAR(30) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    email VARCHAR(100) NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP NOT NULL,

    PRIMARY KEY (id)
);

COMMENT ON TABLE working_logs IS 'Таблица с расписании водителей';
COMMENT ON COLUMN working_logs.id IS 'Идентификатор элемента расписания';
COMMENT ON COLUMN working_logs.license_number IS 'Номер лицензии водителя';
COMMENT ON COLUMN working_logs.phone IS 'Телефон водителя';
COMMENT ON COLUMN working_logs.email IS 'Адрес почты водителя';
COMMENT ON COLUMN working_logs.start_time IS 'Время начала смены';
COMMENT ON COLUMN working_logs.end_time IS 'Время конца смены';