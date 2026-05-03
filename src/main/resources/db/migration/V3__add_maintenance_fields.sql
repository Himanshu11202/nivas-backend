-- Add maintenance payment tracking fields to societies table
ALTER TABLE societies ADD COLUMN total_revenue DOUBLE DEFAULT 0.0;
ALTER TABLE societies ADD COLUMN pending_payments DOUBLE DEFAULT 0.0;
ALTER TABLE societies ADD COLUMN maintenance_amount DOUBLE DEFAULT 1000.0;
ALTER TABLE societies ADD COLUMN maintenance_due_date TIMESTAMP;
