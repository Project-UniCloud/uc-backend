package com.unicloudapp.group.infrastructure.rest;

import com.unicloudapp.common.cloud.CloudResourceAccessDetailsDto;
import com.unicloudapp.common.cloud.CloudResourceRowView;
import com.unicloudapp.common.user.UserDetails;
import com.unicloudapp.common.user.UserQueryService;
import com.unicloudapp.common.vo.cloud.CloudResourceAccessId;
import com.unicloudapp.common.vo.cloud.CloudResourceType;
import com.unicloudapp.common.vo.group.GroupId;
import com.unicloudapp.common.vo.user.UserId;
import com.unicloudapp.common.vo.user.UserLogin;
import com.unicloudapp.group.application.GroupDetailsView;
import com.unicloudapp.group.application.GroupRowView;
import com.unicloudapp.group.application.GroupService;
import com.unicloudapp.group.application.port.StudentImporterPort;
import org.jetbrains.annotations.NotNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringJUnitConfig;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.bind.annotation.ExceptionHandler;
import tools.jackson.databind.ObjectMapper;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import static org.hamcrest.Matchers.is;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.atLeastOnce;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@SpringJUnitConfig(classes = GroupRestController.class)
class GroupRestControllerAdditionalWebMvcTest {

    @Autowired
    GroupRestController groupRestController;

    MockMvc mockMvc;

    @MockitoBean
    GroupService groupService;

    @MockitoBean
    StudentImporterPort studentImporterPort;

    @MockitoBean
    UserQueryService userQueryService;

    public static class TestControllerAdvice {
        @ExceptionHandler(AccessDeniedException.class)
        public ResponseEntity<@NotNull String> handleAccessDenied(AccessDeniedException ex) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(ex.getMessage());
        }
    }

    @BeforeEach
    void setUp() {
        mockMvc = MockMvcBuilders.standaloneSetup(groupRestController)
                .setControllerAdvice(new TestControllerAdvice())
                .build();
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);

        when(authentication.getAuthorities()).thenReturn((java.util.Collection) List.of(new SimpleGrantedAuthority("ROLE_ADMIN")));
    }

    private ObjectMapper mapper() {
        return new ObjectMapper();
    }

    @Test
    @DisplayName("GET /groups/{id} returns GroupDetailsView")
    void getGroupById_success() throws Exception {
        UUID gid = UUID.randomUUID();
        GroupDetailsView view = GroupDetailsView.builder()
                .groupId(gid)
                .name("AI")
                .semester("2024L")
                .startDate(LocalDate.of(2024, 1, 1))
                .endDate(LocalDate.of(2024, 6, 30))
                .status("ACTIVE")
                .description("d")
                .lecturerFullNames(Set.of())
                .build();
        when(groupService.findById(gid)).thenReturn(view);

        mockMvc.perform(MockMvcRequestBuilders.get("/groups/{groupId}", gid))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.groupId", is(gid.toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.name", is("AI")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.semester", is("2024L")));
    }

    @Test
    @DisplayName("GET /groups lists groups with filters")
    void getGroups_filters() throws Exception {
        Page<@NotNull GroupRowView> page = new PageImpl<>(
                List.of(new GroupRowView(UUID.randomUUID(), "AI", "2024L", LocalDate.now(), "Prof X", "S3")),
                PageRequest.of(0, 10),
                1);
        when(groupService.getGroupsByFilter(any(), any())).thenReturn(page);

        mockMvc.perform(MockMvcRequestBuilders.get("/groups")
                        .param("page", "0")
                        .param("pageSize", "10")
                        .param("groupName", "AI"))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].name", is("AI")));
    }

    @Test
    @DisplayName("GET /groups/{id}/cloud-access/{accessId} returns details with dd-MM-yyyy date")
    void getCloudResourceAccessDetails_success() throws Exception {
        UUID gid = UUID.randomUUID();
        UUID aid = UUID.randomUUID();
        CloudResourceAccessDetailsDto dto = CloudResourceAccessDetailsDto.builder()
                .id(aid)
                .cron("0 0 * * * *")
                .limit(new BigDecimal("10"))
                .expiresAt(LocalDate.of(2025, 12, 31))
                .build();
        when(groupService.getCloudResourceAccess(GroupId.of(gid), CloudResourceAccessId.of(aid)))
                .thenReturn(dto);

        mockMvc.perform(MockMvcRequestBuilders.get("/groups/{groupId}/cloud-access/{accessId}", gid, aid))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id", is(aid.toString())))
                .andExpect(MockMvcResultMatchers.jsonPath("$.cron", is("0 0 * * * *")))
                .andExpect(MockMvcResultMatchers.jsonPath("$.limit", is(10)))
                .andExpect(MockMvcResultMatchers.jsonPath("$.expiresAt", is("31-12-2025")));
    }

    @Test
    @DisplayName("PUT /groups/{id}/cloud-access accepts dd-MM-yyyy and delegates; wrong date 400")
    void updateCloudResourceAccess_dateFormat() throws Exception {
        UUID gid = UUID.randomUUID();
        UUID aid = UUID.randomUUID();
        // Correct format
        String bodyOk = mapper().writeValueAsString(CloudResourceAccessDetailsDto.builder()
                .id(aid)
                .cron("0 */15 * * * *")
                .limit(new BigDecimal("20"))
                .expiresAt(LocalDate.of(2025, 1, 15))
                .build());

        mockMvc.perform(MockMvcRequestBuilders.put("/groups/{groupId}/cloud-access", gid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(bodyOk))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Wrong date format string
        String wrongDateJson = "{" + "\"id\":\""
                + aid + "\"," + "\"cron\":\"0 0 * * * *\","
                + "\"limit\":5,"
                + "\"expiresAt\":\"2025/01/15\"}"; // not dd-MM-yyyy

        mockMvc.perform(MockMvcRequestBuilders.put("/groups/{groupId}/cloud-access", gid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(wrongDateJson))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());
    }

    @Test
    @DisplayName("POST /groups/{id}/cloud-access returns created id")
    void grantCloudResourceAccess_success() throws Exception {
        UUID gid = UUID.randomUUID();
        UUID created = UUID.randomUUID();
        when(groupService.grantCloudResourceAccess(eq(GroupId.of(gid)), any(), any(), any()))
                .thenReturn(CloudResourceAccessId.of(created));

        GiveCloudResourceAccessRequest req = new GiveCloudResourceAccessRequest(
                "clientA", CloudResourceType.of("S3").toString(), BigDecimal.TEN);

        mockMvc.perform(MockMvcRequestBuilders.post("/groups/{groupId}/cloud-access", gid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper().writeValueAsString(req)))
                .andExpect(MockMvcResultMatchers.status().isCreated())
                .andExpect(MockMvcResultMatchers.jsonPath("$.value", is(created.toString())));
    }

    @Test
    @DisplayName("GET /groups/{id}/cloud-access returns paginated list")
    void listCloudResourceAccess_success() throws Exception {
        UUID gid = UUID.randomUUID();
        CloudResourceRowView row = CloudResourceRowView.builder()
                .id(UUID.randomUUID())
                .clientId("clientA")
                .name("S3")
                .costLimit(BigDecimal.ONE)
                .limitUsed(BigDecimal.ZERO)
                .expiresAt(LocalDate.now())
                .lastUsedAt(LocalDate.now().atStartOfDay())
                .cronCleanupSchedule("cron")
                .status("ACTIVE")
                .notificationLevel1(10)
                .notificationLevel2(20)
                .notificationLevel3(30)
                .build();
        Pageable pageable = PageRequest.of(0, 20);
        when(groupService.getCloudResourceAccesses(eq(GroupId.of(gid)), any(Pageable.class)))
                .thenReturn(new PageImpl<>(List.of(row), pageable, 1));

        mockMvc.perform(MockMvcRequestBuilders.get("/groups/{groupId}/cloud-access", gid))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.content[0].clientId", is("clientA")));
    }

    @Test
    @DisplayName("POST /groups/{id}/activate and /archive return 200")
    void lifecycle_endpoints() throws Exception {
        UUID gid = UUID.randomUUID();
        mockMvc.perform(MockMvcRequestBuilders.post("/groups/{groupId}/activate", gid))
                .andExpect(MockMvcResultMatchers.status().isOk());
        mockMvc.perform(MockMvcRequestBuilders.post("/groups/{groupId}/archive", gid))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("POST /groups/{gid}/cloud-access/{aid}/deactivate delegates")
    void deactivateCloudResourceAccess_endpoint() throws Exception {
        UUID gid = UUID.randomUUID();
        UUID aid = UUID.randomUUID();

        mockMvc.perform(MockMvcRequestBuilders.post(
                        "/groups/{groupId}/cloud-access/{CloudResourceAccessId}/deactivate", gid, aid))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("POST /groups/{groupId}/students returns 403 when lecturer is not assigned to group")
    void addStudent_forbiddenForUnassignedLecturer() {
        UUID gid = UUID.randomUUID();
        String lecturerLogin = "lecturer1";
        UserId lecturerId = UserId.of(UUID.randomUUID());

        Authentication auth = mock(Authentication.class);
        SecurityContext securityContext = mock(SecurityContext.class);
        when(securityContext.getAuthentication()).thenReturn(auth);
        SecurityContextHolder.setContext(securityContext);

        when(auth.getAuthorities()).thenReturn((java.util.Collection) List.of(new SimpleGrantedAuthority("ROLE_LECTURER")));
        when(auth.getName()).thenReturn(lecturerLogin);

        UserDetails userDetails = UserDetails.builder().userId(lecturerId).build();
        when(userQueryService.getUserDetailsByUsername(UserLogin.of(lecturerLogin)))
                .thenReturn(Optional.of(userDetails));

        GroupDetailsView groupDetails = GroupDetailsView.builder()
                .lecturerIds(Collections.singleton(UUID.randomUUID())) // assigned to someone else
                .build();
        when(groupService.findById(gid)).thenReturn(groupDetails);

        String body = mapper().writeValueAsString(com.unicloudapp.common.user.StudentBasicData.builder()
                .login("s123")
                .firstName("A")
                .lastName("B")
                .email("a@b.com")
                .build());

        org.junit.jupiter.api.Assertions.assertThrows(AccessDeniedException.class, () -> {
            try {
                mockMvc.perform(MockMvcRequestBuilders.post("/groups/{groupId}/students", gid)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body));
            } catch (jakarta.servlet.ServletException e) {
                if (e.getCause() instanceof AccessDeniedException) {
                    throw e.getCause();
                }
                throw e;
            }
        });

        verify(groupService, atLeastOnce()).findById(gid);
    }
}
