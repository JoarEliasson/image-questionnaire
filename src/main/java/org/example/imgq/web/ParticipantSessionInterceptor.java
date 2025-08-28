package org.example.imgq.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.imgq.repo.ParticipantProfileRepository;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.UUID;

@Component
public class ParticipantSessionInterceptor implements HandlerInterceptor {

    public static final String SESSION_INVITATION_ID = "INVITATION_ID";
    private final ParticipantProfileRepository profiles;

    public ParticipantSessionInterceptor(ParticipantProfileRepository profiles) {
        this.profiles = profiles;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();
        if (path.startsWith("/pre-survey") || path.startsWith("/study")) {
            var session = request.getSession(false);
            Object attr = session == null ? null : session.getAttribute(SESSION_INVITATION_ID);
            if (attr == null) {
                response.sendRedirect("/access?e=nosession");
                return false;
            }
            if (path.startsWith("/study")) {
                UUID invitationId = (UUID) attr;
                boolean hasProfile = profiles.existsByInvitationId(invitationId);
                if (!hasProfile) {
                    response.sendRedirect("/pre-survey");
                    return false;
                }
            }
        }
        return true;
    }
}
