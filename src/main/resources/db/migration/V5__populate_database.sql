INSERT INTO categories (name)
VALUES ('Smartphones'),
       ('Laptops'),
       ('Televisions'),
       ('Smart Home'),
       ('Accessories');

INSERT INTO products (name, price, description, category_id)
VALUES
-- Smartphones
('iPhone 15 Pro Max', 1199.99,
 'Apple’s latest flagship smartphone featuring the A17 Pro chip, Dynamic Island, and 48MP camera.', 1),
('Samsung Galaxy S24 Ultra', 1099.99,
 'Top-tier Android device with Snapdragon 8 Gen 3, 200MP camera, and S-Pen support.', 1),

-- Laptops
('MacBook Air M2', 999.00,
 'Lightweight and powerful with the Apple M2 chip, 13.6-inch display, and all-day battery life.', 2),
('Dell XPS 13', 1099.99, 'Premium ultrabook with 13.4" InfinityEdge display, Intel i7 processor, and 16GB RAM.', 2),

-- Televisions
('LG OLED C3 55"', 1399.00, '55-inch OLED TV with 4K resolution, Dolby Vision, and stunning color accuracy.', 3),
('Samsung QN90C Neo QLED 65"', 1799.00, '4K UHD Smart TV with Quantum Matrix Technology and anti-glare screen.', 3),

-- Smart Home
('Google Nest Thermostat', 129.99, 'Smart thermostat with programmable scheduling, energy savings, and voice control.',
 4),
('Amazon Echo Show 8', 149.99, 'Smart display with Alexa, 8" HD screen, and video calling support.', 4),

-- Accessories
('Anker 100W USB-C Charger', 59.99, 'Fast-charging USB-C charger compatible with laptops, tablets, and phones.', 5),
('Sony WH-1000XM5 Headphones', 399.99, 'Industry-leading noise-canceling over-ear headphones with crystal-clear sound.',
 5);
