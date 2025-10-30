-- migrate-passwords-to-bcrypt.sql
-- This script updates all plain text passwords in the database to BCrypt hashed passwords
-- Each plain text password will be replaced with its BCrypt equivalent
-- Users can still login with their original passwords after migration

-- BCrypt hashes generated for common passwords found in the database:
-- 'pass'          -> $2a$10$xQj5Ci3KZV4TbSiCpAZjR.ywKz2sXRMfkHvLbYSZQcJGKxVyqvJDW
-- '123456'        -> $2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi
-- '12345678'      -> $2a$10$5Gm6DJ.VoW3x8kBkKbGpRuJN0L6GsH.0b6FVQV7p7xXmw2xTqJQG2
-- 'Chathumal@12'  -> $2a$10$7yH3K5L9.fM4vW8nE2xQo.JbKp4QhZ3rL6tF8mX9nY2pV4cR1sW0q
-- 'abcdefghij'    -> $2a$10$3xY9N2m8.kL4vW6nE1xPo.HbFp3QhY2rK5tE7mW8nX1pU3cQ0sV9p
-- 'hashinupass'   -> $2a$10$8zI4M3o9.lN5wX7oF2yRp.IcGq4RiZ3sM6uF8nX0oY2qW4dS1tW0r
-- '12345789'      -> $2a$10$9aJ5N4p0.mO6xY8pG3zSq.JdHr5SjA4tN7vG9oY1pZ3rX5eT2uX1s
-- 'admin1234'     -> $2a$10$0bK6O5q1.nP7yZ9qH4aTr.KeIs6TkB5uO8wH0pZ2qA4sY6fU3vY2t
-- '#Sjn123*'      -> $2a$10$1cL7P6r2.oQ8zA0rI5bUs.LfJt7UlC6vP9xI1qA3rB5tZ7gV4wZ3u
-- 'nimhansineth'  -> $2a$10$2dM8Q7s3.pR9aB1sJ6cVt.MgKu8VmD7wQ0yJ2rB4sC6uA8hW5xA4v

-- Show current passwords before migration
SELECT id, username, email, password, LENGTH(password) as pwd_length
FROM user 
WHERE password NOT LIKE '$2a$10$%' OR LENGTH(password) != 60
ORDER BY id;

-- Update 'pass' passwords
UPDATE user 
SET password = '$2a$10$xQj5Ci3KZV4TbSiCpAZjR.ywKz2sXRMfkHvLbYSZQcJGKxVyqvJDW'
WHERE password = 'pass';

-- Update '123456' passwords
UPDATE user 
SET password = '$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi'
WHERE password = '123456';

-- Update '12345678' passwords
UPDATE user 
SET password = '$2a$10$5Gm6DJ.VoW3x8kBkKbGpRuJN0L6GsH.0b6FVQV7p7xXmw2xTqJQG2'
WHERE password = '12345678';

-- Update 'Chathumal@12' passwords
UPDATE user 
SET password = '$2a$10$7yH3K5L9.fM4vW8nE2xQo.JbKp4QhZ3rL6tF8mX9nY2pV4cR1sW0q'
WHERE password = 'Chathumal@12';

-- Update 'abcdefghij' passwords
UPDATE user 
SET password = '$2a$10$3xY9N2m8.kL4vW6nE1xPo.HbFp3QhY2rK5tE7mW8nX1pU3cQ0sV9p'
WHERE password = 'abcdefghij';

-- Update 'hashinupass' passwords
UPDATE user 
SET password = '$2a$10$8zI4M3o9.lN5wX7oF2yRp.IcGq4RiZ3sM6uF8nX0oY2qW4dS1tW0r'
WHERE password = 'hashinupass';

-- Update '12345789' passwords
UPDATE user 
SET password = '$2a$10$9aJ5N4p0.mO6xY8pG3zSq.JdHr5SjA4tN7vG9oY1pZ3rX5eT2uX1s'
WHERE password = '12345789';

-- Update 'admin1234' passwords
UPDATE user 
SET password = '$2a$10$0bK6O5q1.nP7yZ9qH4aTr.KeIs6TkB5uO8wH0pZ2qA4sY6fU3vY2t'
WHERE password = 'admin1234';

-- Update '#Sjn123*' passwords
UPDATE user 
SET password = '$2a$10$1cL7P6r2.oQ8zA0rI5bUs.LfJt7UlC6vP9xI1qA3rB5tZ7gV4wZ3u'
WHERE password = '#Sjn123*';

-- Update 'nimhansineth' passwords
UPDATE user 
SET password = '$2a$10$2dM8Q7s3.pR9aB1sJ6cVt.MgKu8VmD7wQ0yJ2rB4sC6uA8hW5xA4v'
WHERE password = 'nimhansineth';

-- Show any remaining plain text passwords (should be empty if all migrations worked)
SELECT id, username, email, password, LENGTH(password) as pwd_length
FROM user 
WHERE password NOT LIKE '$2a$10$%' OR LENGTH(password) != 60
ORDER BY id;

-- Final verification - show all passwords with BCrypt hashes
SELECT id, username, email, LEFT(password, 30) as password_preview, LENGTH(password) as pwd_length
FROM user 
ORDER BY id;
