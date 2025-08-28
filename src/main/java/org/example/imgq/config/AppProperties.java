package org.example.imgq.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.List;

@ConfigurationProperties(prefix = "app")
public class AppProperties {
    private String domain;
    private int codeTtlMinutes;
    private PreSurvey preSurvey = new PreSurvey();

    public static class PreSurvey {
        private List<String> ageBands;
        private List<String> sexOptions;

        public List<String> getAgeBands() { return ageBands; }
        public void setAgeBands(List<String> ageBands) { this.ageBands = ageBands; }

        public List<String> getSexOptions() { return sexOptions; }
        public void setSexOptions(List<String> sexOptions) { this.sexOptions = sexOptions; }
    }

    public String getDomain() { return domain; }
    public void setDomain(String domain) { this.domain = domain; }

    public int getCodeTtlMinutes() { return codeTtlMinutes; }
    public void setCodeTtlMinutes(int codeTtlMinutes) { this.codeTtlMinutes = codeTtlMinutes; }

    public PreSurvey getPreSurvey() { return preSurvey; }
    public void setPreSurvey(PreSurvey preSurvey) { this.preSurvey = preSurvey; }
}
