# Changelog

All notable changes to this project are documented in this file.

The format follows [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/).

## [Unreleased]
### Added
- `RateLimitFilter` — in-memory per-IP token bucket rate limiter (Bucket4j), ordered to run before Spring Security's filter chain
- `RateLimitProperties` — default limit, excluded paths, and a stricter shared limit for `strict-paths` (e.g. `/api/auth/login`, `/api/auth/register`), to slow down brute-force attempts
- `bucket4j-core` dependency
- `RequestLoggingFilter` with MDC-based request ID and current-user tracing
- `LoggingProperties` — toggle logging on/off, exclude specific paths
- `OpenApiConfig` — Swagger/OpenAPI setup with a working JWT "Authorize" button, activates only if `springdoc-openapi` is on the consuming project's classpath
- `SwaggerProperties` — per-project title/description/version/contact
- Sample `logback-spring.xml` with an MDC-aware log pattern
- `spring-boot-starter-actuator` dependency

### Fixed
- `StarterAutoConfiguration` was missing `JwtTokenProvider` in its `@Import`, causing `UnsatisfiedDependencyException` in every consuming project
- Default `public-endpoints` was missing `/swagger-ui.html` and `/webjars/**`, causing a 401 on Swagger UI even though `/swagger-ui/**` was public

### Notes
- Rate-limit per-path override config deliberately uses a `List<String>` (`strict-paths`) rather than a `Map<String, X>` keyed by path — Spring Boot's YAML binding has a known issue with map keys containing slashes (silently fails to bind, falls back to defaults with no error)

## [1.0.0] - 2026-09-02
### Added
- `JwtTokenProvider`, `JwtAuthenticationFilter` and `JwtAuthenticationEntryPoint` for stateless JWT authentication
- `SecurityConfig` with configurable public endpoints and pluggable `UserDetailsService`
- `CorsConfig` with externalized allowed origins/methods/headers via application properties
- `GlobalExceptionHandler` with unified `BaseResponse` error format for validation, security and generic exceptions
- `CustomException` hierarchy: `ResourceNotFoundException`, `BadRequestException`, `UnauthorizedException`, `ForbiddenException`, `DuplicateResourceException`
- `BaseResponse` and `PageResponse` as standard API response envelopes
- `BaseRequest` and `PageableRequest` as standard request DTO base classes
- `@ValidEnum` and `@NotBlankIfPresent` custom validation annotations
- `DateUtils`, `StringUtils` and `AppConstants` common utilities
- `StarterAutoConfiguration` with Spring Boot auto-configuration registration for zero-config consumption
- `README.md` with setup, configuration and usage instructions