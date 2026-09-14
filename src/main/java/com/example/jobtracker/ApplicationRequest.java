package com.example.jobtracker;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import java.time.LocalDate;

public record ApplicationRequest(
        @NotBlank @Size(max = 120) String company,
        @NotBlank @Size(max = 120) String role,
        @NotNull LocalDate dateApplied,
        @Size(max = 2048) @Pattern(regexp = "|https?://.+", message = "must be an http or https URL") String url,
        @NotNull @Pattern(regexp = "Applied|Interview|Rejected|Offer") String status,
        @Size(max = 2000) String notes
) {
    public ApplicationRequest {
        company = company == null ? null : company.trim();
        role = role == null ? null : role.trim();
        url = url == null ? "" : url.trim();
        notes = notes == null ? "" : notes.trim();
    }
}
