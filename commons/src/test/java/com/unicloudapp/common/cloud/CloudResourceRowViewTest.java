package com.unicloudapp.common.cloud;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class CloudResourceRowViewTest {

    @Test
    @DisplayName("Builder sets fields and JSON serialization respects @JsonFormat patterns")
    void builderAndJsonFormat() throws JacksonException {
        CloudResourceRowView view = CloudResourceRowView.builder()
                .clientId("client-123")
                .name("VM")
                .costLimit(new BigDecimal("100.00"))
                .limitUsed(new BigDecimal("25.50"))
                .expiresAt(LocalDate.of(2025, 1, 5))
                .lastUsedAt(LocalDateTime.of(2025, 1, 5, 14, 7, 9))
                .cronCleanupSchedule("0 0 * * *")
                .status("ACTIVE")
                .build();

        assertEquals("client-123", view.clientId());
        assertEquals("VM", view.name());
        assertEquals(new BigDecimal("100.00"), view.costLimit());
        assertEquals(new BigDecimal("25.50"), view.limitUsed());
        assertEquals(LocalDate.of(2025, 1, 5), view.expiresAt());
        assertEquals(LocalDateTime.of(2025, 1, 5, 14, 7, 9), view.lastUsedAt());
        assertEquals("0 0 * * *", view.cronCleanupSchedule());
        assertEquals("ACTIVE", view.status());

        ObjectMapper mapper = new ObjectMapper();

        String json = mapper.writeValueAsString(view);

        assertTrue(json.contains("\"expiresAt\":\"05-01-2025\""), json);
        assertTrue(json.contains("\"lastUsedAt\":\"05-01-2025 14:07:09\""), json);
    }
}
