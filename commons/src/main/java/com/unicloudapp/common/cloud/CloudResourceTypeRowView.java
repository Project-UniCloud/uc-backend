package com.unicloudapp.common.cloud;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Builder
public record CloudResourceTypeRowView(
        String clientId,
        String name,
        BigDecimal costLimit,
        BigDecimal limitUsed,
        @JsonFormat(pattern = "dd-MM-yyyy") LocalDate expiresAt,
        @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss") LocalDateTime lastUsedAt,
        String cronCleanupSchedule,
        String status
) {

}
