package com.example.cv_reranking.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.nio.file.Path;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Value("${file.upload-dir}")
    private String uploadDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String profileImagePath = Path.of(uploadDir, "profile")
                .toUri()
                .toString();

        registry.addResourceHandler("/uploads/profile/**")
                .addResourceLocations(profileImagePath);
    }
}