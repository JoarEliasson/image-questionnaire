package org.example.imgq.web.admin;

import jakarta.servlet.http.HttpServletResponse;
import org.example.imgq.domain.Invitation;
import org.example.imgq.domain.ParticipantProfile;
import org.example.imgq.domain.Response;
import org.example.imgq.repo.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/admin/exports")
public class AdminExportsController {

    private final ResponseRepository responses;
    private final InvitationRepository invitations;
    private final ParticipantProfileRepository profiles;
    private final ImageRepository images;
    private final QuestionRepository questions;

    public AdminExportsController(ResponseRepository responses, InvitationRepository invitations,
                                  ParticipantProfileRepository profiles, ImageRepository images,
                                  QuestionRepository questions) {
        this.responses = responses;
        this.invitations = invitations;
        this.profiles = profiles;
        this.images = images;
        this.questions = questions;
    }

    @GetMapping("/responses.csv")
    public void exportResponses(HttpServletResponse resp) throws IOException {
        resp.setContentType("text/csv");
        resp.setHeader("Content-Disposition", "attachment; filename=responses.csv");
        List<Response> rows = responses.findAll();

        // Preload lookups to avoid N+1
        Map<UUID, Invitation> invMap = invitations.findAllById(rows.stream().map(Response::getInvitationId).collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(Invitation::getId, i -> i));
        Map<UUID, String> imgLabel = images.findAllById(rows.stream().map(Response::getImageId).collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(i -> i.getId(), i -> Optional.ofNullable(i.getLabel()).orElse("")));
        Map<UUID, String> qText = questions.findAllById(rows.stream().map(Response::getQuestionId).collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(q -> q.getId(), q -> q.getText()));

        try (PrintWriter w = resp.getWriter()) {
            w.println("invitation_id,email,study_id,image_id,image_label,question_id,question_index,question_text,value,answered_at,time_ms");
            for (Response r : rows) {
                Invitation inv = invMap.get(r.getInvitationId());
                w.printf(Locale.ROOT,
                        "%s,%s,%s,%s,%s,%s,%d,%s,%d,%s,%s%n",
                        r.getInvitationId(),
                        csv(inv != null ? inv.getEmail() : ""),
                        r.getStudyId(),
                        r.getImageId(),
                        csv(imgLabel.getOrDefault(r.getImageId(), "")),
                        r.getQuestionId(),
                        r.getQuestionIndex(),
                        csv(qText.getOrDefault(r.getQuestionId(), "")),
                        r.getValue(),
                        r.getAnsweredAt(),
                        r.getTimeMs() == null ? "" : r.getTimeMs().toString()
                );
            }
        }
    }

    @GetMapping("/invitations.csv")
    public void exportInvitations(HttpServletResponse resp) throws IOException {
        resp.setContentType("text/csv");
        resp.setHeader("Content-Disposition", "attachment; filename=invitations.csv");
        List<Invitation> rows = invitations.findAll();
        try (PrintWriter w = resp.getWriter()) {
            w.println("id,study_id,email,status,created_at,sent_at,first_login_at,completed_at,expires_at");
            for (Invitation i : rows) {
                w.printf(Locale.ROOT, "%s,%s,%s,%s,%s,%s,%s,%s%n",
                        i.getId(), i.getStudyId(), csv(i.getEmail()), i.getStatus(),
                        i.getCreatedAt(), i.getSentAt(), i.getFirstLoginAt(), i.getCompletedAt(), i.getExpiresAt());
            }
        }
    }

    @GetMapping("/pre-survey.csv")
    public void exportPreSurvey(HttpServletResponse resp) throws IOException {
        resp.setContentType("text/csv");
        resp.setHeader("Content-Disposition", "attachment; filename=pre_survey.csv");
        List<ParticipantProfile> rows = profiles.findAll();

        // Fetch emails for join
        Map<UUID, String> emailByInv = invitations.findAllById(
                        rows.stream().map(ParticipantProfile::getInvitationId).collect(Collectors.toSet()))
                .stream().collect(Collectors.toMap(Invitation::getId, Invitation::getEmail));

        try (PrintWriter w = resp.getWriter()) {
            w.println("invitation_id,email,attributes_json,created_at");
            for (ParticipantProfile p : rows) {
                String email = emailByInv.getOrDefault(p.getInvitationId(), "");
                w.printf(Locale.ROOT, "%s,%s,%s,%s%n",
                        p.getInvitationId(),
                        csv(email),
                        csv(p.getAttributes().toString()),
                        p.getCreatedAt());
            }
        }
    }

    private static String csv(String s) {
        if (s == null) return "";
        String t = s.replace("\"", "\"\"");
        if (t.contains(",") || t.contains("\"") || t.contains("\n")) {
            return "\"" + t + "\"";
        }
        return t;
    }
}
