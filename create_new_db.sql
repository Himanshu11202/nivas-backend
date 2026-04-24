-- Create new database for society management
CREATE DATABASE society_test_db;

-- Connect to new database
\c society_test_db;

-- Create users table
CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    email VARCHAR(100) UNIQUE NOT NULL,
    password VARCHAR(255) NOT NULL,
    phone_number VARCHAR(20),
    role VARCHAR(20) NOT NULL,
    flat_number VARCHAR(20),
    status VARCHAR(20) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create flats table
CREATE TABLE flats (
    id BIGSERIAL PRIMARY KEY,
    flat_number VARCHAR(20) UNIQUE NOT NULL,
    wing VARCHAR(10),
    floor VARCHAR(10)
);

-- Insert test admin user with correct BCrypt hash for "secret"
INSERT INTO users (name, email, password, phone_number, role, flat_number, status, created_at) 
VALUES ('Test Admin', 'admin@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17VyGW', '9876543210', 'ADMIN', 'ADMIN_OFFICE', 'ACTIVE', NOW());

-- Insert test resident
INSERT INTO users (name, email, password, phone_number, role, flat_number, status, created_at) 
VALUES ('Test Resident', 'resident@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17VyGW', '9876543211', 'RESIDENT', 'A-101', 'ACTIVE', NOW());

-- Insert sample flats
INSERT INTO flats (flat_number, wing, floor) VALUES 
('A-101', 'A', '1'),
('A-102', 'A', '1'),
('B-101', 'B', '1'),
('B-102', 'B', '1');
