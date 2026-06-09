package com.fox.taskmanager.controller;

import com.fox.taskmanager.config.WebRedirect;
import com.fox.taskmanager.dto.note.NoteCreateRequest;
import com.fox.taskmanager.dto.note.NoteResponse;
import com.fox.taskmanager.dto.note.NoteUpdateRequest;
import com.fox.taskmanager.service.NoteService;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import java.security.Principal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class NoteController {

    private final NoteService noteService;

    public NoteController(NoteService noteService) {
        this.noteService = noteService;
    }

    @GetMapping("/note/list")
    public String listNotes(Model model, Principal principal) {
        model.addAttribute("notes", noteService.listAll(principal.getName()));

        return "note-list";
    }

    @GetMapping("/note/view")
    public String viewNotes(Model model, Principal principal) {
        model.addAttribute("notes", noteService.listAll(principal.getName()));

        return "note-view";
    }

    @GetMapping(value = "/note/view", params = "id")
    public String viewNote(@RequestParam Long id, Model model, Principal principal) {
        model.addAttribute("note", noteService.getById(id, principal.getName()));

        return "note-detail";
    }

    @PostMapping("/note/delete")
    public void deleteNote(
            @RequestParam Long id,
            Principal principal,
            HttpServletResponse response) {
        noteService.deleteById(id, principal.getName());

        WebRedirect.sendRelativeRedirect(response, "/note/list");
    }

    @GetMapping("/note/edit")
    public String editNote(@RequestParam Long id, Model model, Principal principal) {
        NoteResponse note = noteService.getById(id, principal.getName());

        model.addAttribute("note", NoteUpdateRequest.from(note));
        addAuditAttributes(model, note);

        return "note-edit";
    }

    @PostMapping("/note/edit")
    public String updateNote(
            @Valid @ModelAttribute("note") NoteUpdateRequest request,
            BindingResult bindingResult,
            Model model,
            Principal principal,
            HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            NoteResponse note = noteService.getById(request.getId(), principal.getName());

            addAuditAttributes(model, note);

            return "note-edit";
        }

        noteService.update(request, principal.getName());

        WebRedirect.sendRelativeRedirect(response, "/note/list");
        return null;
    }

    @GetMapping("/note/create")
    public String createNotePage(Model model) {
        model.addAttribute("note", new NoteCreateRequest());

        return "note-create";
    }

    @PostMapping("/note/create")
    public String createNote(
            @Valid @ModelAttribute("note") NoteCreateRequest request,
            BindingResult bindingResult,
            Principal principal,
            HttpServletResponse response) {
        if (bindingResult.hasErrors()) {
            return "note-create";
        }

        noteService.add(request, principal.getName());

        WebRedirect.sendRelativeRedirect(response, "/note/list");
        return null;
    }

    private void addAuditAttributes(Model model, NoteResponse note) {
        model.addAttribute("createdAt", note.getCreatedAt());
        model.addAttribute("createdAtUtc", note.getCreatedAtUtc());
        model.addAttribute("updatedAt", note.getUpdatedAt());
        model.addAttribute("updatedAtUtc", note.getUpdatedAtUtc());
    }
}
