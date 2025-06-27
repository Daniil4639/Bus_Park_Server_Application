/* ------------------------ Таблицы остановок ----------------------- */

CREATE TABLE IF NOT EXISTS stations (
    id UUID DEFAULT gen_random_uuid(),
    name VARCHAR(100) NOT NULL,
    address VARCHAR(100) NOT NULL,
    is_deleted BOOLEAN NOT NULL,

    PRIMARY KEY (id),
    UNIQUE (name, address)
);

COMMENT ON TABLE stations IS 'Таблица с данными об остановках города';
COMMENT ON COLUMN stations.id IS 'Идентификатор остановки';
COMMENT ON COLUMN stations.name IS 'Название остановки';
COMMENT ON COLUMN stations.address IS 'Адрес остановки';


/* ------------------------ Таблицы маршрутов ----------------------- */

CREATE TABLE IF NOT EXISTS paths (
    id UUID DEFAULT gen_random_uuid(),
    number VARCHAR(20) NOT NULL,
    city VARCHAR(30) NOT NULL,
    distance DECIMAL(7,2) NOT NULL,
    is_deleted BOOLEAN NOT NULL,

    PRIMARY KEY (id),
    UNIQUE (number, city)
);

COMMENT ON TABLE paths IS 'Таблица с данными о маршрутах следования автобусов';
COMMENT ON COLUMN paths.id IS 'Идентификатор маршрута';
COMMENT ON COLUMN paths.number IS 'Номер маршрута';
COMMENT ON COLUMN paths.distance IS 'Протяженность маршрута (км)';


/* ------------------------ Таблицы соответствия маршрутов и остановок ----------------------- */

CREATE TABLE IF NOT EXISTS paths_stations (
    id UUID DEFAULT gen_random_uuid(),
    path_id UUID NOT NULL,
    station_id UUID NOT NULL,
    time_spent_from_start INT NOT NULL,
    is_deleted BOOLEAN NOT NULL,

    PRIMARY KEY (id),
    UNIQUE (path_id, station_id),
    FOREIGN KEY (path_id) REFERENCES paths (id) ON DELETE RESTRICT ON UPDATE CASCADE,
    FOREIGN KEY (station_id) REFERENCES stations (id) ON DELETE RESTRICT ON UPDATE CASCADE
);

COMMENT ON TABLE paths_stations IS 'Таблица с данными о соответствии маршрута и остановки';
COMMENT ON COLUMN paths_stations.path_id IS 'Идентификатор маршрута';
COMMENT ON COLUMN paths_stations.station_id IS 'Идентификатор остановки';
COMMENT ON COLUMN paths_stations.time_spent_from_start IS 'Время, необходимое, чтобы доехать от стартовой точки маршрута до данной остановки';