# Profile Image Management Implementation

## Overview
This implementation adds profile image management functionality to the AxelXpert backend, including database schema updates and API endpoints for uploading, updating, and deleting profile images using Cloudinary.

## Changes Made

### 1. Database Schema Updates

#### User Entity (`User.java`)
- Added `profileImageUrl` field (VARCHAR 500) - stores the Cloudinary image URL
- Added `cloudinaryPublicId` field (VARCHAR 300) - stores the Cloudinary public ID for deletion

#### UserDTO (`UserDTO.java`)
- Added `profileImageUrl` and `cloudinaryPublicId` fields
- Updated constructor automatically via Lombok annotations

### 2. New DTO Class

#### ProfileImageUpdateDTO (`ProfileImageUpdateDTO.java`)
- Created for profile image update requests
- Contains `profileImageUrl` and `cloudinaryPublicId` fields

### 3. Service Layer Updates

#### UserService (`UserService.java`)
- Updated `toDto()` method to include profile image fields
- Added `updateProfileImage()` method - updates user's profile image data
- Added `deleteProfileImage()` method - removes profile image data (sets to null)

### 4. Controller Layer Updates

#### UserController (`UserController.java`)
- Added `PUT /api/users/{id}/profile-image` endpoint
- Added `DELETE /api/users/{id}/profile-image` endpoint
- Added necessary imports for `@DeleteMapping`

## API Endpoints

### Update Profile Image
```http
PUT /api/users/{id}/profile-image
Content-Type: application/json

{
  "profileImageUrl": "https://res.cloudinary.com/dumsebwgb/image/upload/v1234567890/profile_photos/user_123/abc123.jpg",
  "cloudinaryPublicId": "profile_photos/user_123/abc123"
}
```

**Response:**
```json
{
  "id": 123,
  "username": "johndoe",
  "email": "john@example.com",
  "role": "user",
  "profileImageUrl": "https://res.cloudinary.com/dumsebwgb/image/upload/v1234567890/profile_photos/user_123/abc123.jpg",
  "cloudinaryPublicId": "profile_photos/user_123/abc123",
  "isBlocked": false,
  "isActive": true,
  "address": "123 Main St",
  "phoneNumber": "+1234567890",
  "branchId": 1,
  "branchName": "Main Branch",
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00"
}
```

### Delete Profile Image
```http
DELETE /api/users/{id}/profile-image
```

**Response:**
```json
{
  "id": 123,
  "username": "johndoe",
  "email": "john@example.com",
  "role": "user",
  "profileImageUrl": null,
  "cloudinaryPublicId": null,
  "isBlocked": false,
  "isActive": true,
  "address": "123 Main St",
  "phoneNumber": "+1234567890",
  "branchId": 1,
  "branchName": "Main Branch",
  "createdAt": "2024-01-01T00:00:00",
  "updatedAt": "2024-01-01T00:00:00"
}
```

## Database Migration

### SQL Script (`database-migration-profile-image.sql`)
Execute this on your MySQL database to add the required columns:

```sql
ALTER TABLE user 
ADD COLUMN profile_image_url VARCHAR(500) NULL,
ADD COLUMN cloudinary_public_id VARCHAR(300) NULL;
```

## Key Features

1. **Profile Image URL Storage**: Stores the full Cloudinary URL for direct access
2. **Cloudinary Public ID**: Stores the public ID for efficient deletion via Cloudinary API
3. **Null Safety**: Fields are nullable and properly handled in service methods
4. **Consistent Response Format**: All user endpoints now include profile image data
5. **Validation**: Basic validation ensures required fields are present for updates
6. **Transactional Safety**: All database operations are properly wrapped in transactions

## Integration Notes

- Profile image fields are now included in all user-related API responses
- The `getUserById`, `getEmployees`, `getUsers`, `getManagers`, and `getEmployeesByBranch` endpoints all return profile image data
- The database uses Hibernate's `update` mode, so schema changes will be applied automatically on next startup
- Profile images are optional fields and don't affect existing functionality

## Next Steps

1. Execute the database migration script on your production database
2. Test the new endpoints with your frontend application
3. Implement Cloudinary integration in your frontend for image upload
4. Consider adding image validation (file size, format) if needed
5. Add logging for profile image operations for audit purposes