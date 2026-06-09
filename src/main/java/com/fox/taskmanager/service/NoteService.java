package com.fox.taskmanager.service;

import com.fox.taskmanager.dto.note.NoteCreateRequest;
import com.fox.taskmanager.dto.note.NoteResponse;
import com.fox.taskmanager.dto.note.NoteUpdateRequest;
import java.util.List;

public interface NoteService {

    List<NoteResponse> listAll(String login);

    NoteResponse add(NoteCreateRequest request, String login);

    void deleteById(Long id, String login);

    NoteResponse update(NoteUpdateRequest request, String login);

    NoteResponse getById(Long id, String login);
}
