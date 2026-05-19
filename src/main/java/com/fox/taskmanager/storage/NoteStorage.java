package com.fox.taskmanager.storage;

import com.fox.taskmanager.model.Note;
import java.util.HashMap;
import java.util.Map;
import org.springframework.stereotype.Component;

@Component
public class NoteStorage {

    private final Map<Long, Note> notes = new HashMap<>();

    public Map<Long, Note> getNotes() {
        return notes;
    }
}
