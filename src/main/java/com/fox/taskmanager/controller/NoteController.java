package com.fox.taskmanager.controller;

import com.fox.taskmanager.dto.note.NoteCreateRequest;
import com.fox.taskmanager.dto.note.NoteUpdateRequest;
import com.fox.taskmanager.model.Note;
import com.fox.taskmanager.service.NoteService;
import com.fox.taskmanager.support.AppTime;
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
    public String deleteNote(@RequestParam Long id, Principal principal) {
        noteService.deleteById(id, principal.getName());

        return "redirect:/note/list";
    }

    @GetMapping("/note/edit")
    public String editNote(@RequestParam Long id, Model model, Principal principal) {
        Note note = noteService.getById(id, principal.getName());

        model.addAttribute("note", NoteUpdateRequest.from(note));
        model.addAttribute("createdAt", note.getCreatedAt());
        model.addAttribute("createdAtUtc", AppTime.toUtcString(note.getCreatedAt()));
        model.addAttribute("updatedAt", note.getUpdatedAt());
        model.addAttribute("updatedAtUtc", AppTime.toUtcString(note.getUpdatedAt()));

        return "note-edit";
    }

    @PostMapping("/note/edit")
    public String updateNote(
            @Valid @ModelAttribute("note") NoteUpdateRequest request,
            BindingResult bindingResult,
            Model model,
            Principal principal) {
        if (bindingResult.hasErrors()) {
            Note note = noteService.getById(request.getId(), principal.getName());

            model.addAttribute("createdAt", note.getCreatedAt());
            model.addAttribute("createdAtUtc", AppTime.toUtcString(note.getCreatedAt()));
            model.addAttribute("updatedAt", note.getUpdatedAt());
            model.addAttribute("updatedAtUtc", AppTime.toUtcString(note.getUpdatedAt()));

            return "note-edit";
        }

        noteService.update(request, principal.getName());

        return "redirect:/note/list";
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
            Principal principal) {
        if (bindingResult.hasErrors()) {
            return "note-create";
        }

        noteService.add(request, principal.getName());

        return "redirect:/note/list";
    }
}
