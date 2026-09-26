CREATE TABLE IF NOT EXISTS buyers (
    id        SERIAL PRIMARY KEY,
    full_name VARCHAR(150) NOT NULL,
    email     VARCHAR(150) NOT NULL UNIQUE,
    phone     VARCHAR(30)  NOT NULL,
    address   VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS orders (
    id           SERIAL PRIMARY KEY,
    buyer_id     INTEGER NOT NULL REFERENCES buyers(id) ON DELETE CASCADE,
    product_name VARCHAR(200) NOT NULL,
    quantity     INTEGER NOT NULL CHECK (quantity > 0),
    total_price  NUMERIC(10,2) NOT NULL CHECK (total_price >= 0),
    status       VARCHAR(20) NOT NULL
                 CHECK (status IN ('CREATED','PAID','SHIPPED','DELIVERED','CANCELLED')),
    created_at   TIMESTAMP NOT NULL DEFAULT NOW()
);

CREATE INDEX IF NOT EXISTS idx_orders_buyer_id ON orders(buyer_id);
CREATE INDEX IF NOT EXISTS idx_orders_status   ON orders(status);
CREATE INDEX IF NOT EXISTS idx_orders_created  ON orders(created_at);

INSERT INTO buyers (full_name, email, phone, address) VALUES
    ('Иванов Иван Иванович',     'ivanov@mail.ru',    '+79001112233', 'Москва, ул. Ленина, 1'),
    ('Петрова Анна Сергеевна',   'petrova@mail.ru',   '+79002223344', 'СПб, Невский пр., 10'),
    ('Сидоров Пётр Алексеевич',  'sidorov@mail.ru',   '+79003334455', 'Казань, ул. Баумана, 5'),
    ('Кузнецова Мария Ивановна', 'kuznetsova@mail.ru','+79004445566', 'Екатеринбург, ул. Мира, 20'),
    ('Смирнов Олег Дмитриевич',  'smirnov@mail.ru',   '+79005556677', 'Новосибирск, ул. Кирова, 15');

INSERT INTO orders (buyer_id, product_name, quantity, total_price, status, created_at) VALUES
    (1, 'Royal Canin Adult',      2, 4500.00, 'DELIVERED', NOW() - INTERVAL '20 days'),
    (1, 'Whiskas для кошек',      5, 1200.00, 'PAID',      NOW() - INTERVAL '15 days'),
    (2, 'Pedigree для собак',     3, 2700.00, 'SHIPPED',   NOW() - INTERVAL '10 days'),
    (2, 'Purina One',             1,  900.00, 'CREATED',   NOW() - INTERVAL '8 days'),
    (3, 'Hill''s Science Plan',   4, 6800.00, 'DELIVERED', NOW() - INTERVAL '7 days'),
    (3, 'Brit Care',              2, 3200.00, 'CANCELLED', NOW() - INTERVAL '6 days'),
    (4, 'Acana Wild Prairie',     1, 5200.00, 'PAID',      NOW() - INTERVAL '5 days'),
    (4, 'Orijen Original',        2, 8900.00, 'CREATED',   NOW() - INTERVAL '3 days'),
    (5, 'Monge Natural',          3, 4100.00, 'SHIPPED',   NOW() - INTERVAL '2 days'),
    (5, 'Farmina N&D',            1, 3500.00, 'CREATED',   NOW() - INTERVAL '1 day'),
    (1, 'Josera Kids',            2, 2400.00, 'PAID',      NOW()),
    (2, 'Bosch Adult',            1, 1800.00, 'DELIVERED', NOW());