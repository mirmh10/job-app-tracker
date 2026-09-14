package com.example.jobtracker;

import java.time.LocalDate;

public record ApplicationEntry(
        long id,
        String company,
        String role,
        LocalDate dateApplied,
        String url,
        String status,
        String notes
) {
}
