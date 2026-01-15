package com.unicloudapp.common.audit.infrastructure.rest;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import com.unicloudapp.common.audit.AuditLogQueryService;
import java.time.Instant;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

@SpringJUnitConfig(classes = AuditRestController.class)
class AuditRestControllerTest {

    @Autowired
    private AuditRestController auditRestController;

    private MockMvc mockMvc;

    @MockitoBean
    private AuditLogQueryService auditLogQueryService;

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(auditRestController).build();
    }

    @Test
    @DisplayName("GET /audit-logs returns paginated audit logs")
    void getAuditLogs_success() throws Exception {
        // given
        AuditLogResponse log1 = AuditLogResponse.builder()
                .id(1L)
                .action("CREATE_GROUP")
                .actor("admin")
                .occurredAt(Instant.parse("2026-01-15T00:00:00Z"))
                .details(Map.of("groupId", "uuid-1"))
                .build();

        when(auditLogQueryService.getAuditLogs(any()))
                .thenReturn(new PageImpl<>(List.of(log1), PageRequest.of(0, 10), 1));

        // when & then
        mockMvc.perform(MockMvcRequestBuilders.get("/audit-logs")
                        .param("page", "0")
                        .param("pageSize", "10"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content", hasSize(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].id", is(1)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].action", is("CREATE_GROUP")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].actor", is("admin")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].details.groupId", is("uuid-1")));
    }
}
