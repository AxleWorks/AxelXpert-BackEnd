-- Database migration script to add profile image fields to users table
-- Execute this script on your MySQL database

ALTER TABLE user 
ADD COLUMN profile_image_url VARCHAR(500) NULL,
ADD COLUMN cloudinary_public_id VARCHAR(300) NULL;

-- Verify the changes
DESCRIBE user;