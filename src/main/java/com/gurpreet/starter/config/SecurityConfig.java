package com.gurpreet.starter.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gurpreet.starter.autoconfigure.JwtProperties;
import com.gurpreet.starter.autoconfigure.SecurityStarterProperties;
import com.gurpreet.starter.security.JwtAuthenticationEntryPoint;
import com.gurpreet.starter.security.JwtAuthenticationFilter;
import com.gurpreet.starter.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.filter.CorsFilter;

/**
 * Baseline stateless JWT security chain.
 *
 * Consuming project only needs to:
 *   1. Provide a UserDetailsService bean (load user from its own DB).
 *   2. List any extra public endpoints under gurpreet.starter.security.public-endpoints.
 *
 * Every bean here is @ConditionalOnMissingBean, so a project can override
 * any single piece (e.g. its own SecurityFilterChain) without forking this class.
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtProperties jwtProperties;
    private final SecurityStarterProperties securityProperties;
    private final JwtTokenProvider jwtTokenProvider;
    private final ObjectMapper objectMapper;
    private final CorsFilter corsFilter;

    @Bean
    @ConditionalOnMissingBean(PasswordEncoder.class)
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    @ConditionalOnMissingBean(AuthenticationManager.class)
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    @Bean
    @ConditionalOnMissingBean(DaoAuthenticationProvider.class)
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService userDetailsService,
                                                              PasswordEncoder passwordEncoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(userDetailsService);
        provider.setPasswordEncoder(passwordEncoder);
        return provider;
    }

    @Bean
    @ConditionalOnMissingBean(JwtAuthenticationEntryPoint.class)
    public JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint() {
        return new JwtAuthenticationEntryPoint(objectMapper);
    }

    @Bean
    @ConditionalOnMissingBean(JwtAuthenticationFilter.class)
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtTokenProvider, jwtProperties);
    }

    @Bean
    @ConditionalOnMissingBean(SecurityFilterChain.class)
    public SecurityFilterChain filterChain(HttpSecurity http,
                                            JwtAuthenticationEntryPoint entryPoint,
                                            JwtAuthenticationFilter jwtFilter) throws Exception {

        if (securityProperties.isCsrfDisabled()) {
            http.csrf(AbstractHttpConfigurer::disable);
        }

        String[] publicEndpoints = securityProperties.getPublicEndpoints().toArray(new String[0]);

        http
            .cors(cors -> {}) // uses the CorsFilter bean registered below
            .addFilter(corsFilter)
            .exceptionHandling(ex -> ex.authenticationEntryPoint(entryPoint))
            .sessionManagement(sm -> sm.sessionCreationPolicy(
                    securityProperties.isStateless() ? SessionCreationPolicy.STATELESS : SessionCreationPolicy.IF_REQUIRED))
            .authorizeHttpRequests(auth -> auth
                    .requestMatchers(publicEndpoints).permitAll()
                    .anyRequest().authenticated())
            .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
}
