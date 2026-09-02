# springboot-api-starter

Reusable Spring Boot starter for `com.gurpreet.starter` projects. Provides, out of the box:

- **JWT authentication** — token generation/validation (`JwtTokenProvider`), request filter, custom entry point
- **Spring Security** — stateless filter chain, wired to your own `UserDetailsService`
- **CORS** — fully configurable via `application.yml`, no code changes needed per project
- **Global exception handling** — consistent JSON error shape for validation errors, custom business exceptions, DB constraint violations, 401/403, and any uncaught exception
- **`BaseResponse<T>` / `PageResponse<T>`** — standard API response envelope
- **`BaseRequest` / `PageableRequest`** — standard request DTO base classes
- **Validation helpers** — `@ValidEnum`, `@NotBlankIfPresent`
- **Common utils** — `DateUtils`, `StringUtils`, `AppConstants`

Everything is wired via Spring Boot's auto-configuration mechanism
(`META-INF/spring/org.springframework.boot.autoconfigure.AutoConfiguration.imports`),
so adding this as a dependency is enough — nothing to `@Import` or `@ComponentScan` manually.

## 1. Install / publish

```bash
mvn clean install          # local ~/.m2 use (fastest way to try it in another local project)
mvn clean deploy           # publish to GitHub Packages / Nexus / Artifactory (see distributionManagement in pom.xml)
```

## 2. Add as a dependency in your new project

```xml
<dependency>
    <groupId>com.gurpreet.starter</groupId>
    <artifactId>springboot-api-starter</artifactId>
    <version>1.0.0</version>
</dependency>
```

## 3. Configure `application.yml`

Copy the block from `src/main/resources/application-sample.yml` into your project and
set your own JWT secret and allowed CORS origins. **Never commit a real secret** — pull
it from an environment variable in production:

```yaml
gurpreet:
  starter:
    jwt:
      secret: ${JWT_SECRET}
```

## 4. Provide the one bean the starter needs from you

The starter does NOT know how your users are stored — you supply a `UserDetailsService`:

```java
@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        return org.springframework.security.core.userdetails.User.builder()
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(user.getRoles().stream()
                        .map(r -> "ROLE_" + r.getName())
                        .toArray(String[]::new))
                .build();
    }
}
```

That's it — the `SecurityFilterChain`, `JwtAuthenticationFilter`, `PasswordEncoder`,
`AuthenticationManager`, CORS filter, and `GlobalExceptionHandler` are all auto-registered.

## 5. Write your login endpoint

The starter deliberately does NOT ship a `/api/auth/login` controller (login request/response
shape differs per project — CRM vs e-commerce vs internal tool). Your controller just needs to:

```java
@PostMapping("/api/auth/login")
public ResponseEntity<BaseResponse<String>> login(@RequestBody @Valid LoginRequest req) {
    Authentication auth = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(req.getUsername(), req.getPassword()));
    String token = jwtTokenProvider.generateAccessToken(auth);
    return ResponseEntity.ok(BaseResponse.success(token, "Login successful", 200));
}
```

Add `/api/auth/**` to `gurpreet.starter.security.public-endpoints` (already there by default).

## 6. Overriding anything

Every bean in this starter is `@ConditionalOnMissingBean`. If a project needs a custom
`SecurityFilterChain`, just declare your own `SecurityFilterChain` bean in that project —
it wins automatically, nothing to delete from the starter.

## Versioning

Use semantic versioning (`MAJOR.MINOR.PATCH`). Bug fixes / non-breaking additions → patch/minor
bump. Breaking changes to method signatures or property keys → major bump, and note it in
`CHANGELOG.md`.

## Package structure

```
com.gurpreet.starter
 ├── autoconfigure   → AutoConfiguration entry point + @ConfigurationProperties
 ├── config          → SecurityConfig, CorsConfig
 ├── security        → JwtTokenProvider, JwtAuthenticationFilter, JwtAuthenticationEntryPoint, SecurityUtils
 ├── exception       → GlobalExceptionHandler + CustomException hierarchy
 ├── response        → BaseResponse<T>, PageResponse<T>
 ├── request         → BaseRequest, PageableRequest
 ├── validation      → @ValidEnum, @NotBlankIfPresent
 └── util            → DateUtils, StringUtils, AppConstants
```
