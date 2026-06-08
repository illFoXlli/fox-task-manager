package com.fox.taskmanager.repository;

import com.fox.taskmanager.model.Note;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface NoteRepository extends JpaRepository<Note, Long> {

    List<Note> findAllByUserProfileLoginOrderByUpdatedAtDesc(String login);

    Optional<Note> findByIdAndUserProfileLogin(Long id, String login);
}
