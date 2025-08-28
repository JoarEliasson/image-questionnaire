package org.example.imgq.domain;

import jakarta.persistence.*;
import java.time.OffsetDateTime;

@Entity
@Table(name = "question_set")
public class QuestionSet {
    @Id
    private Integer version;

    @Column(nullable = false)
    private String title;

    @Column(name = "created_at")
    private OffsetDateTime createdAt;

    @Column(name = "is_locked", nullable = false)
    private boolean locked;

    public Integer getVersion() { return version; }
    public void setVersion(Integer version) { this.version = version; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public OffsetDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }

    public boolean isLocked() { return locked; }
    public void setLocked(boolean locked) { this.locked = locked; }
}
