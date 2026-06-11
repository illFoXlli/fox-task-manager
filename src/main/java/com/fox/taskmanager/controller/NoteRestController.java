package com.fox.taskmanager.controller;

import com.fox.taskmanager.config.OpenApiConfig;
import com.fox.taskmanager.dto.note.NoteCreateRequest;
import com.fox.taskmanager.dto.note.NoteResponse;
import com.fox.taskmanager.dto.note.NoteUpdateRequest;
import com.fox.taskmanager.service.NoteService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.net.URI;
import java.security.Principal;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/notes")
@Tag(name = "Notes", description = "REST API for current user's notes")
@SecurityRequirement(name = OpenApiConfig.COOKIE_AUTH_SCHEME)
public class NoteRestController {

    private final NoteService noteService;

    public NoteRestController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping
    @Operation(summary = "Get current user's notes")
    @ApiResponse(responseCode = "200", description = "Notes returned")
    @ApiResponse(responseCode = "401", description = "Auth required", content = @Content)
    public List<NoteResponse> listNotes(@Parameter(hidden = true) Principal principal) {
        return noteService.listAll(principal.getName());
    }

    @GetMapping("/{id}")
    @Operation(summary = "Get one note by id")
    @ApiResponse(responseCode = "200", description = "Note returned")
    @ApiResponse(responseCode = "404", description = "Note was not found", content = @Content)
    public NoteResponse getNote(
            @PathVariable Long id,
            @Parameter(hidden = true) Principal principal) {
        return noteService.getById(id, principal.getName());
    }

    @PostMapping
    @Operation(summary = "Create note")
    @ApiResponse(responseCode = "201", description = "Note created")
    @ApiResponse(responseCode = "400", description = "Request body is invalid", content = @Content)
    public ResponseEntity<NoteResponse> createNote(
            @Valid @RequestBody NoteCreateRequest request,
            @Parameter(hidden = true) Principal principal) {
        NoteResponse response = noteService.add(request, principal.getName());

        return ResponseEntity
                .created(URI.create("/api/notes/" + response.getId()))
                .body(response);
    }

    @PutMapping("/{id}")
    @Operation(summary = "Update note")
    @ApiResponse(responseCode = "200", description = "Note updated")
    @ApiResponse(responseCode = "404", description = "Note was not found", content = @Content)
    public NoteResponse updateNote(
            @PathVariable Long id,
            @Valid @RequestBody NoteCreateRequest request,
            @Parameter(hidden = true) Principal principal) {
        return noteService.update(toUpdateRequest(id, request), principal.getName());
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Delete note")
    @ApiResponse(responseCode = "204", description = "Note deleted")
    @ApiResponse(responseCode = "404", description = "Note was not found")
    public ResponseEntity<Void> deleteNote(
            @PathVariable Long id,
            @Parameter(hidden = true) Principal principal) {
        noteService.deleteById(id, principal.getName());

        return ResponseEntity.noContent().build();
    }

    private NoteUpdateRequest toUpdateRequest(Long id, NoteCreateRequest request) {
        NoteUpdateRequest updateRequest = new NoteUpdateRequest();

        updateRequest.setId(id);
        updateRequest.setTitle(request.getTitle());
        updateRequest.setContent(request.getContent());

        return updateRequest;
    }
}
