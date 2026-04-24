-- Create society_new database
CREATE DATABASE society_new;

-- Connect to society_new database
\c society_new;

-- Create tables manually
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

CREATE TABLE flats (
    id BIGSERIAL PRIMARY KEY,
    flat_number VARCHAR(20) UNIQUE NOT NULL,
    wing VARCHAR(10),
    floor VARCHAR(10)
);

-- Insert default admin user
INSERT INTO users (name, email, password, phone_number, role, flat_number, status, created_at) 
VALUES ('Admin User', 'admin@society.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '9876543210', 'ADMIN', 'ADMIN_OFFICE', 'ACTIVE', NOW());

-- Insert additional admin user
INSERT INTO users (name, email, password, phone_number, role, flat_number, status, created_at) 
VALUES ('Himanshu Admin', 'himanshu001@gmail.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '9876543210', 'ADMIN', 'ADMIN_OFFICE', 'ACTIVE', NOW());

-- Insert sample guard
INSERT INTO users (name, email, password, phone_number, role, flat_number, status, created_at) 
VALUES ('Security Guard', 'guard@society.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '9876543211', 'GUARD', 'SECURITY', 'ACTIVE', NOW());

-- Insert sample residents
INSERT INTO users (name, email, password, phone_number, role, flat_number, status, created_at) VALUES 
('Rajesh Kumar', 'rajesh@society.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '9876543210', 'RESIDENT', 'A-101', 'ACTIVE', NOW()),
('Amit Sharma', 'amit@society.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '9876543211', 'RESIDENT', 'A-102', 'ACTIVE', NOW()),
('Priya Singh', 'priya@society.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '9876543212', 'RESIDENT', 'B-201', 'ACTIVE', NOW());

-- Insert sample flats
INSERT INTO flats (flat_number, wing, floor) VALUES 
('A-101', 'A', '1'),
('A-102', 'A', '1'),
('A-201', 'A', '2'),
('A-202', 'A', '2'),
('B-101', 'B', '1'),
('B-102', 'B', '1'),
('B-201', 'B', '2'),
('B-202', 'B', '2');
