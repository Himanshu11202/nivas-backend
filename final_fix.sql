-- FINAL FIX - Correct BCrypt hash for "secret" password

-- Delete existing users
DELETE FROM users WHERE email IN ('admin@society.com', 'himanshu001@gmail.com', 'test@admin.com', 'guard@society.com', 'rajesh@society.com');

-- Insert with CORRECT BCrypt hash for "secret"
INSERT INTO users (name, email, password, phone_number, role, flat_number, status, created_at) 
VALUES ('Admin User', 'admin@society.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17VyGW', '9876543210', 'ADMIN', 'ADMIN_OFFICE', 'ACTIVE', NOW());

INSERT INTO users (name, email, password, phone_number, role, flat_number, status, created_at) 
VALUES ('Himanshu Admin', 'himanshu001@gmail.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17VyGW', '9876543210', 'ADMIN', 'ADMIN_OFFICE', 'ACTIVE', NOW());

INSERT INTO users (name, email, password, phone_number, role, flat_number, status, created_at) 
VALUES ('Test Admin', 'test@admin.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17VyGW', '9876543230', 'ADMIN', 'ADMIN_OFFICE', 'ACTIVE', NOW());

INSERT INTO users (name, email, password, phone_number, role, flat_number, status, created_at) 
VALUES ('Security Guard', 'guard@society.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17VyGW', '9876543211', 'GUARD', 'SECURITY', 'ACTIVE', NOW());

INSERT INTO users (name, email, password, phone_number, role, flat_number, status, created_at) 
VALUES ('Rajesh Kumar', 'rajesh@society.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17VyGW', '9876543210', 'RESIDENT', 'A-101', 'ACTIVE', NOW());

INSERT INTO users (name, email, password, phone_number, role, flat_number, status, created_at) 
VALUES ('Amit Sharma', 'amit@society.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17VyGW', '9876543211', 'RESIDENT', 'A-102', 'ACTIVE', NOW());
