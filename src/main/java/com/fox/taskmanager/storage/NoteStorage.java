package com.fox.taskmanager.storage;

import com.fox.taskmanager.model.Note;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class NoteStorage {

    private final Map<Long, Note> notes = new HashMap<>();

    public NoteStorage() {
        notes.put(1L, new Note(1L, "First note", "This is the first test note"));
        notes.put(2L, new Note(2L, "Second note", "This is the second test note"));
        notes.put(3L, new Note(3L, "Third note", "This is the third test note"));
    }

    public Map<Long, Note> getNotes() {
        return notes;
    }
}
