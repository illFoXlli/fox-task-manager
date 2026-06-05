package com.fox.taskmanager.service;

import com.fox.taskmanager.exception.NoteNotFoundException;
import com.fox.taskmanager.model.Note;
import com.fox.taskmanager.repository.NoteRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;

    public NoteServiceImpl(NoteRepository noteRepository) {
        this.noteRepository = noteRepository;
    }

    @Override
    public List<Note> listAll() {
        return noteRepository.findAll();
    }

    @Override
    public Note add(Note note) {
        return noteRepository.save(note);
    }

    @Override
    public void deleteById(long id) {
        if (!noteRepository.existsById(id)) {
            throw new NoteNotFoundException("Note with id "
                    + id
                    + " not found");
        }

        noteRepository.deleteById(id);
    }

    @Override
    public void update(Note note) {
        Note existingNote = getById(note.getId());
        existingNote.setTitle(note.getTitle());
        existingNote.setContent(note.getContent());

        noteRepository.save(existingNote);
    }

    @Override
    public Note getById(long id) {
        return noteRepository.findById(id)
                .orElseThrow(() -> new NoteNotFoundException("Note with id "
                        + id
                        + " not found"));
    }
}
