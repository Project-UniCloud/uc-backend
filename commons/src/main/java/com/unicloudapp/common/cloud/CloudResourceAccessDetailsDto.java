package com.unicloudapp.common.cloud;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;
import lombok.Builder;

@Builder
public record CloudResourceAccessDetailsDto(
        UUID id,
        BigDecimal limit,
        String cron,
        @JsonFormat(pattern = "dd-MM-yyyy") LocalDate expiresAt,
        String status,
        Integer notificationLevel1,
        Integer notificationLevel2,
        Integer notificationLevel3) {}
