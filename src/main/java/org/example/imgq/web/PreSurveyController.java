package org.example.imgq.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import org.example.imgq.config.AppProperties;
import org.example.imgq.domain.ParticipantProfile;
import org.example.imgq.repo.ParticipantProfileRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Map;
import java.util.UUID;

@Controller
public class PreSurveyController {

    private final AppProperties props;
    private final ParticipantProfileRepository profiles;

    public PreSurveyController(AppProperties props, ParticipantProfileRepository profiles) {
        this.props = props;
        this.profiles = profiles;
    }

    @GetMapping("/pre-survey")
    public String preSurvey(HttpServletRequest request, Model model) {
        model.addAttribute("ages", props.getPreSurvey().getAgeBands());
        model.addAttribute("sexes", props.getPreSurvey().getSexOptions());
        return "pre-survey";
    }

    @PostMapping("/pre-survey")
    public String submit(HttpServletRequest request,
                         @RequestParam("age_band") @NotBlank String ageBand,
                         @RequestParam("sex") @NotBlank String sex,
                         Model model) {

        if (!props.getPreSurvey().getAgeBands().contains(ageBand)
                || !props.getPreSurvey().getSexOptions().contains(sex)) {
            model.addAttribute("ages", props.getPreSurvey().getAgeBands());
            model.addAttribute("sexes", props.getPreSurvey().getSexOptions());
            model.addAttribute("error", "Invalid selection values");
            return "pre-survey";
        }

        UUID invitationId = (UUID) request.getSession().getAttribute(ParticipantSessionInterceptor.SESSION_INVITATION_ID);
        if (invitationId == null) {
            return "redirect:/access?e=nosession";
        }

        Map<String, Object> attrs = new java.util.HashMap<>();
        attrs.put("age_band", ageBand);
        attrs.put("sex", sex);

        if (!profiles.existsByInvitationId(invitationId)) {
            ParticipantProfile pp = new ParticipantProfile();
            pp.setInvitationId(invitationId);
            pp.setAttributes(attrs);
            pp.setCreatedAt(java.time.OffsetDateTime.now());
            profiles.save(pp);
        }

        return "redirect:/study/start";
    }

}
