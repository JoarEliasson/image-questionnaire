package org.example.imgq.service;

import org.example.imgq.domain.Invitation;
import org.example.imgq.repo.InvitationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Optional;
import java.util.Set;

@Service
public class InvitationService {
    private static final Set<String> ACTIVE_STATUSES = Set.of("created","sent","started");
    private final InvitationRepository invitations;

    public InvitationService(InvitationRepository invitations) {
        this.invitations = invitations;
    }

    @Transactional
    public Optional<Invitation> verifyAndStart(String rawCode) {
        String canonical = canonicalize(rawCode);
        String hash = Hashing.sha256Hex(canonical);
        Optional<Invitation> opt = invitations.findByCodeHash(hash);
        if (opt.isEmpty()) return Optional.empty();
        Invitation inv = opt.get();
        if (!ACTIVE_STATUSES.contains(inv.getStatus())) return Optional.empty();
        if (inv.getExpiresAt() != null && inv.getExpiresAt().isBefore(OffsetDateTime.now())) return Optional.empty();

        if (inv.getFirstLoginAt() == null) inv.setFirstLoginAt(OffsetDateTime.now());
        inv.setStatus("started");
        invitations.save(inv);
        return Optional.of(inv);
    }

    public static String canonicalize(String code) {
        return code == null ? "" : code.trim();
    }
}
