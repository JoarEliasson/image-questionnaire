package org.example.imgq.service;

import jakarta.mail.internet.MimeMessage;
import org.example.imgq.config.AppProperties;
import org.example.imgq.domain.Invitation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import java.nio.charset.StandardCharsets;
import java.net.URLEncoder;
import java.time.OffsetDateTime;
import java.util.Locale;

@Service
public class EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine thymeleaf;
    private final AppProperties appProps;

    @Value("${SMTP_FROM:}")
    private String fromAddress;

    public EmailService(JavaMailSender mailSender, SpringTemplateEngine thymeleaf, AppProperties appProps) {
        this.mailSender = mailSender;
        this.thymeleaf = thymeleaf;
        this.appProps = appProps;
    }

    public String sendInvitationEmail(Invitation inv, String plainCode) throws Exception {
        String accessUrl = appProps.getDomain() + "/access?code=" +
                URLEncoder.encode(plainCode, StandardCharsets.UTF_8);

        Context ctx = new Context(Locale.getDefault());
        ctx.setVariable("code", plainCode);
        ctx.setVariable("accessUrl", accessUrl);
        ctx.setVariable("domain", appProps.getDomain());
        String html = thymeleaf.process("mail/invitation", ctx);

        String plain = """
                Hello,

                Your access code is: %s

                Visit: %s

                If the link doesn't work, open %s and paste the code.

                Best regards
                """.formatted(plainCode, accessUrl, appProps.getDomain() + "/access");

        MimeMessage msg = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(msg, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8");
        helper.setTo(inv.getEmail());
        if (fromAddress != null && !fromAddress.isBlank()) {
            helper.setFrom(fromAddress);
        }
        helper.setSubject("Your access code");
        helper.setText(plain, html);

        mailSender.send(msg);

        inv.setStatus("sent");
        inv.setSentAt(OffsetDateTime.now());
        inv.setLastSendResult("OK");
        return "OK";
    }
}
