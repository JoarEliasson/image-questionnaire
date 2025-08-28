package org.example.imgq.repo;

import org.example.imgq.domain.Response;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.UUID;

public interface ResponseRepository extends JpaRepository<Response, UUID> {
    long countByInvitationIdAndImageId(UUID invitationId, UUID imageId);
    boolean existsByInvitationIdAndImageIdAndQuestionIndex(UUID invitationId, UUID imageId, Integer questionIndex);
    long countByInvitationId(UUID invitationId);
}
