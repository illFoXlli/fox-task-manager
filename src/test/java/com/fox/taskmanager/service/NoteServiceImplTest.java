package com.fox.taskmanager.service;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fox.taskmanager.dto.note.NoteUpdateRequest;
import com.fox.taskmanager.exception.NoteNotFoundException;
import com.fox.taskmanager.model.Note;
import com.fox.taskmanager.repository.NoteRepository;
import com.fox.taskmanager.repository.UserProfileRepository;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class NoteServiceImplTest {

    private static final long NOTE_ID = 10L;
    private static final String OWNER_LOGIN = "owner";

    @Mock
    private NoteRepository noteRepository;

    @Mock
    private UserProfileRepository userProfileRepository;

    @InjectMocks
    private NoteServiceImpl noteService;

    @Test
    void updateThrowsWhenNoteDoesNotBelongToCurrentUser() {
        NoteUpdateRequest request = createUpdateRequest();

        when(noteRepository.findByIdAndUserProfileLogin(NOTE_ID, OWNER_LOGIN))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> noteService.update(request, OWNER_LOGIN))
                .isInstanceOf(NoteNotFoundException.class);

        verify(noteRepository, never()).save(any(Note.class));
    }

    @Test
    void deleteThrowsWhenNoteDoesNotBelongToCurrentUser() {
        when(noteRepository.findByIdAndUserProfileLogin(NOTE_ID, OWNER_LOGIN))
                .thenReturn(Optional.empty());

        assertThatThrownBy(() -> noteService.deleteById(NOTE_ID, OWNER_LOGIN))
                .isInstanceOf(NoteNotFoundException.class);

        verify(noteRepository, never()).delete(any(Note.class));
    }

    private NoteUpdateRequest createUpdateRequest() {
        NoteUpdateRequest request = new NoteUpdateRequest();

        request.setId(NOTE_ID);
        request.setTitle("Updated title");
        request.setContent("Updated content");

        return request;
    }
}
