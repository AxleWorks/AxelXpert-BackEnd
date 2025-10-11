package com.login.AxleXpert.Tasks.dto;

import java.time.LocalDateTime;

import com.login.AxleXpert.common.enums.NoteType;

public record TaskNoteDTO(
    Long id,
    Long taskId,
    Long authorId,
    String authorName,
    NoteType noteType,
    String content,
    LocalDateTime createdAt
) {}