package com.gurpreet.starter.autoconfigure;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * gurpreet:
 *   starter:
 *     swagger:
 *       title: "AMS CRM API"
 *       description: "Asset Management System - REST API"
 *       version: "1.0.0"
 *       contact-name: "Infodart Technologies"
 *       contact-email: "support@infodart.com"
 */
@Data
@ConfigurationProperties(prefix = "gurpreet.starter.swagger")
public class SwaggerProperties {

    private String title = "API Documentation";
    private String description = "REST API documentation";
    private String version = "1.0.0";
    private String contactName = "";
    private String contactEmail = "";
}
