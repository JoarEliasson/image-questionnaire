package org.example.imgq.web;

import jakarta.servlet.http.HttpServletRequest;
import org.example.imgq.domain.Invitation;
import org.example.imgq.repo.ParticipantProfileRepository;
import org.example.imgq.service.InvitationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class AuthController {

    private final InvitationService invitationService;
    private final ParticipantProfileRepository profiles;

    public AuthController(InvitationService invitationService, ParticipantProfileRepository profiles) {
        this.invitationService = invitationService;
        this.profiles = profiles;
    }

    @PostMapping("/auth/login")
    public String login(@RequestParam("code") String code, HttpServletRequest request, Model model) {
        Optional<Invitation> opt = invitationService.verifyAndStart(code);
        if (opt.isEmpty()) {
            model.addAttribute("error", "Invalid or expired code");
            return "access";
        }
        var inv = opt.get();
        request.getSession(true).setAttribute(ParticipantSessionInterceptor.SESSION_INVITATION_ID, inv.getId());
        boolean hasProfile = profiles.existsByInvitationId(inv.getId());
        return hasProfile ? "redirect:/study/start" : "redirect:/pre-survey";
    }
}
