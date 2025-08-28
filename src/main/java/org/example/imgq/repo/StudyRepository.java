package org.example.imgq.repo;

import org.example.imgq.domain.Study;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface StudyRepository extends JpaRepository<Study, UUID> { }
