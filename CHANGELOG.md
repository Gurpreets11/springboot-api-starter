# Changelog

All notable changes to this project are documented in this file.

The format follows [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/).

## [Unreleased]

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
