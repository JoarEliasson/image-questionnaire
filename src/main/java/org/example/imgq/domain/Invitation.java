package org.example.imgq.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;
import java.util.UUID;

@Entity
@Table(name = "invitation")
public class Invitation {
    @Id
    @GeneratedValue
    private UUID id;

    @Column(name = "study_id", nullable = false)
    private UUID studyId;

    @Column(nullable = false)
    private String email;

    @Column(name = "code_hash")
    private String codeHash;

    @Column(nullable = false)
    private String status; // created | sent | started | completed | expired | bounced

    @Column(name = "created_at", nullable = false)
    private OffsetDateTime createdAt;

    @Column(name = "sent_at")
    private OffsetDateTime sentAt;

    @Column(name = "first_login_at")
    private OffsetDateTime firstLoginAt;

    @Column(name = "completed_at")
    private OffsetDateTime completedAt;

    @Column(name = "expires_at")
    private OffsetDateTime expiresAt;

    @Column(name = "last_send_result")
    private String lastSendResult;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getStudyId() { return studyId; }
    public void setStudyId(UUID studyId) { this.studyId = studyId; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getCodeHash() { return codeHash; }
    public void setCodeHash(String codeHash) { this.codeHash = codeHash; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public OffsetDateTime getSentAt() { return sentAt; }
    public void setSentAt(OffsetDateTime sentAt) { this.sentAt = sentAt; }

    public OffsetDateTime getFirstLoginAt() { return firstLoginAt; }
    public void setFirstLoginAt(OffsetDateTime firstLoginAt) { this.firstLoginAt = firstLoginAt; }

    public OffsetDateTime getCompletedAt() { return completedAt; }
    public void setCompletedAt(OffsetDateTime completedAt) { this.completedAt = completedAt; }

    public OffsetDateTime getExpiresAt() { return expiresAt; }
    public void setExpiresAt(OffsetDateTime expiresAt) { this.expiresAt = expiresAt; }

    public String getLastSendResult() { return lastSendResult; }
    public void setLastSendResult(String lastSendResult) { this.lastSendResult = lastSendResult; }
}
