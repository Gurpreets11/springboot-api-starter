package com.gurpreet.starter.security;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gurpreet.starter.response.BaseResponse;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

import java.io.IOException;

/**
 * Returned whenever an unauthenticated request hits a protected endpoint.
 * Produces the same BaseResponse JSON shape as everything else, instead of
 * Spring Security's default HTML/blank 401 page.
 */
@RequiredArgsConstructor
public class JwtAuthenticationEntryPoint implements AuthenticationEntryPoint {

    private final ObjectMapper objectMapper;

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response,
                          AuthenticationException authException) throws IOException, ServletException {
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        response.setContentType(MediaType.APPLICATION_JSON_VALUE);
        BaseResponse<Object> body = BaseResponse.error(
                "Authentication required or token is invalid/expired", HttpStatus.UNAUTHORIZED.value());
        objectMapper.writeValue(response.getWriter(), body);
    }
}
