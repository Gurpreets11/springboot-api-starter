package com.gurpreet.starter.config;

import com.gurpreet.starter.autoconfigure.JwtProperties;
import com.gurpreet.starter.autoconfigure.SwaggerProperties;
import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Only activates if springdoc-openapi is on the consuming project's
 * classpath (@ConditionalOnClass) - this starter does not force every
 * project to pull in Swagger. To enable, the consuming project just adds:
 *
 *   <dependency>
 *       <groupId>org.springdoc</groupId>
 *       <artifactId>springdoc-openapi-starter-webmvc-ui</artifactId>
 *       <version>2.6.0</version>
 *   </dependency>
 *
 * Swagger UI will be at /swagger-ui.html, with a working "Authorize"
 * button wired to the same JWT header/prefix as the rest of the app.
 */
@Configuration
@ConditionalOnClass(OpenAPI.class)
@RequiredArgsConstructor
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "bearerAuth";

    private final SwaggerProperties swaggerProperties;
    private final JwtProperties jwtProperties;

    @Bean
    @ConditionalOnMissingBean(OpenAPI.class)
    public OpenAPI customOpenApi() {
        return new OpenAPI()
                .info(new Info()
                        .title(swaggerProperties.getTitle())
                        .description(swaggerProperties.getDescription())
                        .version(swaggerProperties.getVersion())
                        .contact(new Contact()
                                .name(swaggerProperties.getContactName())
                                .email(swaggerProperties.getContactEmail())))
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components().addSecuritySchemes(SECURITY_SCHEME_NAME,
                        new SecurityScheme()
                                .name(jwtProperties.getHeader())
                                .type(SecurityScheme.Type.HTTP)
                                .scheme("bearer")
                                .bearerFormat("JWT")));
    }
}
