package org.example.imgq.config;

import org.example.imgq.web.ParticipantSessionInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    private final ParticipantSessionInterceptor participantSessionInterceptor;

    public WebConfig(ParticipantSessionInterceptor participantSessionInterceptor) {
        this.participantSessionInterceptor = participantSessionInterceptor;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(participantSessionInterceptor)
                .addPathPatterns("/pre-survey/**", "/study/**");
    }
}
