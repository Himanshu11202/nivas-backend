-- Quick test - Insert user directly
INSERT INTO users (name, email, password, phone_number, role, flat_number, status, created_at) 
VALUES ('Test User', 'test@test.com', '$2a$10$N9qo8uLOickgx2ZMRZoMyeIjZAgcfl7p92ldGxad68LJZdL17VyGW', '9876543210', 'ADMIN', 'ADMIN_OFFICE', 'ACTIVE', NOW());
