package com.gurpreet.starter.request;

import jakarta.validation.constraints.Min;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * Common request shape for paginated list/search endpoints.
 * Extend and add filter fields per module (e.g. LeadSearchRequest extends PageableRequest).
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class PageableRequest extends BaseRequest {

    @Min(value = 0, message = "page must be >= 0")
    private int page = 0;

    @Min(value = 1, message = "size must be >= 1")
    private int size = 10;

    private String sortBy = "id";

    private String sortDirection = "DESC"; // ASC | DESC
}
