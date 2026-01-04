package org.atdev.artrip.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000", "http://localhost:8080", "https://dev.coffit.today")
                .allowedMethods("OPTIONS","GET", "POST", "PUT", "PATCH", "DELETE")
                .allowCredentials(true);
    }
}