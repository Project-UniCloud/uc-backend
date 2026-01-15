package com.unicloudapp.common.audit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import com.unicloudapp.common.audit.infrastructure.rest.AuditLogResponse;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

@ExtendWith(MockitoExtension.class)
class AuditLogQueryServiceTest {

    @Mock
    private AuditLogRepository auditLogRepository;

    @InjectMocks
    private AuditLogQueryService auditLogQueryService;

    @Test
    @DisplayName("Should return paginated audit log responses")
    void shouldReturnPaginatedAuditLogResponses() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        Instant now = Instant.now();
        AuditLogEntity entity = AuditLogEntity.builder()
                .id(1L)
                .action("ACTION")
                .actor("actor")
                .occurredAt(now)
                .details(Map.of("key", "value"))
                .build();

        when(auditLogRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(entity), pageable, 1));

        // when
        Page<AuditLogResponse> result = auditLogQueryService.getAuditLogs(pageable);

        // then
        assertThat(result.getContent()).hasSize(1);
        AuditLogResponse response = result.getContent().getFirst();
        assertThat(response.id()).isEqualTo(1L);
        assertThat(response.action()).isEqualTo("ACTION");
        assertThat(response.actor()).isEqualTo("actor");
        assertThat(response.occurredAt()).isEqualTo(now);
        assertThat(response.details()).containsEntry("key", "value");
    }

    @Test
    @DisplayName("Should return empty page when no audit logs found")
    void shouldReturnEmptyPageWhenNoLogs() {
        // given
        Pageable pageable = PageRequest.of(0, 10);
        when(auditLogRepository.findAll(pageable)).thenReturn(new PageImpl<>(List.of(), pageable, 0));

        // when
        Page<AuditLogResponse> result = auditLogQueryService.getAuditLogs(pageable);

        // then
        assertThat(result.getContent()).isEmpty();
        assertThat(result.getTotalElements()).isZero();
    }
}
