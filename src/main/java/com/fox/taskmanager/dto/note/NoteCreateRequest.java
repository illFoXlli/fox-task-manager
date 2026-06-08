package com.fox.taskmanager.dto.note;

import com.fox.taskmanager.config.AppConstants;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class NoteCreateRequest {

    @NotBlank
    @Size(max = AppConstants.Note.TITLE_MAX_LENGTH)
    private String title;

    @NotBlank
    @Size(max = AppConstants.Note.CONTENT_MAX_LENGTH)
    private String content;

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
