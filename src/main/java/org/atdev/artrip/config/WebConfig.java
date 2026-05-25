package org.atdev.artrip.config;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.global.resolver.LoginUserIdArgumentResolver;
import org.atdev.artrip.interceptor.MaintenanceInterceptor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties(MaintenanceAccessProperties.class)
public class WebConfig implements WebMvcConfigurer {

    private final LoginUserIdArgumentResolver userIdArgumentResolver;
    private final MaintenanceInterceptor maintenanceInterceptor;

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins("http://localhost:3000", "http://localhost:8080", "https://dev.08166.dev", "http://localhost:5173")
                .allowedMethods("OPTIONS","GET", "POST", "PUT", "PATCH", "DELETE")
                .allowCredentials(true);
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(userIdArgumentResolver);
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(maintenanceInterceptor).addPathPatterns("/**");
    }

}