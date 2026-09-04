# springboot-api-starter — Project Documentation

**Package:** `com.gurpreet.starter`
**Type:** Reusable Spring Boot starter (JAR library, not a runnable app)
**Depends on:** nothing project-specific — no database, no opinion on what a "User" looks like

This is the foundation layer. It provides generic, reusable building
blocks that any Spring Boot project can plug in via a Maven dependency,
instead of rewriting JWT/security/CORS/exception-handling code from
scratch each time.

---

## 1. Why it exists

Every new backend project needs the same handful of things: JWT token
handling, a Spring Security filter chain, CORS configuration, a
consistent error response shape, and some base request/response
classes. Copy-pasting this across projects means bugs get fixed in one
copy and never make it to the others. This starter packages all of it
as a **versioned dependency** — fix it once here, bump the version,
every consuming project gets the fix.

---

## 2. Package structure

```
com.gurpreet.starter
├── autoconfigure/
│   ├── StarterAutoConfiguration.java
│   ├── JwtProperties.java
│   ├── SecurityStarterProperties.java
│   └── CorsStarterProperties.java
├── config/
│   ├── SecurityConfig.java
│   └── CorsConfig.java
├── security/
│   ├── JwtTokenProvider.java
│   ├── JwtAuthenticationFilter.java
│   ├── JwtAuthenticationEntryPoint.java
│   └── SecurityUtils.java
├── exception/
│   ├── GlobalExceptionHandler.java
│   ├── CustomException.java
│   ├── ResourceNotFoundException.java
│   ├── BadRequestException.java
│   ├── UnauthorizedException.java
│   ├── ForbiddenException.java
│   └── DuplicateResourceException.java
├── response/
│   ├── BaseResponse.java
│   └── PageResponse.java
├── request/
│   ├── BaseRequest.java
│   └── PageableRequest.java
├── validation/
│   ├── ValidEnum.java (+ ValidEnumValidator)
│   └── NotBlankIfPresent.java
└── util/
    ├── DateUtils.java
    ├── StringUtils.java
    └── AppConstants.java
```

---

## 3. How auto-configuration works

`StarterAutoConfiguration` is registered via
`src/main/resources/META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`,
which Spring Boot reads automatically the moment this JAR is on the
classpath — **no manual `@Import` or `@ComponentScan` needed** in the
consuming project.

```java
@AutoConfiguration
@ConditionalOnWebApplication
@EnableConfigurationProperties({
        JwtProperties.class,
        SecurityStarterProperties.class,
        CorsStarterProperties.class
})
@Import({
        CorsConfig.class,
        SecurityConfig.class,
        GlobalExceptionHandler.class,
        JwtTokenProvider.class
})
public class StarterAutoConfiguration { }
```

> **Fixed during integration:** `JwtTokenProvider` was originally
> `@Component`-annotated but not listed in this `@Import`, which meant
> it was never actually registered as a bean (auto-configuration
> classes don't component-scan other packages on their own). Adding it
> to `@Import` fixed a startup failure in `ams-crm`.

Every bean declared here uses `@ConditionalOnMissingBean`, so any
consuming project can override a single piece (e.g. its own
`SecurityFilterChain`) by declaring its own bean — no need to fork this
starter.

---

## 4. Feature breakdown

### 4.1 JWT (`security/` package)

- **`JwtTokenProvider`** — generates access tokens (from an
  `Authentication` or username+roles) and refresh tokens, validates
  tokens, extracts username/roles from a token. Uses `io.jsonwebtoken`
  (JJWT) with HMAC signing.
- **`JwtAuthenticationFilter`** — a `OncePerRequestFilter` that reads
  the `Authorization` header on every request, validates the token, and
  populates `SecurityContextHolder` so `@PreAuthorize`/`hasRole` checks
  work downstream.
- **`JwtAuthenticationEntryPoint`** — returned when an unauthenticated
  request hits a protected endpoint; produces a `BaseResponse` JSON 401
  instead of Spring Security's default blank/HTML page.
- **`SecurityUtils`** — a helper to get the current logged-in username
  anywhere in the app (service layer, audit fields) without wiring
  `HttpServletRequest` around.

Configured via `JwtProperties` (`gurpreet.starter.jwt.*`): secret,
access-token expiry, refresh-token expiry, header name, prefix.

### 4.2 Security (`config/SecurityConfig.java`)

A baseline stateless JWT `SecurityFilterChain`. Requires only ONE bean
from the consuming project: a `UserDetailsService`. Everything else —
`PasswordEncoder` (BCrypt), `AuthenticationManager`,
`DaoAuthenticationProvider`, the filter chain itself — is provided
here, configurable via `SecurityStarterProperties`
(`gurpreet.starter.security.*`): public endpoints list, stateless
session policy, CSRF toggle.

### 4.3 CORS (`config/CorsConfig.java`)

Fully driven by `CorsStarterProperties`
(`gurpreet.starter.cors.*`): allowed origins, methods, headers, exposed
headers, credentials, max-age. No code changes needed per project —
just set properties in `application.yml`.

### 4.4 Global exception handling (`exception/GlobalExceptionHandler.java`)

A `@RestControllerAdvice` that converts every exception into a
consistent `BaseResponse` JSON shape:

| Exception | HTTP Status |
|---|---|
| `CustomException` subclasses | Whatever status the exception specifies |
| `MethodArgumentNotValidException` (bean validation) | 400, with field-level error map |
| `HttpMessageNotReadableException` (malformed JSON) | 400 |
| `MethodArgumentTypeMismatchException` | 400 |
| `DataIntegrityViolationException` (DB constraint) | 409 |
| `BadCredentialsException` | 401 |
| `AccessDeniedException` | 403 |
| `NoHandlerFoundException` | 404 |
| Any other uncaught `Exception` | 500, generic message (no stack trace leaked) |

Custom exception hierarchy (all extend `CustomException`, each carrying
its own `HttpStatus`): `ResourceNotFoundException`,
`BadRequestException`, `UnauthorizedException`, `ForbiddenException`,
`DuplicateResourceException`.

### 4.5 Response/Request base classes

- **`BaseResponse<T>`** — standard envelope: `success`, `message`,
  `statusCode`, `data`, `timestamp`, optional `errors`. Static factories
  `success(...)` / `error(...)`.
- **`PageResponse<T>`** — wraps a Spring `Page<T>` into a flat, clean
  pagination shape.
- **`BaseRequest`** — marker base for request DTOs (currently carries
  an optional `requestId` for tracing).
- **`PageableRequest`** — base for paginated list/search request DTOs
  (`page`, `size`, `sortBy`, `sortDirection`).

### 4.6 Validation (`validation/` package)

- **`@ValidEnum`** — validates a `String` field against an enum's
  allowed values.
- **`@NotBlankIfPresent`** — for optional PATCH-style fields: allows
  `null`, rejects blank/empty if the field IS supplied.

### 4.7 Utilities (`util/` package)

- **`DateUtils`** — format/parse `LocalDate`/`LocalDateTime`, date-range checks.
- **`StringUtils`** — email/Indian-mobile validation, blank checks, UUID/random string generation, string masking.
- **`AppConstants`** — shared constants (default page size, date formats, role prefix).

---

## 5. Configuration reference

```yaml
gurpreet:
  starter:
    jwt:
      secret: ${JWT_SECRET}
      expiration-ms: 3600000
      refresh-expiration-ms: 604800000
      header: Authorization
      prefix: "Bearer "
    security:
      public-endpoints:
        - /api/auth/**
        - /swagger-ui/**
        - /v3/api-docs/**
        - /actuator/health
      stateless: true
      csrf-disabled: true
    cors:
      allowed-origins: ["http://localhost:4200"]
      allowed-methods: [GET, POST, PUT, PATCH, DELETE, OPTIONS]
      allowed-headers: ["*"]
      exposed-headers: ["Authorization"]
      allow-credentials: true
      max-age-seconds: 3600
```

---

## 6. How a consuming project uses this

1. Add as a Maven dependency
2. Set the properties above in `application.yml`
3. Implement `UserDetailsService` against your own User entity
4. Write your own login controller (or use `springboot-auth-starter`,
   which builds a full login/refresh/logout flow on top of this)

See `README.md` for the full walkthrough with code examples.

---

## 7. Known issue log

| Issue | Root cause | Fix |
|---|---|---|
| `JwtTokenProvider` bean not found in consuming projects | Not listed in `StarterAutoConfiguration`'s `@Import` | Added `JwtTokenProvider.class` to `@Import` |

---

## 8. Current status

✅ Compiles and installs cleanly (`mvn clean install`)
✅ Verified working end-to-end via `ams-crm` (login, JWT validation, CORS, exception handling all confirmed in practice)
✅ Version `1.0.0`, consumed by `springboot-auth-starter`
