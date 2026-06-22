-- ============================================================
-- CLEAN EXISTING DATA
-- ============================================================
SET FOREIGN_KEY_CHECKS = 0;
DELETE FROM Blood_Requests;
DELETE FROM Works_At;
DELETE FROM Blood_Inventory;
DELETE FROM Blood_Donations;
DELETE FROM Blood_Banks;
DELETE FROM Doctors;
DELETE FROM Donors;
DELETE FROM Users;
SET FOREIGN_KEY_CHECKS = 1;

-- ============================================================
-- RESET AUTO_INCREMENT
-- ============================================================
ALTER TABLE Users AUTO_INCREMENT = 1;
ALTER TABLE Donors AUTO_INCREMENT = 1;
ALTER TABLE Doctors AUTO_INCREMENT = 1;
ALTER TABLE Blood_Banks AUTO_INCREMENT = 1;
ALTER TABLE Blood_Donations AUTO_INCREMENT = 1;
ALTER TABLE Blood_Inventory AUTO_INCREMENT = 1;
ALTER TABLE Blood_Requests AUTO_INCREMENT = 1;

-- ============================================================
-- 1. USERS (password = '123')
-- ============================================================
INSERT INTO Users (username, password, role) VALUES
('donor_thu',    '$2a$10$Lu.u56JikrOfVGWsAftTrOtuGPnL/JLmW/ezvJ3zllEu4HN5nrtle', 'donor'),
('donor_phong',    '$2a$10$Lu.u56JikrOfVGWsAftTrOtuGPnL/JLmW/ezvJ3zllEu4HN5nrtle', 'donor'),
('donor_ha',     '$2a$10$Lu.u56JikrOfVGWsAftTrOtuGPnL/JLmW/ezvJ3zllEu4HN5nrtle', 'donor'),
('donor_han',        '$2a$10$Lu.u56JikrOfVGWsAftTrOtuGPnL/JLmW/ezvJ3zllEu4HN5nrtle', 'donor'),
('donor_kien',       '$2a$10$Lu.u56JikrOfVGWsAftTrOtuGPnL/JLmW/ezvJ3zllEu4HN5nrtle', 'donor'),
('donor_ky',   '$2a$10$Lu.u56JikrOfVGWsAftTrOtuGPnL/JLmW/ezvJ3zllEu4HN5nrtle', 'donor'),
('donor_tam',     '$2a$10$Lu.u56JikrOfVGWsAftTrOtuGPnL/JLmW/ezvJ3zllEu4HN5nrtle', 'donor'),
('doctor_minh',       '$2a$10$Lu.u56JikrOfVGWsAftTrOtuGPnL/JLmW/ezvJ3zllEu4HN5nrtle', 'doctor'),
('doctor_hoa',        '$2a$10$Lu.u56JikrOfVGWsAftTrOtuGPnL/JLmW/ezvJ3zllEu4HN5nrtle', 'doctor'),
('doctor_tuan',       '$2a$10$Lu.u56JikrOfVGWsAftTrOtuGPnL/JLmW/ezvJ3zllEu4HN5nrtle', 'doctor'),
('admin',         '$2a$10$Lu.u56JikrOfVGWsAftTrOtuGPnL/JLmW/ezvJ3zllEu4HN5nrtle', 'admin');

-- ============================================================
-- 2. DONORS
-- ============================================================
INSERT INTO Donors (user_id, donor_name, contact, blood_type, last_donate) VALUES
(1, 'Duong Ngoc Anh Thu',     '0912345671', 'A+',  NULL),
(2, 'Nguyen Thien Phong',     '0912345672', 'O+',  '2026-05-20'),
(3, 'Tran Phuong Ha',      '0912345673', 'B-',  '2026-05-25'),
(4, 'Nguyen Hoang Bao Han',         '0912345674', 'AB+', '2026-05-10'),
(5, 'Tran Trung Kien',        '0912345675', 'O-',  '2026-05-30'),
(6, 'Vu Trung Ky',    '0912345676', 'A-',  '2026-04-15'),
(7, 'Mai Quoc Minh Tam',      '0912345677', 'B+',  '2026-05-01');

-- ============================================================
-- 3. DOCTORS
-- ============================================================
INSERT INTO Doctors (user_id, doctor_name, specialization) VALUES
(8,  'Tran Duc Minh',   'Hematology'),
(9,  'Le Thi Hoa',  'Cardiologist'),
(10, 'Nguyen Tran Anh Tuan', 'Dermatologist');

-- ============================================================
-- 4. BLOOD BANKS
-- ============================================================
INSERT INTO Blood_Banks (bank_name, bank_addr) VALUES
('Central Blood Bank',        '123 Le Loi Street, District 1, HCMC'),
('City Blood Center',         '456 Nguyen Hue Boulevard, District 1, HCMC'),
('Community Blood Hub',       '789 Cach Mang Thang Tam Street, District 3, HCMC');

-- ============================================================
-- 5. BLOOD DONATIONS
-- ============================================================
INSERT INTO Blood_Donations (date, donate_addr, donor_id) VALUES
('2026-05-20 10:00:00', 'Central Hospital, District 5', 2),
('2026-05-10 14:30:00', 'Mobile Donation Bus, District 7', 2),
('2026-05-25 09:15:00', 'City Health Fair, District 1', 3),
('2026-05-10 11:00:00', 'Community Center, District 4', 4),
('2026-05-30 13:45:00', 'Central Hospital, District 5', 5),
('2026-04-15 08:30:00', 'Old Town Clinic, District 8', 6),
('2026-05-01 12:00:00', 'Community Blood Hub, District 3', 7);

-- ============================================================
-- 6. BLOOD INVENTORY
-- ============================================================
INSERT INTO Blood_Inventory (blood_type, status, bank_id, donation_id) VALUES
('O+', 'available', 1, 1),
('O+', 'available', 1, 1),
('O+', 'available', 2, 2),
('B-', 'available', 1, 3),
('B-', 'available', 3, 3),
('AB+', 'used', 1, 4),
('AB+', 'used', 1, 4),
('AB+', 'available', 1, 4),
('O-', 'available', 2, 5),
('O-', 'available', 2, 5),
('A-', 'available', 3, 6),   -- will expire (donation 2026-04-15)
('A-', 'available', 3, 6),
('B+', 'available', 1, 7),
('B+', 'available', 1, 7);

-- ============================================================
-- 7. WORKS_AT (doctor-bank assignments)
-- ============================================================
INSERT INTO Works_At (doctor_id, bank_id) VALUES
(1, 1),   -- Tran Duc Minh at Central Blood Bank
(1, 2),   -- also at City Blood Center
(2, 2),   -- Le Thi Hoa at City Blood Center
(3, 3);   -- Nguyen Tran Anh Tuan at Community Blood Hub

-- ============================================================
-- 8. BLOOD REQUESTS (all addresses already in English)
-- ============================================================
INSERT INTO Blood_Requests (blood_type, quantity, request_date, status, doctor_id, bank_id) VALUES
('O+', 2, NOW(), 'pending', 1, 1),
('AB+', 2, '2026-05-15 09:00:00', 'approved', 2, 1),
('A+', 1, '2026-05-20 11:00:00', 'rejected', 3, 2),
('B+', 3, NOW(), 'pending', 1, 1),      -- insufficient stock (only 2 B+ available)
('O-', 1, NOW(), 'pending', 2, 2);      -- enough stock

-- ============================================================
-- VERIFICATION QUERIES (optional)
-- ============================================================
-- SELECT * FROM Donors;
-- SELECT * FROM Doctors;
-- SELECT * FROM Blood_Banks;
-- SELECT * FROM Blood_Inventory;
-- SELECT * FROM Blood_Requests;
-- Test Bcrypt hased password length (should be 60 characters)
-- SELECT username, LENGTH(password) FROM Users;