package org.example.imgq.domain;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "study")
public class Study {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    @Column
    private String description;

    @Column(name = "question_set_version", nullable = false)
    private int questionSetVersion;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getQuestionSetVersion() { return questionSetVersion; }
    public void setQuestionSetVersion(int questionSetVersion) { this.questionSetVersion = questionSetVersion; }
}
