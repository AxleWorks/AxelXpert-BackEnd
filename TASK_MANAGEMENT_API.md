# Task Management System API Documentation

## Overview

The task management system is automatically created when an employee is assigned to a booking. It allows employees to manage subtasks, add notes and images, and track progress. Customers can also add notes for communication with employees.

## Folder Structure

```
Tasks/
├── controller/
│   └── TaskController.java
├── dto/
│   ├── CreateSubTaskDTO.java
│   ├── CreateTaskImageDTO.java
│   ├── CreateTaskNoteDTO.java
│   ├── SubTaskDTO.java
│   ├── TaskDTO.java
│   ├── TaskImageDTO.java
│   ├── TaskNoteDTO.java
│   └── UpdateSubTaskDTO.java
├── entity/
│   ├── Task.java
│   ├── SubTask.java
│   ├── TaskNote.java
│   └── TaskImage.java
├── repository/
│   ├── TaskRepository.java
│   ├── SubTaskRepository.java
│   ├── TaskNoteRepository.java
│   └── TaskImageRepository.java
└── service/
    └── TaskService.java
```

## Entities

### Task

- **id**: Primary key
- **booking**: Reference to booking (ManyToOne)
- **assignedEmployee**: Reference to employee user (ManyToOne)
- **title**: Task title
- **description**: Task description
- **status**: TaskStatus enum (NOT_STARTED, IN_PROGRESS, COMPLETED, ON_HOLD)
- **subTasks**: List of subtasks (OneToMany)
- **taskNotes**: List of notes (OneToMany)
- **taskImages**: List of images (OneToMany)
- **createdAt**, **updatedAt**: Timestamps

### SubTask

- **id**: Primary key
- **task**: Reference to parent task (ManyToOne)
- **title**: Subtask title
- **description**: Subtask description
- **status**: TaskStatus enum
- **orderIndex**: Display order
- **notes**: Additional notes (String)
- **createdAt**, **updatedAt**: Timestamps

### TaskNote

- **id**: Primary key
- **task**: Reference to task (ManyToOne)
- **author**: Reference to user who created note (ManyToOne)
- **noteType**: NoteType enum (EMPLOYEE_NOTE, CUSTOMER_NOTE)
- **content**: Note content (String)
- **createdAt**: Timestamp

### TaskImage

- **id**: Primary key
- **task**: Reference to task (ManyToOne)
- **imageUrl**: Cloudinary URL (String)
- **description**: Image description (String)
- **createdAt**: Timestamp

## API Endpoints

### Task Management

- `GET /api/tasks/employee/{employeeId}` - Get all tasks for an employee
- `GET /api/tasks/customer/{customerId}` - Get all tasks for a customer
- `GET /api/tasks/{taskId}` - Get specific task by ID
- `GET /api/tasks/booking/{bookingId}` - Get task for a specific booking
- `PUT /api/tasks/{taskId}/status?status={status}` - Update task status

### SubTask Management

- `POST /api/tasks/{taskId}/subtasks` - Add subtask to a task
- `PUT /api/tasks/subtasks/{subTaskId}` - Update subtask
- `DELETE /api/tasks/subtasks/{subTaskId}` - Delete subtask

### Notes Management

- `POST /api/tasks/{taskId}/notes?authorId={authorId}` - Add note to task
- `GET /api/tasks/{taskId}/notes?noteType={noteType}` - Get task notes (optionally filtered by type)

### Image Management

- `POST /api/tasks/{taskId}/images` - Add image URL to task
- `GET /api/tasks/{taskId}/images` - Get all images for a task
- `DELETE /api/tasks/images/{imageId}` - Delete an image

## Request/Response Examples

### Create SubTask

```json
POST /api/tasks/1/subtasks
{
    "title": "Check oil level",
    "description": "Verify engine oil level and quality",
    "orderIndex": 1
}
```

### Add Note

```json
POST /api/tasks/1/notes?authorId=2
{
    "noteType": "EMPLOYEE_NOTE",
    "content": "Oil change completed successfully"
}
```

### Add Image

```json
POST /api/tasks/1/images
{
    "imageUrl": "https://res.cloudinary.com/your-cloud/image/upload/v1234567890/task-image.jpg",
    "description": "Before service photo"
}
```

### Update SubTask

```json
PUT /api/tasks/subtasks/1
{
    "status": "COMPLETED",
    "notes": "Task completed without issues"
}
```

## Automatic Task Creation

When an employee is assigned to a booking through the booking service, a task is automatically created with:

- Title: "Service Task - {ServiceName}"
- Description: "Complete the {ServiceName} service for customer: {CustomerName}"
- Status: NOT_STARTED

## Status Calculation

The main task status is automatically calculated based on subtask statuses:

- **COMPLETED**: All subtasks are completed
- **ON_HOLD**: Any subtask is on hold
- **IN_PROGRESS**: Any subtask is in progress (and none on hold)
- **NOT_STARTED**: All subtasks are not started

## Integration with Cloudinary

Images are stored as URLs from Cloudinary service. The frontend handles the actual image upload to Cloudinary and sends the resulting URL to the backend for storage.

## Database Tables Created

- `tasks`
- `sub_tasks`
- `task_notes`
- `task_images`
