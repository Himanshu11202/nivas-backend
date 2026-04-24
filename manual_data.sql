-- Manual data insertion for society_new database

-- Insert default admin user
INSERT INTO users (name, email, password, phone_number, role, flat_number, status, created_at) 
VALUES ('Admin User', 'admin@society.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '9876543210', 'ADMIN', 'ADMIN_OFFICE', 'ACTIVE', NOW());

-- Insert additional admin user
INSERT INTO users (name, email, password, phone_number, role, flat_number, status, created_at) 
VALUES ('Himanshu Admin', 'himanshu001@gmail.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '9876543210', 'ADMIN', 'ADMIN_OFFICE', 'ACTIVE', NOW());

-- Insert test admin with simple password
INSERT INTO users (name, email, password, phone_number, role, flat_number, status, created_at) 
VALUES ('Test Admin', 'test@admin.com', '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2uheWG/igi.', '9876543230', 'ADMIN', 'ADMIN_OFFICE', 'ACTIVE', NOW());

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
('B-202', 'B', '2'),
('C-101', 'C', '1'),
('C-102', 'C', '1'),
('C-201', 'C', '2'),
('C-202', 'C', '2'),
('D-101', 'D', '1'),
('D-102', 'D', '1'),
('D-201', 'D', '2'),
('D-202', 'D', '2');
