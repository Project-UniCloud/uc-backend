package com.unicloudapp.common.cloud;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import lombok.Builder;

@Builder
public record CloudResourceRowView(
        UUID id,
        String clientId,
        String name,
        BigDecimal costLimit,
        BigDecimal limitUsed,
        @JsonFormat(pattern = "dd-MM-yyyy") LocalDate expiresAt,
        @JsonFormat(pattern = "dd-MM-yyyy HH:mm:ss") LocalDateTime lastUsedAt,
        String cronCleanupSchedule,
        String status,
        Integer notificationLevel1,
        Integer notificationLevel2,
        Integer notificationLevel3) {}
