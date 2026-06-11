package com.fox.taskmanager.controller;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fox.taskmanager.dto.note.NoteCreateRequest;
import com.fox.taskmanager.dto.note.NoteResponse;
import com.fox.taskmanager.dto.note.NoteUpdateRequest;
import com.fox.taskmanager.service.NoteService;
import java.security.Principal;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

@ExtendWith(MockitoExtension.class)
class NoteRestControllerTest {

    private static final String OWNER_LOGIN = "owner";

    @Mock
    private NoteService noteService;

    @InjectMocks
    private NoteRestController controller;

    @Test
    void createNoteReturnsCreatedResponseAndLocation() {
        NoteCreateRequest request = createRequest();
        NoteResponse createdNote = createResponse(15L);

        when(noteService.add(request, OWNER_LOGIN)).thenReturn(createdNote);

        ResponseEntity<NoteResponse> response = controller.createNote(request, principal());

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getHeaders().getLocation().toString())
                .isEqualTo("/api/notes/15");
        assertThat(response.getBody()).isSameAs(createdNote);
    }

    @Test
    void updateNoteUsesIdFromPathAndCurrentUser() {
        NoteCreateRequest request = createRequest();
        ArgumentCaptor<NoteUpdateRequest> captor = ArgumentCaptor.forClass(NoteUpdateRequest.class);

        controller.updateNote(33L, request, principal());

        verify(noteService).update(captor.capture(), eq(OWNER_LOGIN));
        assertThat(captor.getValue().getId()).isEqualTo(33L);
        assertThat(captor.getValue().getTitle()).isEqualTo(request.getTitle());
        assertThat(captor.getValue().getContent()).isEqualTo(request.getContent());
    }

    private Principal principal() {
        return () -> OWNER_LOGIN;
    }

    private NoteCreateRequest createRequest() {
        NoteCreateRequest request = new NoteCreateRequest();

        request.setTitle("Api title");
        request.setContent("Api content");

        return request;
    }

    private NoteResponse createResponse(Long id) {
        NoteResponse response = new NoteResponse();

        response.setId(id);
        response.setTitle("Api title");
        response.setContent("Api content");

        return response;
    }
}
