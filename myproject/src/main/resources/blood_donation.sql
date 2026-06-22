DROP DATABASE IF EXISTS blood_donation;
CREATE DATABASE blood_donation;
USE blood_donation;

-- 1. Table: Users
-- Stores authentication data with hashed passwords
CREATE TABLE Users (
    user_id INT PRIMARY KEY AUTO_INCREMENT,
    username VARCHAR(50) NOT NULL UNIQUE,
    -- Password column is 255 chars to accommodate BCrypt hash strings
    password VARCHAR(255) NOT NULL, 
    role ENUM('admin', 'doctor', 'donor') NOT NULL
);

-- 2. Table: Donors
-- Linked to Users via user_id (Unique to ensure 1-to-1 profile mapping)
CREATE TABLE Donors (
    donor_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL UNIQUE,  -- 1 user corresponds to 1 donor
    donor_name VARCHAR(100) NOT NULL,
    contact VARCHAR(20) NOT NULL,
    blood_type ENUM('A+', 'A-', 'B+', 'B-', 'O+', 'O-', 'AB+', 'AB-') NOT NULL,
    last_donate DATE,
    FOREIGN KEY (user_id) REFERENCES Users(user_id)
);

-- 3. Table: Doctors
-- Linked to Users via user_id (Unique to ensure 1-to-1 profile mapping)
CREATE TABLE Doctors (
    doctor_id INT PRIMARY KEY AUTO_INCREMENT,
    user_id INT NOT NULL UNIQUE, -- 1 user corresponds to 1 doctor
    doctor_name VARCHAR(100) NOT NULL,
    specialization VARCHAR(50), 
    FOREIGN KEY (user_id) REFERENCES Users(user_id)
);

-- 4. Table: Blood_Banks
CREATE TABLE Blood_Banks (
    bank_id INT PRIMARY KEY AUTO_INCREMENT,
    bank_name VARCHAR(100) NOT NULL,
    bank_addr VARCHAR(150) NOT NULL
);

-- 5. Table: Blood_Donations
-- Tracks the specific event where a donor gives blood
CREATE TABLE Blood_Donations (
    donation_id INT PRIMARY KEY AUTO_INCREMENT,
    date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    donate_addr VARCHAR(150),
    donor_id INT NOT NULL,
    FOREIGN KEY (donor_id) REFERENCES Donors(donor_id)
);

-- 6. Table: Blood_Inventory
-- Each row represents A SINGLE BLOOD BAG linked to a donation
CREATE TABLE Blood_Inventory (
    inventory_id INT PRIMARY KEY AUTO_INCREMENT,
    blood_type ENUM('A+', 'A-', 'B+', 'B-', 'O+', 'O-', 'AB+', 'AB-') NOT NULL,
    expdate DATE, -- This will be handled by the trigger below
    status ENUM('available', 'expired', 'used') NOT NULL DEFAULT 'available',
    bank_id INT NOT NULL,
    donation_id INT NOT NULL, 
    FOREIGN KEY (bank_id) REFERENCES Blood_Banks(bank_id),
    FOREIGN KEY (donation_id) REFERENCES Blood_Donations(donation_id)
);

-- 7. Table: Works_At (Many-to-Many relationship between Doctors and Banks)
CREATE TABLE Works_At (
    doctor_id INT NOT NULL,
    bank_id INT NOT NULL,
    PRIMARY KEY (doctor_id, bank_id),
    FOREIGN KEY (doctor_id) REFERENCES Doctors(doctor_id),
    FOREIGN KEY (bank_id) REFERENCES Blood_Banks(bank_id)
);

-- 8. Table: Blood_Requests
CREATE TABLE Blood_Requests (
    request_id INT PRIMARY KEY AUTO_INCREMENT,
    blood_type ENUM('A+', 'A-', 'B+', 'B-', 'O+', 'O-', 'AB+', 'AB-') NOT NULL,
    quantity INT NOT NULL,  -- Number of blood bags requested
    request_date DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
    status ENUM('pending', 'approved', 'rejected') NOT NULL DEFAULT 'pending',
    doctor_id INT NOT NULL,
    bank_id INT NULL,
    FOREIGN KEY (doctor_id) REFERENCES Doctors(doctor_id),
    FOREIGN KEY (bank_id) REFERENCES Blood_Banks(bank_id)
);

-- =============================================================
-- TRIGGERS
-- =============================================================

-- 1. Auto-calculate Expiration Date (30 Days)
DELIMITER //

CREATE TRIGGER tr_set_expdate
BEFORE INSERT ON Blood_Inventory
FOR EACH ROW
BEGIN
    DECLARE donation_date DATE;
    
    -- Fetch the donation date from the parent donation record
    SELECT DATE(date) INTO donation_date 
    FROM Blood_Donations 
    WHERE donation_id = NEW.donation_id;
    
    -- Set expdate to 30 days after the donation date
    SET NEW.expdate = DATE_ADD(donation_date, INTERVAL 30 DAY);
END;
//

DELIMITER ;

-- 2. Change Blood Unit's Status when exprired

-- ENABLE SYSTEM EVENT SCHEDULER
-- This must be ON for the expiration logic to work automatically
SET GLOBAL event_scheduler = ON;

-- AUTOMATIC EXPIRATION EVENT
-- Runs every day at midnight to update status based on expdate
DELIMITER //

CREATE EVENT ev_update_blood_expiry
ON SCHEDULE EVERY 1 DAY
STARTS (TIMESTAMP(CURRENT_DATE) + INTERVAL 1 DAY) -- Run at 00:00 every day
DO
BEGIN
  UPDATE Blood_Inventory 
  SET status = 'expired' 
  WHERE expdate < CURDATE() 
    AND status = 'available';
END //

DELIMITER ;

-- 3. Update last donation date automatically
DELIMITER //

CREATE TRIGGER tr_update_last_donation_date
AFTER INSERT ON Blood_Donations
FOR EACH ROW
BEGIN
    -- Update last_donate in Donors using latest donate date
    UPDATE Donors 
    SET last_donate = NEW.date 
    WHERE donor_id = NEW.donor_id;
END;
//

DELIMITER ;