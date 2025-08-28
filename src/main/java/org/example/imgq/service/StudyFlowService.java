package org.example.imgq.service;

import org.example.imgq.domain.*;
import org.example.imgq.repo.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.*;

@Service
public class StudyFlowService {

    public static final int QUESTIONS_PER_IMAGE = 15;
    public static final int SLIDER_MIN = 1;
    public static final int SLIDER_MAX = 8;

    private final InvitationRepository invitations;
    private final StudyRepository studies;
    private final ImageRepository images;
    private final QuestionRepository questions;
    private final ResponseRepository responses;

    public StudyFlowService(InvitationRepository invitations,
                            StudyRepository studies,
                            ImageRepository images,
                            QuestionRepository questions,
                            ResponseRepository responses) {
        this.invitations = invitations;
        this.studies = studies;
        this.images = images;
        this.questions = questions;
        this.responses = responses;
    }

    public static record CurrentStep(
            boolean completed,
            int imageOrdinal, // 1-based
            int totalImages,
            int questionIndex, // 1..15
            Image image,
            Question question
    ) {}

    public UUID requireStudyId(UUID invitationId) {
        Invitation inv = invitations.findById(invitationId)
                .orElseThrow(() -> new IllegalStateException("Invitation not found"));
        return inv.getStudyId();
    }

    public CurrentStep computeCurrent(UUID invitationId) {
        Invitation inv = invitations.findById(invitationId)
                .orElseThrow(() -> new IllegalStateException("Invitation not found"));
        UUID studyId = inv.getStudyId();

        List<Image> imgList = images.findByStudyIdOrderByOrderIndexAsc(studyId);
        if (imgList.isEmpty()) {
            throw new IllegalStateException("No images configured for this study");
        }

        Study study = studies.findById(studyId)
                .orElseThrow(() -> new IllegalStateException("Study not found"));
        List<Question> qs = questions.findByQuestionSetVersionOrderByIndexInSetAsc(study.getQuestionSetVersion());
        if (qs.size() != QUESTIONS_PER_IMAGE) {
            throw new IllegalStateException("Expected 15 questions for question set version " + study.getQuestionSetVersion());
        }

        for (int i = 0; i < imgList.size(); i++) {
            Image img = imgList.get(i);
            long answered = responses.countByInvitationIdAndImageId(invitationId, img.getId());
            if (answered < QUESTIONS_PER_IMAGE) {
                int qIndex = (int) answered + 1; // 1..15
                Question q = qs.get(qIndex - 1);
                return new CurrentStep(false, i + 1, imgList.size(), qIndex, img, q);
            }
        }

        return new CurrentStep(true, imgList.size(), imgList.size(), QUESTIONS_PER_IMAGE, imgList.get(imgList.size() - 1), qs.get(QUESTIONS_PER_IMAGE - 1));
    }

    @Transactional
    public void submitAnswer(UUID invitationId, int value, Integer timeMs) {
        if (value < SLIDER_MIN || value > SLIDER_MAX) {
            throw new IllegalArgumentException("Value out of range");
        }

        Invitation inv = invitations.findById(invitationId)
                .orElseThrow(() -> new IllegalStateException("Invitation not found"));
        UUID studyId = inv.getStudyId();

        CurrentStep step = computeCurrent(invitationId);
        if (step.completed()) {
            return;
        }

        if (responses.existsByInvitationIdAndImageIdAndQuestionIndex(invitationId, step.image().getId(), step.questionIndex())) {
            throw new IllegalStateException("This question already answered");
        }

        Response r = new Response();
        r.setInvitationId(invitationId);
        r.setStudyId(studyId);
        r.setImageId(step.image().getId());
        r.setQuestionId(step.question().getId());
        r.setQuestionIndex(step.questionIndex());
        r.setValue(value);
        r.setAnsweredAt(OffsetDateTime.now());
        r.setTimeMs(timeMs);
        responses.save(r);

        CurrentStep after = computeCurrent(invitationId);
        if (after.completed()) {
            inv.setStatus("completed");
            inv.setCompletedAt(OffsetDateTime.now());
            invitations.save(inv);
        }
    }
}
