package org.example.imgq.config;

import org.example.imgq.web.AdminAuthHandlers;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    @Value("${ADMIN_USERNAME:admin}")
    private String adminUser;

    @Value("${ADMIN_PASSWORD:admin}")
    private String adminPass;

    private final AdminAuthHandlers adminHandlers;

    public SecurityConfig(AdminAuthHandlers adminHandlers) {
        this.adminHandlers = adminHandlers;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public UserDetailsService users(PasswordEncoder encoder) {
        return new InMemoryUserDetailsManager(
                User.withUsername(adminUser)
                        .password(encoder.encode(adminPass))
                        .roles("ADMIN")
                        .build()
        );
    }

    @Bean
    public SecurityFilterChain security(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.ignoringRequestMatchers("/access", "/pre-survey", "/responses"))

                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/", "/access", "/login", "/pre-survey", "/study/**",
                                "/images/**", "/uploads/**", "/css/**").permitAll()
                        .requestMatchers("/admin/login").permitAll()
                        .requestMatchers("/admin/**").hasRole("ADMIN")
                        .anyRequest().permitAll()
                )

                .formLogin(form -> form
                        .loginPage("/admin/login")
                        .successHandler(adminHandlers)
                        .permitAll()
                )

                .logout(logout -> logout
                        .logoutUrl("/admin/logout")
                        .logoutSuccessHandler(adminHandlers)
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                        .permitAll()
                )

                .headers(Customizer.withDefaults());

        return http.build();
    }
}
