package org.example.imgq.repo;

import org.example.imgq.domain.Image;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

public interface ImageRepository extends JpaRepository<Image, UUID> {
    List<Image> findByStudyIdOrderByOrderIndexAsc(UUID studyId);
}
