package org.example.imgq.service;

import org.example.imgq.domain.Invitation;
import org.example.imgq.repo.InvitationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.OffsetDateTime;

@Service
public class CodeService {
    private static final String ALPHABET = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789"; // no I, O, 1, 0
    private final SecureRandom rnd = new SecureRandom();
    private final InvitationRepository invitations;

    public CodeService(InvitationRepository invitations) {
        this.invitations = invitations;
    }

    /** e.g. XXXX-XXXX-XXXX */
    public String generatePlainCode() {
        return segment(4) + "-" + segment(4) + "-" + segment(4);
    }

    private String segment(int n) {
        StringBuilder sb = new StringBuilder(n);
        for (int i = 0; i < n; i++) {
            sb.append(ALPHABET.charAt(rnd.nextInt(ALPHABET.length())));
        }
        return sb.toString();
    }

    @Transactional
    public String assignNewCode(Invitation inv) {
        String plain = generatePlainCode();
        inv.setCodeHash(Hashing.sha256Hex(plain));
        if (inv.getFirstLoginAt() == null) {
            inv.setStatus("created");
        }

        if (inv.getCreatedAt() == null) inv.setCreatedAt(OffsetDateTime.now());
        invitations.save(inv);
        return plain;
    }
}
