package org.example.imgq.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.constraints.NotBlank;
import org.example.imgq.config.AppProperties;
import org.example.imgq.domain.Invitation;
import org.example.imgq.repo.InvitationRepository;
import org.example.imgq.repo.ParticipantProfileRepository;
import org.example.imgq.service.AuditService;
import org.example.imgq.service.Hashing;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.Optional;

@Controller
public class AccessController {

    private final InvitationRepository invitations;
    private final ParticipantProfileRepository profiles;
    private final AppProperties props;
    private final AuditService audit;

    public AccessController(InvitationRepository invitations,
                            ParticipantProfileRepository profiles,
                            AppProperties props,
                            AuditService audit) {
        this.invitations = invitations;
        this.profiles = profiles;
        this.props = props;
        this.audit = audit;
    }

    @GetMapping("/access")
    public String accessPage() {
        return "access";
    }

    @PostMapping("/access")
    public String submitCode(HttpServletRequest request,
                             @RequestParam("code") @NotBlank String code,
                             Model model) {
        String trimmed = code.trim();
        if (trimmed.isEmpty()) {
            model.addAttribute("error", "Please enter your access code.");
            return "access";
        }

        String hash = Hashing.sha256Hex(trimmed);
        Optional<Invitation> opt = invitations.findByCodeHash(hash);
        if (opt.isEmpty()) {
            model.addAttribute("error", "Invalid or expired code.");
            return "access";
        }

        Invitation inv = opt.get();

        // Basic expiry check if set
        if (inv.getExpiresAt() != null && inv.getExpiresAt().isBefore(OffsetDateTime.now())) {
            model.addAttribute("error", "This code has expired.");
            return "access";
        }

        // Mark first login timestamp
        if (inv.getFirstLoginAt() == null) {
            inv.setFirstLoginAt(OffsetDateTime.now());
            if (!"completed".equalsIgnoreCase(inv.getStatus())) {
                inv.setStatus("in_progress");
            }
            invitations.save(inv);
        }

        request.getSession(true).setAttribute(ParticipantSessionInterceptor.SESSION_INVITATION_ID, inv.getId());

        audit.participant(request, inv.getId().toString(), "login");

        if (profiles.existsByInvitationId(inv.getId())) {
            return "redirect:/study/start";
        } else {
            return "redirect:/pre-survey";
        }
    }
}
