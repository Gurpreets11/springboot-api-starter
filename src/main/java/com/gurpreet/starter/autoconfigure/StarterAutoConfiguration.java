package com.gurpreet.starter.autoconfigure;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gurpreet.starter.config.CorsConfig;
import com.gurpreet.starter.config.OpenApiConfig;
import com.gurpreet.starter.config.SecurityConfig;
import com.gurpreet.starter.exception.GlobalExceptionHandler;
import com.gurpreet.starter.logging.RequestLoggingFilter;
import com.gurpreet.starter.ratelimit.RateLimitFilter;

import com.gurpreet.starter.security.JwtTokenProvider;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.condition.ConditionalOnWebApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;

/**
 * Single entry point that Spring Boot picks up automatically (via
 * META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports)
 * the moment this starter is added as a dependency.
 *
 * Nothing needs to be manually @Import'ed or @ComponentScan'ed in the
 * consuming application - just add the Maven/Gradle dependency and set
 * the gurpreet.starter.* properties you care about.
 */
@AutoConfiguration
@ConditionalOnWebApplication
@EnableConfigurationProperties({
        JwtProperties.class,
        SecurityStarterProperties.class,
        CorsStarterProperties.class,
        LoggingProperties.class,
        SwaggerProperties.class,
        RateLimitProperties.class
})
@Import({
        CorsConfig.class,
        SecurityConfig.class,
        GlobalExceptionHandler.class,
        JwtTokenProvider.class,
        OpenApiConfig.class
})
public class StarterAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(RequestLoggingFilter.class)
    @ConditionalOnProperty(prefix = "gurpreet.starter.logging", name = "enabled", havingValue = "true", matchIfMissing = true)
    public RequestLoggingFilter requestLoggingFilter(LoggingProperties loggingProperties) {
        return new RequestLoggingFilter(loggingProperties);
    }
    
    @Bean
    @ConditionalOnMissingBean(RateLimitFilter.class)
    @ConditionalOnProperty(prefix = "gurpreet.starter.rate-limit", name = "enabled", havingValue = "true", matchIfMissing = true)
    public RateLimitFilter rateLimitFilter(RateLimitProperties rateLimitProperties, ObjectMapper objectMapper) {
        return new RateLimitFilter(rateLimitProperties, objectMapper);
    }
}
