package com.fox.taskmanager.dto.note;

import com.fox.taskmanager.model.Note;
import com.fox.taskmanager.support.AppTime;
import java.time.LocalDateTime;

public class NoteResponse {

    private Long id;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private String createdAtUtc;
    private LocalDateTime updatedAt;
    private String updatedAtUtc;

    public static NoteResponse from(Note note) {
        NoteResponse response = new NoteResponse();

        response.setId(note.getId());
        response.setTitle(note.getTitle());
        response.setContent(note.getContent());
        response.setCreatedAt(note.getCreatedAt());
        response.setCreatedAtUtc(AppTime.toUtcString(note.getCreatedAt()));
        response.setUpdatedAt(note.getUpdatedAt());
        response.setUpdatedAtUtc(AppTime.toUtcString(note.getUpdatedAt()));

        return response;
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

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getCreatedAtUtc() {
        return createdAtUtc;
    }

    public void setCreatedAtUtc(String createdAtUtc) {
        this.createdAtUtc = createdAtUtc;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public String getUpdatedAtUtc() {
        return updatedAtUtc;
    }

    public void setUpdatedAtUtc(String updatedAtUtc) {
        this.updatedAtUtc = updatedAtUtc;
    }
}
