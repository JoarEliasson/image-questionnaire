package org.example.imgq.web;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.example.imgq.service.AuditService;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class AdminAuthHandlers implements AuthenticationSuccessHandler, LogoutSuccessHandler {

    private final AuditService audit;

    public AdminAuthHandlers(AuditService audit) { this.audit = audit; }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        audit.admin(request, authentication.getName(), "login");
        response.sendRedirect("/admin");
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
        String name = (authentication != null ? authentication.getName() : null);
        audit.admin(request, name, "logout");
        response.sendRedirect("/admin/login?logout");
    }
}
