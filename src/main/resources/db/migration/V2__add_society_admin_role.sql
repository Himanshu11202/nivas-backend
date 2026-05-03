-- Drop existing check constraint on role
ALTER TABLE users DROP CONSTRAINT IF EXISTS users_role_check;

-- Add new check constraint with all roles including SOCIETY_ADMIN
ALTER TABLE users ADD CONSTRAINT users_role_check 
    CHECK (role IN ('RESIDENT', 'ADMIN', 'GUARD', 'SUPER_ADMIN', 'SOCIETY_ADMIN'));
