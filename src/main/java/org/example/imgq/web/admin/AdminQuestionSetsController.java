package org.example.imgq.web.admin;

import org.example.imgq.domain.Question;
import org.example.imgq.domain.QuestionSet;
import org.example.imgq.repo.QuestionRepository;
import org.example.imgq.repo.QuestionSetRepository;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Controller
@RequestMapping("/admin/question-sets")
public class AdminQuestionSetsController {

    private final QuestionSetRepository sets;
    private final QuestionRepository questions;

    public AdminQuestionSetsController(QuestionSetRepository sets, QuestionRepository questions) {
        this.sets = sets;
        this.questions = questions;
    }

    @GetMapping
    public String list(Model model) {
        model.addAttribute("items", sets.findAll());
        return "admin/qsets/list";
    }

    @GetMapping("/new")
    public String formNew(Model model) {
        model.addAttribute("qset", new QuestionSet());
        return "admin/qsets/form";
    }

    @PostMapping
    public String create(@ModelAttribute QuestionSet qset, Model model) {
        if (qset.getVersion() == null) {
            model.addAttribute("qset", qset);
            model.addAttribute("error", "Version is required (integer)");
            return "admin/qsets/form";
        }
        qset.setCreatedAt(OffsetDateTime.now());
        sets.save(qset);
        return "redirect:/admin/question-sets/" + qset.getVersion() + "/questions";
    }

    @GetMapping("/{version}/edit")
    public String formEdit(@PathVariable Integer version, Model model) {
        model.addAttribute("qset", sets.findById(version).orElseThrow());
        return "admin/qsets/form";
    }

    @PostMapping("/{version}")
    public String update(@PathVariable Integer version, @ModelAttribute QuestionSet qset, Model model) {
        qset.setVersion(version);
        sets.save(qset);
        return "redirect:/admin/question-sets";
    }

    @PostMapping("/{version}/delete")
    public String delete(@PathVariable Integer version, Model model) {
        try {
            sets.deleteById(version);
            return "redirect:/admin/question-sets";
        } catch (DataIntegrityViolationException e) {
            model.addAttribute("items", sets.findAll());
            model.addAttribute("error", "Cannot delete: questions or studies reference this set.");
            return "admin/qsets/list";
        }
    }

    // --- Questions within a set ---

    @GetMapping("/{version}/questions")
    public String listQuestions(@PathVariable Integer version, Model model) {
        model.addAttribute("qset", sets.findById(version).orElseThrow());
        model.addAttribute("items", questions.findByQuestionSetVersionOrderByIndexInSetAsc(version));
        return "admin/qsets/questions";
    }

    @GetMapping("/{version}/questions/new")
    public String formQuestion(@PathVariable Integer version, Model model) {
        Question q = new Question();
        q.setQuestionSetVersion(version);
        model.addAttribute("q", q);
        model.addAttribute("version", version);
        return "admin/qsets/qform";
    }

    @PostMapping("/{version}/questions")
    public String createQuestion(@PathVariable Integer version, @ModelAttribute Question q) {
        q.setQuestionSetVersion(version);
        if (q.getIndexInSet() == null) q.setIndexInSet(1);
        questions.save(q);
        return "redirect:/admin/question-sets/" + version + "/questions";
    }

    @GetMapping("/{version}/questions/{id}/edit")
    public String editQuestion(@PathVariable Integer version, @PathVariable UUID id, Model model) {
        model.addAttribute("q", questions.findById(id).orElseThrow());
        model.addAttribute("version", version);
        return "admin/qsets/qform";
    }

    @PostMapping("/{version}/questions/{id}")
    public String updateQuestion(@PathVariable Integer version, @PathVariable UUID id, @ModelAttribute Question q) {
        q.setId(id);
        q.setQuestionSetVersion(version);
        questions.save(q);
        return "redirect:/admin/question-sets/" + version + "/questions";
    }

    @PostMapping("/{version}/questions/{id}/delete")
    public String deleteQuestion(@PathVariable Integer version, @PathVariable UUID id) {
        questions.deleteById(id);
        return "redirect:/admin/question-sets/" + version + "/questions";
    }
}
