package org.example.imgq.web;

import jakarta.servlet.http.HttpServletRequest;
import org.example.imgq.service.StudyFlowService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@Controller
public class StudyController {

    private final StudyFlowService flow;

    public StudyController(StudyFlowService flow) {
        this.flow = flow;
    }

    @GetMapping("/study/start")
    public String start() {
        return "redirect:/study/current";
    }

    @GetMapping("/study/current")
    public String current(HttpServletRequest request, Model model) {
        UUID invitationId = (UUID) request.getSession().getAttribute(ParticipantSessionInterceptor.SESSION_INVITATION_ID);
        var step = flow.computeCurrent(invitationId);
        if (step.completed()) {
            return "redirect:/study/done";
        }
        model.addAttribute("imageUrl", step.image().getStoragePath());
        model.addAttribute("questionText", step.question().getText());
        model.addAttribute("minLabel", step.question().getMinLabel());
        model.addAttribute("maxLabel", step.question().getMaxLabel());
        model.addAttribute("imageOrdinal", step.imageOrdinal());
        model.addAttribute("totalImages", step.totalImages());
        model.addAttribute("questionIndex", step.questionIndex());
        model.addAttribute("questionsPerImage", StudyFlowService.QUESTIONS_PER_IMAGE);
        model.addAttribute("sliderMin", StudyFlowService.SLIDER_MIN);
        model.addAttribute("sliderMax", StudyFlowService.SLIDER_MAX);
        return "study/current";
    }

    @PostMapping("/responses")
    public String submit(HttpServletRequest request,
                         @RequestParam("value") int value,
                         @RequestParam(value = "timeMs", required = false) Integer timeMs) {
        UUID invitationId = (UUID) request.getSession().getAttribute(ParticipantSessionInterceptor.SESSION_INVITATION_ID);
        flow.submitAnswer(invitationId, value, timeMs);
        return "redirect:/study/current";
    }

    @GetMapping("/study/done")
    public String done() {
        return "study/done";
    }
}
