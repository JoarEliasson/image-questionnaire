package org.example.imgq.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "response")
public class Response {
    @Id
    @GeneratedValue
    @org.hibernate.annotations.UuidGenerator
    private UUID id;

    @Column(name = "invitation_id", nullable = false)
    private UUID invitationId;

    @Column(name = "study_id", nullable = false)
    private UUID studyId;

    @Column(name = "image_id", nullable = false)
    private UUID imageId;

    @Column(name = "question_id", nullable = false)
    private UUID questionId;

    @Column(name = "question_index", nullable = false)
    private Integer questionIndex; // 1..15

    @Column(name = "value", nullable = false)
    private Integer value; // 1..8

    @Column(name = "answered_at", nullable = false)
    private OffsetDateTime answeredAt;

    @Column(name = "time_ms")
    private Integer timeMs;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getInvitationId() { return invitationId; }
    public void setInvitationId(UUID invitationId) { this.invitationId = invitationId; }

    public UUID getStudyId() { return studyId; }
    public void setStudyId(UUID studyId) { this.studyId = studyId; }

    public UUID getImageId() { return imageId; }
    public void setImageId(UUID imageId) { this.imageId = imageId; }

    public UUID getQuestionId() { return questionId; }
    public void setQuestionId(UUID questionId) { this.questionId = questionId; }

    public Integer getQuestionIndex() { return questionIndex; }
    public void setQuestionIndex(Integer questionIndex) { this.questionIndex = questionIndex; }

    public Integer getValue() { return value; }
    public void setValue(Integer value) { this.value = value; }

    public OffsetDateTime getAnsweredAt() { return answeredAt; }
    public void setAnsweredAt(OffsetDateTime answeredAt) { this.answeredAt = answeredAt; }

    public Integer getTimeMs() { return timeMs; }
    public void setTimeMs(Integer timeMs) { this.timeMs = timeMs; }
}
