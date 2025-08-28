package org.example.imgq.web.admin;

import org.example.imgq.domain.Invitation;
import org.example.imgq.repo.InvitationRepository;
import org.example.imgq.repo.StudyRepository;
import org.example.imgq.service.CodeService;
import org.example.imgq.service.EmailService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.OffsetDateTime;
import java.util.UUID;

@Controller
@RequestMapping("/admin/studies/{studyId}/invitations")
public class AdminInvitationsController {

    private final InvitationRepository invitations;
    private final StudyRepository studies;
    private final CodeService codes;
    private final EmailService email;

    public AdminInvitationsController(InvitationRepository invitations, StudyRepository studies,
                                      CodeService codes, EmailService email) {
        this.invitations = invitations;
        this.studies = studies;
        this.codes = codes;
        this.email = email;
    }

    @GetMapping
    public String list(@PathVariable UUID studyId, Model model) {
        model.addAttribute("study", studies.findById(studyId).orElseThrow());
        model.addAttribute("items", invitations.findAll().stream().filter(i -> i.getStudyId().equals(studyId)).toList());
        return "admin/invitations/list";
    }

    @GetMapping("/new")
    public String form(@PathVariable UUID studyId, Model model) {
        Invitation inv = new Invitation();
        inv.setStudyId(studyId);
        inv.setStatus("created");
        model.addAttribute("inv", inv);
        model.addAttribute("studyId", studyId);
        return "admin/invitations/form";
    }

    @PostMapping
    public String create(@PathVariable UUID studyId, @ModelAttribute Invitation inv) {
        inv.setStudyId(studyId);
        inv.setStatus("created");
        inv.setCreatedAt(OffsetDateTime.now());
        invitations.save(inv);
        return "redirect:/admin/studies/" + studyId + "/invitations";
    }

    @PostMapping("/{id}/expire")
    public String expire(@PathVariable UUID studyId, @PathVariable UUID id) {
        Invitation inv = invitations.findById(id).orElseThrow();
        inv.setExpiresAt(OffsetDateTime.now());
        inv.setStatus("expired");
        invitations.save(inv);
        return "redirect:/admin/studies/" + studyId + "/invitations";
    }

    @PostMapping("/{id}/generate-code")
    public String generateCode(@PathVariable UUID studyId, @PathVariable UUID id, Model model) {
        Invitation inv = invitations.findById(id).orElseThrow();
        String code = codes.assignNewCode(inv);
        model.addAttribute("studyId", studyId);
        model.addAttribute("inv", inv);
        model.addAttribute("code", code);
        return "admin/invitations/code";
    }

    @PostMapping("/{id}/send-email")
    public String sendEmail(@PathVariable UUID studyId, @PathVariable UUID id, Model model) {
        Invitation inv = invitations.findById(id).orElseThrow();
        String code = codes.assignNewCode(inv);
        String result;
        try {
            result = email.sendInvitationEmail(inv, code);
        } catch (Exception ex) {
            result = "ERROR: " + ex.getMessage();
            inv.setLastSendResult(result);
            invitations.save(inv);
        }
        model.addAttribute("studyId", studyId);
        model.addAttribute("inv", inv);
        model.addAttribute("code", code);
        model.addAttribute("sendResult", result);
        return "admin/invitations/sent";
    }

    @PostMapping("/{id}/delete")
    public String delete(@PathVariable UUID studyId, @PathVariable UUID id) {
        invitations.deleteById(id);
        return "redirect:/admin/studies/" + studyId + "/invitations";
    }
}
