package org.example.imgq;

import org.example.imgq.config.AppProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication
@EnableConfigurationProperties(AppProperties.class)
public class ImageQuestionnaireApplication {
    public static void main(String[] args) {
        SpringApplication.run(ImageQuestionnaireApplication.class, args);
    }
}
