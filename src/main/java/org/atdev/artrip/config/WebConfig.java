package org.atdev.artrip.config;

import lombok.RequiredArgsConstructor;
import org.atdev.artrip.global.resolver.LoginUserIdArgumentResolver;
import org.atdev.artrip.interceptor.MaintenanceInterceptor;
import org.atdev.artrip.interceptor.RequestLoggingInterceptor;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
@EnableConfigurationProperties({MaintenanceAccessProperties.class, CookieProperties.class, CorsProperties.class})
public class WebConfig implements WebMvcConfigurer {

    private final LoginUserIdArgumentResolver userIdArgumentResolver;
    private final MaintenanceInterceptor maintenanceInterceptor;
    private final RequestLoggingInterceptor requestLoggingInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(requestLoggingInterceptor).addPathPatterns("/**");
        registry.addInterceptor(maintenanceInterceptor).addPathPatterns("/**");
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(userIdArgumentResolver);
    }

}