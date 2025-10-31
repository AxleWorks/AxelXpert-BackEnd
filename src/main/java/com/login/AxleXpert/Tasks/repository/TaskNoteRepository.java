package com.login.AxleXpert.Tasks.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.login.AxleXpert.Tasks.entity.TaskNote;
import com.login.AxleXpert.common.enums.NoteType;

@Repository
public interface TaskNoteRepository extends JpaRepository<TaskNote, Long> {
    List<TaskNote> findByTaskIdOrderByCreatedAtDesc(Long taskId);
    
    @Query("SELECT tn FROM TaskNote tn WHERE tn.task.id = :taskId AND tn.noteType = :noteType ORDER BY tn.createdAt DESC")
    List<TaskNote> findByTaskIdAndNoteTypeOrderByCreatedAtDesc(@Param("taskId") Long taskId, 
                                                               @Param("noteType") NoteType noteType);
}
