package com.fox.taskmanager.service;

import com.fox.taskmanager.exception.NoteNotFoundException;
import com.fox.taskmanager.model.Note;
import com.fox.taskmanager.storage.NoteStorage;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class InMemoryNoteService implements NoteService {

    private final NoteStorage noteStorage;
    private long nextId = 1L;

    public InMemoryNoteService(NoteStorage noteStorage) {
        this.noteStorage = noteStorage;
    }

    @Override
    public List<Note> listAll() {
        return new ArrayList<>(noteStorage.getNotes().values());
    }

    @Override
    public Note add(Note note) {
        long id = generateId();
        note.setId(id);
        noteStorage.getNotes().put(id, note);
        return note;
    }

    @Override
    public void deleteById(long id) {
        Map<Long, Note> notes = noteStorage.getNotes();
        if (!notes.containsKey(id)) {
            throw new NoteNotFoundException("Note with id "
                    + id
                    + " not found");
        }
        notes.remove(id);
    }

    @Override
    public void update(Note note) {
        Map<Long, Note> notes = noteStorage.getNotes();
        if (!notes.containsKey(note.getId())) {
            throw new NoteNotFoundException("Note with id "
                    + note.getId()
                    + " not found");
        }
        Note existingNote = notes.get(note.getId());
        existingNote.setTitle(note.getTitle());
        existingNote.setContent(note.getContent());
    }

    @Override
    public Note getById(long id) {
        Note note = noteStorage.getNotes().get(id);
        if (note == null) {
            throw new NoteNotFoundException("Note with id "
                    + id
                    + " not found");
        }
        return note;
    }

    private long generateId() {
        return nextId++;
    }
}
