package org.example.imgq.repo;

import org.example.imgq.domain.ParticipantProfile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface ParticipantProfileRepository extends JpaRepository<ParticipantProfile, UUID> {
    Optional<ParticipantProfile> findByInvitationId(UUID invitationId);
    boolean existsByInvitationId(UUID invitationId);
}
