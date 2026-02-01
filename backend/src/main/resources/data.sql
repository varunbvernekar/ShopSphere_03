-- Users (Indian Names & Phones)
-- Password is 'password' encoded
INSERT INTO users (id, name, email, password, role, phone_number, gender, date_of_birth, street, city, state, zip_code, country) VALUES
(1, 'Admin User', 'admin@shopsphere.com', '$2a$10$N.zmdr9k7uOCQb376NoUnutj8iAt6ValgoL9ADM/opnNlcyrryV8W', 'ADMIN', '9876543210', 'Male', '1990-01-01', '123 Admin St', 'Mumbai', 'Maharashtra', '400001', 'India'),
(2, 'Aditya Sharma', 'aditya@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnutj8iAt6ValgoL9ADM/opnNlcyrryV8W', 'CUSTOMER', '9898989898', 'Male', '1995-05-15', '456 Linking Rd', 'Mumbai', 'Maharashtra', '400050', 'India'),
(3, 'Priya Patel', 'priya@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnutj8iAt6ValgoL9ADM/opnNlcyrryV8W', 'CUSTOMER', '9988776655', 'Female', '1998-08-20', '789 MG Road', 'Ahmedabad', 'Gujarat', '380001', 'India'),
(4, 'Rahul Verma', 'rahul@example.com', '$2a$10$N.zmdr9k7uOCQb376NoUnutj8iAt6ValgoL9ADM/opnNlcyrryV8W', 'CUSTOMER', '8877665544', 'Male', '1992-11-10', '101 Indiranagar', 'Bangalore', 'Karnataka', '560038', 'India');

-- Products (Handmade, Specific Categories, Custom IDs, Local Image Paths)

-- JEWELRY (3 items)
INSERT INTO products (product_id, name, description, category, base_price, is_active, preview_image) VALUES
('P1769940721571', 'Handcrafted Silver Oxidized Necklace', 'Traditional handmade oxidized silver necklace with intricate details. Perfect for festive occasions.', 'Jewelry', 1299.00, true, '/api/uploads/images/3bafb4de-ccb4-4ac9-8dc7-a71fd80ca4fc.jpg'),
('P1769940721572', 'Terracotta Jhumka Earrings', 'Eco-friendly handmade terracotta earrings painted in vibrant colors.', 'Jewelry', 499.00, true, '/api/uploads/images/e7d8f9a1-b2c3-4d5e-9f0a-1b2c3d4e5f6g.jpg'),
('P1769940721573', 'Beaded Boho Bracelet Set', 'A set of 3 handmade beaded bracelets with semi-precious stones.', 'Jewelry', 799.00, true, '/api/uploads/images/h8i9j0k1-l2m3-4n5o-6p7q-8r9s0t1u2v3w.jpg');

-- ACCESSORIES (3 items)
INSERT INTO products (product_id, name, description, category, base_price, is_active, preview_image) VALUES
('P1769940721574', 'Hand-painted Canvas Tote Bag', 'Eco-friendly canvas tote bag with hand-painted floral designs.', 'Accessories', 599.00, true, '/api/uploads/images/a1b2c3d4-e5f6-4789-8012-34567890abcd.jpg'),
('P1769940721575', 'Embroidered Silk Potli Bag', 'Traditional silk potli bag with intricate gold embroidery. Ideal for weddings.', 'Accessories', 899.00, true, '/api/uploads/images/f1e2d3c4-b5a6-4978-9021-43658709fedc.jpg'),
('P1769940721576', 'Handwoven Woolen Scarf', 'Soft and warm handwoven wool scarf in pastel shades.', 'Accessories', 1499.00, true, '/api/uploads/images/1a2b3c4d-5e6f-7a8b-9c0d-1e2f3a4b5c6d.jpg');

-- HOME DECOR (3 items)
INSERT INTO products (product_id, name, description, category, base_price, is_active, preview_image) VALUES
('P1769940721577', 'Macrame Wall Hanging', 'Beautiful handmade macrame wall hanging to add a boho touch to your room.', 'Home Decor', 1899.00, true, '/api/uploads/images/99887766-5544-3322-1100-aabbccddeeff.jpg'),
('P1769940721578', 'Hand-painted Clay Vase', 'Artistic clay vase hand-painted with tribal Warli art.', 'Home Decor', 999.00, true, '/api/uploads/images/00112233-4455-6677-8899-ffeeddccbbaa.jpg'),
('P1769940721579', 'Embroidered Cushion Cover Set', 'Set of 2 handmade cotton cushion covers with mirror work embroidery.', 'Home Decor', 1299.00, true, '/api/uploads/images/12345678-90ab-cdef-1234-567890abcdef.jpg');

-- KITCHEN (3 items)
INSERT INTO products (product_id, name, description, category, base_price, is_active, preview_image) VALUES
('P1769940721580', 'Handmade Ceramic Dinner Set', 'Set of 6 handmade ceramic dinner plates with a rustic glaze finish.', 'Kitchen', 2499.00, true, '/api/uploads/images/fedcba98-7654-3210-fedc-ba9876543210.jpg'),
('P1769940721581', 'Wooden Serving Tray', 'Hand-carved wooden serving tray with brass inlay work.', 'Kitchen', 1599.00, true, '/api/uploads/images/abcdef01-2345-6789-abcd-ef0123456789.jpg'),
('P1769940721582', 'Hand-painted Tea Coasters', 'Set of 4 wooden tea coasters featuring Madhubani art.', 'Kitchen', 499.00, true, '/api/uploads/images/45678901-23ib-cdef-4567-890123abcdef.jpg');


-- Inventory
INSERT INTO inventory (id, product_id, quantity, reorder_threshold) VALUES
(1, 'P1769940721571', 50, 5),
(2, 'P1769940721572', 100, 10),
(3, 'P1769940721573', 75, 10),
(4, 'P1769940721574', 40, 5),
(5, 'P1769940721575', 30, 5),
(6, 'P1769940721576', 25, 5),
(7, 'P1769940721577', 15, 2),
(8, 'P1769940721578', 20, 5),
(9, 'P1769940721579', 60, 10),
(10, 'P1769940721580', 10, 2),
(11, 'P1769940721581', 18, 5),
(12, 'P1769940721582', 50, 10);

-- Custom Option Groups & Items
-- NOTE: type MUST be 'colour', 'size', or 'material' as per frontend CustomOptionType
-- NOTE: We explicitly DELETE existing items for these groups to prevent duplicates on restart

DELETE FROM custom_option_items WHERE option_group_id IN (1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12);

-- Necklace (Finishing -> mapped to material)
INSERT IGNORE INTO custom_option_groups (id, product_id, type) VALUES (1, 'P1769940721571', 'material');
INSERT INTO custom_option_items (option_group_id, label, price_modifier) VALUES 
(1, 'Matte Finish', 0.0),
(1, 'Glossy Finish', 100.0);

-- Earrings (Color -> mapped to colour)
INSERT IGNORE INTO custom_option_groups (id, product_id, type) VALUES (2, 'P1769940721572', 'colour');
INSERT INTO custom_option_items (option_group_id, label, price_modifier) VALUES 
(2, 'Red', 0.0),
(2, 'Blue', 0.0),
(2, 'Green', 0.0);

-- Bracelet (Size -> mapped to size)
INSERT IGNORE INTO custom_option_groups (id, product_id, type) VALUES (7, 'P1769940721573', 'size');
INSERT INTO custom_option_items (option_group_id, label, price_modifier) VALUES 
(7, 'Small 6in', 0.0),
(7, 'Medium 7in', 0.0),
(7, 'Large 8in', 50.0);

-- Tote Bag (Design -> mapped to material)
INSERT IGNORE INTO custom_option_groups (id, product_id, type) VALUES (3, 'P1769940721574', 'material');
INSERT INTO custom_option_items (option_group_id, label, price_modifier) VALUES 
(3, 'Canvas - Sunflower Print', 0.0),
(3, 'Canvas - Rose Print', 0.0);

-- Potli Bag (Color -> mapped to colour)
INSERT IGNORE INTO custom_option_groups (id, product_id, type) VALUES (4, 'P1769940721575', 'colour');
INSERT INTO custom_option_items (option_group_id, label, price_modifier) VALUES 
(4, 'Red', 0.0),
(4, 'Gold', 50.0);

-- Scarf (Color -> mapped to colour)
INSERT IGNORE INTO custom_option_groups (id, product_id, type) VALUES (8, 'P1769940721576', 'colour');
INSERT INTO custom_option_items (option_group_id, label, price_modifier) VALUES 
(8, 'Pastel Pink', 0.0),
(8, 'Sky Blue', 0.0),
(8, 'Cream', 0.0);

-- Macrame (Size -> mapped to size)
INSERT IGNORE INTO custom_option_groups (id, product_id, type) VALUES (9, 'P1769940721577', 'size');
INSERT INTO custom_option_items (option_group_id, label, price_modifier) VALUES 
(9, 'Small 12x24', 0.0),
(9, 'Large 24x48', 500.0);

-- Vase (Design -> mapped to material)
INSERT IGNORE INTO custom_option_groups (id, product_id, type) VALUES (10, 'P1769940721578', 'material');
INSERT INTO custom_option_items (option_group_id, label, price_modifier) VALUES 
(10, 'Natural Clay', 0.0),
(10, 'Glazed', 200.0);

-- Cushion Cover (Size -> mapped to size)
INSERT IGNORE INTO custom_option_groups (id, product_id, type) VALUES (5, 'P1769940721579', 'size');
INSERT INTO custom_option_items (option_group_id, label, price_modifier) VALUES 
(5, '16x16 inches', 0.0),
(5, '18x18 inches', 100.0);

-- Ceramic Set (Color -> mapped to colour)
INSERT IGNORE INTO custom_option_groups (id, product_id, type) VALUES (6, 'P1769940721580', 'colour');
INSERT INTO custom_option_items (option_group_id, label, price_modifier) VALUES 
(6, 'Teal Blue', 0.0),
(6, 'Earthen Brown', 0.0);

-- Tray (Wood Type -> mapped to material)
INSERT IGNORE INTO custom_option_groups (id, product_id, type) VALUES (11, 'P1769940721581', 'material');
INSERT INTO custom_option_items (option_group_id, label, price_modifier) VALUES 
(11, 'Teak Wood', 0.0),
(11, 'Rose Wood', 300.0);

-- Coasters (Design -> mapped to material)
INSERT IGNORE INTO custom_option_groups (id, product_id, type) VALUES (12, 'P1769940721582', 'material');
INSERT INTO custom_option_items (option_group_id, label, price_modifier) VALUES 
(12, 'Square', 0.0),
(12, 'Round', 0.0);
