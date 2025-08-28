package org.example.imgq.web.admin;

import org.example.imgq.domain.Study;
import org.example.imgq.repo.StudyRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
@RequestMapping("/admin/studies")
public class AdminStudiesController {

    private final StudyRepository studies;

    public AdminStudiesController(StudyRepository studies) {
        this.studies = studies;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("items", studies.findAll());
        return "admin/studies/list";
    }

    @GetMapping("/new")
    public String createForm(Model model) {
        model.addAttribute("study", new Study());
        return "admin/studies/form";
    }

    @PostMapping
    public String create(@ModelAttribute Study study, Model model) {
        if (study.getId() == null) {
            study.setId(UUID.randomUUID());
        }
        if (study.getQuestionSetVersion() == 0) {
            model.addAttribute("study", study);
            model.addAttribute("error", "questionSetVersion is required (e.g., 1)");
            return "admin/studies/form";
        }
        studies.save(study);
        return "redirect:/admin/studies";
    }

    @GetMapping("/{id}/edit")
    public String editForm(@PathVariable UUID id, Model model) {
        model.addAttribute("study", studies.findById(id).orElseThrow());
        return "admin/studies/form";
    }

    @PostMapping("/{id}")
    public String update(@PathVariable UUID id, @ModelAttribute Study study, Model model) {
        study.setId(id);
        if (study.getQuestionSetVersion() == 0) {
            model.addAttribute("study", study);
            model.addAttribute("error", "questionSetVersion is required");
            return "admin/studies/form";
        }
        studies.save(study);
        return "redirect:/admin/studies";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable UUID id, Model model) {
        try {
            studies.deleteById(id);
            return "redirect:/admin/studies";
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("items", studies.findAll());
            model.addAttribute("error", "Cannot delete: study in use.");
            return "admin/studies/list";
        }
    }
}
