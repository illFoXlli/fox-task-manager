package com.fox.taskmanager.dto.note;

import com.fox.taskmanager.config.AppConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class NoteUpdateRequest {

    @NotNull
    private Long id;

    @NotBlank
    @Size(max = AppConstants.Note.TITLE_MAX_LENGTH)
    private String title;

    @NotBlank
    @Size(max = AppConstants.Note.CONTENT_MAX_LENGTH)
    private String content;

    public static NoteUpdateRequest from(NoteResponse note) {
        NoteUpdateRequest request = new NoteUpdateRequest();

        request.setId(note.getId());
        request.setTitle(note.getTitle());
        request.setContent(note.getContent());

        return request;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }
}
