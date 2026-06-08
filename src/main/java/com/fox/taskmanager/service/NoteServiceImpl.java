package com.fox.taskmanager.service;

import com.fox.taskmanager.config.AppConstants;
import com.fox.taskmanager.dto.note.NoteCreateRequest;
import com.fox.taskmanager.dto.note.NoteUpdateRequest;
import com.fox.taskmanager.exception.NoteNotFoundException;
import com.fox.taskmanager.model.Note;
import com.fox.taskmanager.model.UserProfile;
import com.fox.taskmanager.repository.NoteRepository;
import com.fox.taskmanager.repository.UserProfileRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class NoteServiceImpl implements NoteService {

    private final NoteRepository noteRepository;
    private final UserProfileRepository userProfileRepository;

    public NoteServiceImpl(
            NoteRepository noteRepository,
            UserProfileRepository userProfileRepository) {
        this.noteRepository = noteRepository;
        this.userProfileRepository = userProfileRepository;
    }

    @Override
    public List<Note> listAll(String login) {
        return noteRepository.findAllByUserProfileLoginOrderByUpdatedAtDesc(login);
    }

    @Override
    public Note add(NoteCreateRequest request, String login) {
        UserProfile userProfile = getUserProfile(login);
        Note note = new Note();

        note.setTitle(cleanText(request.getTitle()));
        note.setContent(cleanText(request.getContent()));
        note.setUserProfile(userProfile);

        return noteRepository.save(note);
    }

    @Override
    public void deleteById(Long id, String login) {
        Note note = getById(id, login);

        noteRepository.delete(note);
    }

    @Override
    public void update(NoteUpdateRequest request, String login) {
        Note existingNote = getById(request.getId(), login);

        existingNote.setTitle(cleanText(request.getTitle()));
        existingNote.setContent(cleanText(request.getContent()));

        noteRepository.save(existingNote);
    }

    @Override
    public Note getById(Long id, String login) {
        return noteRepository.findByIdAndUserProfileLogin(id, login)
                .orElseThrow(() -> new NoteNotFoundException(
                        AppConstants.Note.NOT_FOUND_MESSAGE_PREFIX + id));
    }

    private UserProfile getUserProfile(String login) {
        return userProfileRepository.findByLogin(login)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + login));
    }

    private String cleanText(String value) {
        if (value == null) {
            return "";
        }

        return value.trim();
    }
}
