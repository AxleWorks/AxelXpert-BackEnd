package com.login.AxleXpert.Tasks.dto;

import com.login.AxleXpert.common.enums.NoteType;

public record CreateTaskNoteDTO(
    NoteType noteType,
    String content,
    boolean visibleToCustomer
) {}
