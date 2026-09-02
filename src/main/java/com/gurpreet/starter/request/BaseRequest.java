package com.gurpreet.starter.request;

import lombok.Data;

import java.io.Serializable;

/**
 * Marker/common base for incoming request DTOs.
 * Extend this in your project's request DTOs to keep a consistent shape
 * and to easily add cross-cutting fields (requestId, source, locale, etc.)
 * later without touching every DTO.
 */
@Data
public abstract class BaseRequest implements Serializable {

    /** Optional client-supplied correlation/request id, useful for tracing across logs. */
    private String requestId;
}
