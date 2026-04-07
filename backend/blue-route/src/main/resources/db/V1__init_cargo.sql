CREATE TABLE goods (
                       id BIGSERIAL PRIMARY KEY,
                       name VARCHAR(100) NOT NULL,
                       base_price DECIMAL(10,2) NOT NULL,
                       weight DECIMAL(10,2) NOT NULL
);

CREATE TABLE port_goods (
                            id BIGSERIAL PRIMARY KEY,
                            port_id BIGINT NOT NULL,
                            good_id BIGINT NOT NULL,
                            buy_price DECIMAL(10,2) NOT NULL,
                            sell_price DECIMAL(10,2) NOT NULL,
                            stock INT NOT NULL,

                            CONSTRAINT fk_port FOREIGN KEY (port_id) REFERENCES ports(id),
                            CONSTRAINT fk_good FOREIGN KEY (good_id) REFERENCES goods(id)
);

CREATE TABLE ship_cargo (
                            id BIGSERIAL PRIMARY KEY,
                            ship_id BIGINT NOT NULL,
                            good_id BIGINT NOT NULL,
                            quantity INT NOT NULL,

                            CONSTRAINT fk_ship FOREIGN KEY (ship_id) REFERENCES ships(id),
                            CONSTRAINT fk_good FOREIGN KEY (good_id) REFERENCES goods(id),

                            UNIQUE (ship_id, good_id)
);

ALTER TABLE ships
    ADD COLUMN cargo_capacity DECIMAL(10,2) NOT NULL DEFAULT 1000;