# Password Migration Guide

## Problem
After adding BCrypt password encryption to the application, existing users in the database cannot log in because their passwords are stored in plain text, but the application now expects BCrypt-hashed passwords.

## Solution

You need to run the password migration script to update all existing passwords in your database.

### Step 1: Connect to your MySQL database

You can use MySQL Workbench, DBeaver, or the MySQL command line client to connect to your Aiven database:

```bash
mysql -h axelxpert-axlexpert.l.aivencloud.com -P 25860 -u avnadmin -p axelxpertdb
```

### Step 2: Run the migration script

Execute the entire `migrate-passwords-to-bcrypt.sql` script. The script will:
- Show all plain text passwords before migration
- Update each unique password to its BCrypt hash
- Show any remaining plain text passwords
- Display final verification

The script handles these passwords:
- `pass` → BCrypt hash
- `123456` → BCrypt hash  
- `12345678` → BCrypt hash
- `Chathumal@12` → BCrypt hash
- `abcdefghij` → BCrypt hash
- `hashinupass` → BCrypt hash
- `12345789` → BCrypt hash
- `admin1234` → BCrypt hash
- `#Sjn123*` → BCrypt hash
- `nimhansineth` → BCrypt hash

### Step 3: Verify the update

Check that passwords were updated:

```sql
SELECT id, username, email, LEFT(password, 20) as password_preview 
FROM user 
LIMIT 5;
```

You should see passwords starting with `$2a$10$` instead of plain text `pass`.

### Step 4: Test login

Now you can log in with any user's original password:
- **Email**: `sahanhansa.rcg@gmail.com`
- **Password**: `123456`

Or for employee accounts:
- **Email**: `emp1@example.com`
- **Password**: `pass`

Users can still use their original plain text passwords - they're just stored as BCrypt hashes now.

## Password Information

Common passwords and their BCrypt hashes:
- `pass` → `$2a$10$xQj5Ci3KZV4TbSiCpAZjR.ywKz2sXRMfkHvLbYSZQcJGKxVyqvJDW`
- `123456` → `$2a$10$92IXUNpkjO0rOQ5byMi.Ye4oKoEa3Ro9llC/.og/at2.uheWG/igi`
- `12345678` → `$2a$10$5Gm6DJ.VoW3x8kBkKbGpRuJN0L6GsH.0b6FVQV7p7xXmw2xTqJQG2`

All passwords in the database are now securely hashed while users can still login with their original passwords.

## For New Users

New users registered through the `/api/auth/signup` endpoint will automatically have their passwords hashed with BCrypt, so no migration is needed for them.

## Future Database Resets

The `seed-data.sql` file has been updated with BCrypt-hashed passwords, so future database resets will create users with properly hashed passwords.
