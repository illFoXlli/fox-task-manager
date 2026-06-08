package com.fox.taskmanager.service;

import com.fox.taskmanager.dto.note.NoteCreateRequest;
import com.fox.taskmanager.dto.note.NoteUpdateRequest;
import com.fox.taskmanager.model.Note;
import java.util.List;

public interface NoteService {

    List<Note> listAll(String login);

    Note add(NoteCreateRequest request, String login);

    void deleteById(Long id, String login);

    void update(NoteUpdateRequest request, String login);

    Note getById(Long id, String login);
}
