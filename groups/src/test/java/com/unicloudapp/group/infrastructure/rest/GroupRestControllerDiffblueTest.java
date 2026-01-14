package com.unicloudapp.group.infrastructure.rest;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.unicloudapp.common.user.StudentBasicData;
import com.unicloudapp.common.user.UserQueryService;
import com.unicloudapp.common.user.UserValidationService;
import com.unicloudapp.common.vo.group.GroupId;
import com.unicloudapp.common.vo.user.UserId;
import com.unicloudapp.group.application.GroupDTO;
import com.unicloudapp.group.application.GroupService;
import com.unicloudapp.group.application.port.StudentImporterPort;
import com.unicloudapp.group.domain.Group;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import tools.jackson.databind.ObjectMapper;

@ContextConfiguration(classes = GroupRestController.class)
@DisabledInAotMode
@ExtendWith(SpringExtension.class)
class GroupRestControllerDiffblueTest {

    @Autowired
    private GroupRestController groupRestController;

    @MockitoBean
    private GroupService groupService;

    @MockitoBean
    private UserValidationService userValidationService;

    @MockitoBean
    private UserQueryService userQueryService;

    @MockitoBean
    private StudentImporterPort studentImporterPort;

    @BeforeEach
    void setUp() {
        SecurityContext securityContext = Mockito.mock(SecurityContext.class);
        Authentication authentication = Mockito.mock(Authentication.class);
        Mockito.when(securityContext.getAuthentication()).thenReturn(authentication);
        SecurityContextHolder.setContext(securityContext);
    }

    /**
     * Test {@link GroupRestController#createGroup(CreateGroupRequest)}.
     * <p>
     * Method under test: {@link GroupRestController#createGroup(CreateGroupRequest)}
     */
    @Test
    @DisplayName("Test createGroup(CreateGroupRequest)")
    @Tag("MaintainedByDiffblue")
    void testCreateGroup() throws Exception {
        // Arrange
        MockHttpServletRequestBuilder contentTypeResult =
                MockMvcRequestBuilders.post("/groups").contentType(MediaType.APPLICATION_JSON);

        ObjectMapper objectMapper = new ObjectMapper();
        HashSet<UUID> lecturers = new HashSet<>();
        LocalDate startDate = LocalDate.of(1970, 1, 1);
        MockHttpServletRequestBuilder requestBuilder =
                contentTypeResult.content(objectMapper.writeValueAsString(new CreateGroupRequest(
                        "Name", "Semester", lecturers, startDate, LocalDate.of(1970, 1, 1), "Description")));
        Group group = mock(Group.class);
        UUID uuid = UUID.randomUUID();
        when(group.getGroupId()).thenReturn(GroupId.of(uuid));
        when(groupService.createGroup(Mockito.any())).thenReturn(group);

        // Act
        MockMvcBuilders.standaloneSetup(groupRestController).build().perform(requestBuilder);
    }

    /**
     * Test {@link GroupRestController#createGroup(CreateGroupRequest)}.
     * <ul>
     *   <li>Then return randomUUID.</li>
     * </ul>
     * <p>
     * Method under test: {@link GroupRestController#createGroup(CreateGroupRequest)}
     */
    @Test
    @DisplayName("Test createGroup(CreateGroupRequest); then return randomUUID")
    @Tag("MaintainedByDiffblue")
    void testCreateGroup_thenReturnRandomUUID() {
        //   Diffblue Cover was unable to create a Spring-specific test for this Spring method.
        //   Run dcover create --keep-partial-tests to gain insights into why
        //   a non-Spring test was created.

        // Arrange
        Group group = mock(Group.class);
        UUID uuid = UUID.randomUUID();
        when(group.getGroupId()).thenReturn(GroupId.of(uuid));
        GroupService groupService = mock(GroupService.class);
        StudentImporterPort studentBasicData = mock(StudentImporterPort.class);
        UserQueryService userQueryService = mock(UserQueryService.class);
        when(groupService.createGroup(Mockito.any())).thenReturn(group);
        GroupRestController groupRestController =
                new GroupRestController(groupService, studentBasicData, userQueryService);
        HashSet<UUID> lecturers = new HashSet<>();
        LocalDate startDate = LocalDate.of(1970, 1, 1);

        // Act
        UUID actualCreateGroupResult = groupRestController.createGroup(new CreateGroupRequest(
                "Name", "Semester", lecturers, startDate, LocalDate.of(1970, 1, 1), "Description"));

        // Assert
        verify(groupService).createGroup(isA(GroupDTO.class));
        verify(group).getGroupId();
        assertSame(uuid, actualCreateGroupResult);
    }

    @Test
    @DisplayName("Test addStudent(UUID, StudentBasicData)")
    void testAddStudent() throws Exception {
        // Arrange
        UUID groupId = UUID.randomUUID();
        StudentBasicData requestData = StudentBasicData.builder()
                .email("email@example.com")
                .firstName("John")
                .lastName("Doe")
                .login("s123123")
                .build();

        doNothing().when(groupService).addStudent(Mockito.any(), Mockito.any());

        String json = new ObjectMapper().writeValueAsString(requestData);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders.post(
                        "/groups/{groupId}/students", groupId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        // Act and Assert
        MockMvcBuilders.standaloneSetup(groupRestController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    @Test
    @DisplayName("Test deleteStudentFromGroup(UUID, UUID)")
    void testDeleteStudentFromGroup() throws Exception {
        // Arrange
        UUID groupId = UUID.randomUUID();
        UUID studentId = UUID.randomUUID();

        doNothing().when(groupService).deleteStudentFromGroup(Mockito.any(), Mockito.any());

        MockHttpServletRequestBuilder requestBuilder =
                MockMvcRequestBuilders.delete("/groups/{groupId}/students/{studentId}", groupId, studentId);

        // Act and Assert
        MockMvcBuilders.standaloneSetup(groupRestController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());

        verify(groupService).deleteStudentFromGroup(eq(GroupId.of(groupId)), eq(UserId.of(studentId)));
    }
}
