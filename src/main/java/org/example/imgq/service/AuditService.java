package org.example.imgq.service;

import jakarta.servlet.http.HttpServletRequest;
import org.example.imgq.domain.AuthLog;
import org.example.imgq.repo.AuthLogRepository;
import org.springframework.stereotype.Service;

import java.time.OffsetDateTime;

@Service
public class AuditService {
    private final AuthLogRepository repo;

    public AuditService(AuthLogRepository repo) { this.repo = repo; }

    public void admin(HttpServletRequest req, String username, String event) {
        save(req, "ADMIN", username, event);
    }

    public void participant(HttpServletRequest req, String invitationId, String event) {
        save(req, "PARTICIPANT", invitationId, event);
    }

    private void save(HttpServletRequest req, String type, String id, String event) {
        try {
            AuthLog al = new AuthLog();
            al.setActorType(type);
            al.setActorId(id);
            al.setEvent(event);
            al.setIp(ip(req));
            al.setUserAgent(req != null ? req.getHeader("User-Agent") : null);
            al.setAt(OffsetDateTime.now());
            repo.save(al);
        } catch (Exception ignored) { }
    }

    private String ip(HttpServletRequest req) {
        if (req == null) return null;
        String xff = req.getHeader("X-Forwarded-For");
        return (xff != null && !xff.isBlank()) ? xff.split(",")[0].trim() : req.getRemoteAddr();
    }
}
