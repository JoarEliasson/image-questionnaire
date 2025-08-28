package org.example.imgq.domain;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "question")
public class Question {
    @Id
    @GeneratedValue
    @org.hibernate.annotations.UuidGenerator
    private UUID id;

    @Column(name = "question_set_version", nullable = false)
    private Integer questionSetVersion;

    @Column(name = "index_in_set", nullable = false)
    private Integer indexInSet; // 1..15

    @Column(nullable = false)
    private String text;

    @Column(name = "min_label")
    private String minLabel;

    @Column(name = "max_label")
    private String maxLabel;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public Integer getQuestionSetVersion() { return questionSetVersion; }
    public void setQuestionSetVersion(Integer questionSetVersion) { this.questionSetVersion = questionSetVersion; }

    public Integer getIndexInSet() { return indexInSet; }
    public void setIndexInSet(Integer indexInSet) { this.indexInSet = indexInSet; }

    public String getText() { return text; }
    public void setText(String text) { this.text = text; }

    public String getMinLabel() { return minLabel; }
    public void setMinLabel(String minLabel) { this.minLabel = minLabel; }

    public String getMaxLabel() { return maxLabel; }
    public void setMaxLabel(String maxLabel) { this.maxLabel = maxLabel; }
}
