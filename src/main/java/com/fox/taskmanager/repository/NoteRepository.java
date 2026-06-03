package com.fox.taskmanager.repository;

import com.fox.taskmanager.model.Note;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<Note, Long> {
}
