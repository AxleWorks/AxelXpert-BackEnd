package com.login.AxleXpert.Tasks.dto;

import java.time.LocalDateTime;

// A simple DTO that pairs a technician note with its timestamp.
public record TechnicianNoteInfo(
    String content,
    LocalDateTime addedAt
) {}
