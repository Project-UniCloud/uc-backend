package com.unicloudapp.group.infrastructure.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.unicloudapp.common.user.StudentBasicData;
import com.unicloudapp.common.user.UserValidationService;
import com.unicloudapp.group.application.GroupDTO;
import com.unicloudapp.group.application.GroupService;
import com.unicloudapp.group.application.StudentImporterPort;
import com.unicloudapp.group.domain.Group;
import com.unicloudapp.common.domain.group.GroupId;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.request.MockHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.*;

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
    private StudentImporterPort studentImporterPort;

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
        MockHttpServletRequestBuilder contentTypeResult = MockMvcRequestBuilders.post("/groups")
                .contentType(MediaType.APPLICATION_JSON);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        HashSet<UUID> lecturers = new HashSet<>();
        LocalDate startDate = LocalDate.of(1970,
                1,
                1
        );
        MockHttpServletRequestBuilder requestBuilder = contentTypeResult.content(objectMapper.writeValueAsString(
                new CreateGroupRequest("Name",
                        "Semester",
                        lecturers,
                        startDate,
                        LocalDate.of(1970,
                                1,
                                1
                        ),
                        "Description"
                )));
        Group group = mock(Group.class);
        UUID uuid = UUID.randomUUID();
        when(group.getGroupId()).thenReturn(GroupId.of(uuid));
        when(groupService.createGroup(Mockito.any())).thenReturn(group);

        // Act
        MockMvcBuilders.standaloneSetup(groupRestController)
                .build()
                .perform(requestBuilder);
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
        when(groupService.createGroup(Mockito.<GroupDTO>any())).thenReturn(group);
        GroupRestController groupRestController = new GroupRestController(groupService, studentBasicData);
        HashSet<UUID> lecturers = new HashSet<>();
        LocalDate startDate = LocalDate.of(1970,
                1,
                1
        );

        // Act
        UUID actualCreateGroupResult = groupRestController
                .createGroup(new CreateGroupRequest("Name",
                        "Semester",
                        lecturers,
                        startDate,
                        LocalDate.of(1970,
                                1,
                                1
                        ),
                        "Description"
                ));

        // Assert
        verify(groupService).createGroup(isA(GroupDTO.class));
        verify(group).getGroupId();
        assertSame(uuid,
                actualCreateGroupResult
        );
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

        doNothing().when(groupService)
                .addStudent(Mockito.any(), Mockito.any());

        String json = new ObjectMapper().writeValueAsString(requestData);

        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/groups/{groupId}/students", groupId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(json);

        // Act and Assert
        MockMvcBuilders.standaloneSetup(groupRestController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status().isOk());
    }
}