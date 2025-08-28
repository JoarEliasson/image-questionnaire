package org.example.imgq.domain;

import jakarta.persistence.*;
import java.util.UUID;

@Entity
@Table(name = "image")
public class Image {
    @Id
    @GeneratedValue
    @org.hibernate.annotations.UuidGenerator
    private UUID id;

    @Column(name = "study_id", nullable = false)
    private UUID studyId;

    @Column(name = "storage_path", nullable = false)
    private String storagePath;

    @Column(name = "order_index", nullable = false)
    private int orderIndex;

    @Column
    private String label;

    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getStudyId() { return studyId; }
    public void setStudyId(UUID studyId) { this.studyId = studyId; }

    public String getStoragePath() { return storagePath; }
    public void setStoragePath(String storagePath) { this.storagePath = storagePath; }

    public int getOrderIndex() { return orderIndex; }
    public void setOrderIndex(int orderIndex) { this.orderIndex = orderIndex; }

    public String getLabel() { return label; }
    public void setLabel(String label) { this.label = label; }

}
