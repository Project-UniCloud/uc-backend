package com.unicloudapp.common.cloud;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Builder;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

@Builder
public record CloudResourceAccessDetailsDto(
        UUID id,
        BigDecimal limit,
        String cron,
        @JsonFormat(pattern = "dd-MM-yyyy") LocalDate expiresAt,
        String status
) { }
