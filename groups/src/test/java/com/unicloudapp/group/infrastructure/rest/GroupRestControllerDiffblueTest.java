package com.unicloudapp.group.infrastructure.rest;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.unicloudapp.common.domain.user.UserId;
import com.unicloudapp.group.application.GroupDTO;
import com.unicloudapp.group.application.GroupService;
import com.unicloudapp.group.domain.Group;
import com.unicloudapp.group.domain.GroupId;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.UUID;

import org.junit.jupiter.api.Disabled;
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

@ContextConfiguration(classes = {GroupRestController.class})
@DisabledInAotMode
@ExtendWith(SpringExtension.class)
class GroupRestControllerDiffblueTest {

    @Autowired
    private GroupRestController groupRestController;

    @MockitoBean
    private GroupService groupService;

    /**
     * Test {@link GroupRestController#createGroup(CreateGroupRequest)}.
     * <p>
     * Method under test: {@link GroupRestController#createGroup(CreateGroupRequest)}
     */
    @Test
    @DisplayName("Test createGroup(CreateGroupRequest)")
    @Disabled("TODO: Complete this test")
    @Tag("MaintainedByDiffblue")
    void testCreateGroup() throws Exception {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   com.fasterxml.jackson.databind.exc.InvalidDefinitionException: Java 8 date/time type `java.time.LocalDate` not supported by default: add Module "com.fasterxml.jackson.datatype:jackson-datatype-jsr310" to enable handling (through reference chain: com.unicloudapp.group.infrastructure.rest.CreateGroupRequest["startDate"])
        //       at com.fasterxml.jackson.databind.exc.InvalidDefinitionException.from(InvalidDefinitionException.java:77)
        //       at com.fasterxml.jackson.databind.SerializerProvider.reportBadDefinition(SerializerProvider.java:1340)
        //       at com.fasterxml.jackson.databind.ser.impl.UnsupportedTypeSerializer.serialize(UnsupportedTypeSerializer.java:35)
        //       at com.fasterxml.jackson.databind.ser.BeanPropertyWriter.serializeAsField(BeanPropertyWriter.java:732)
        //       at com.fasterxml.jackson.databind.ser.std.BeanSerializerBase.serializeFields(BeanSerializerBase.java:770)
        //       at com.fasterxml.jackson.databind.ser.BeanSerializer.serialize(BeanSerializer.java:184)
        //       at com.fasterxml.jackson.databind.ser.DefaultSerializerProvider._serialize(DefaultSerializerProvider.java:502)
        //       at com.fasterxml.jackson.databind.ser.DefaultSerializerProvider.serializeValue(DefaultSerializerProvider.java:341)
        //       at com.fasterxml.jackson.databind.ObjectMapper._writeValueAndClose(ObjectMapper.java:4819)
        //       at com.fasterxml.jackson.databind.ObjectMapper.writeValueAsString(ObjectMapper.java:4060)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange
        MockHttpServletRequestBuilder contentTypeResult = MockMvcRequestBuilders.post("/groups")
                .contentType(MediaType.APPLICATION_JSON);

        ObjectMapper objectMapper = new ObjectMapper();
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
                        )
                )));

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
        when(group.getGroupId()).thenReturn(new GroupId(uuid));
        GroupService groupService = mock(GroupService.class);
        when(groupService.createGroup(Mockito.<GroupDTO>any())).thenReturn(group);
        GroupRestController groupRestController = new GroupRestController(groupService);
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
                        )
                ));

        // Assert
        verify(groupService).createGroup(isA(GroupDTO.class));
        verify(group).getGroupId();
        assertSame(uuid,
                actualCreateGroupResult
        );
    }

    /**
     * Test {@link GroupRestController#addAttender(UUID, UUID)}.
     * <p>
     * Method under test: {@link GroupRestController#addAttender(UUID, UUID)}
     */
    @Test
    @DisplayName("Test addAttender(UUID, UUID)")
    void testAddAttender() throws Exception {
        // Arrange
        doNothing().when(groupService)
                .addAttender(Mockito.<GroupId>any(),
                        Mockito.<UserId>any()
                );
        UUID randomUUIDResult = UUID.randomUUID();
        MockHttpServletRequestBuilder requestBuilder = MockMvcRequestBuilders
                .post("/groups/{groupId}/attenders/{attenderId}",
                        randomUUIDResult,
                        UUID.randomUUID()
                );

        // Act and Assert
        MockMvcBuilders.standaloneSetup(groupRestController)
                .build()
                .perform(requestBuilder)
                .andExpect(MockMvcResultMatchers.status()
                        .isOk());
    }

    /**
     * Test {@link GroupRestController#getAllGroups(int, int)}.
     * <ul>
     *   <li>Then return {@link PageImpl}.</li>
     * </ul>
     * <p>
     * Method under test: {@link GroupRestController#getAllGroups(int, int)}
     */
    @Test
    @DisplayName("Test getAllGroups(int, int); then return PageImpl")
    @Tag("MaintainedByDiffblue")
    void testGetAllGroups_thenReturnPageImpl() {
        //   Diffblue Cover was unable to create a Spring-specific test for this Spring method.
        //   Run dcover create --keep-partial-tests to gain insights into why
        //   a non-Spring test was created.

        // Arrange
        GroupService groupService = mock(GroupService.class);
        PageImpl<GroupDTO> pageImpl = new PageImpl<>(new ArrayList<>());
        when(groupService.getAllGroups(Mockito.<Pageable>any())).thenReturn(pageImpl);

        // Act
        Page<GroupDTO> actualAllGroups = (new GroupRestController(groupService)).getAllGroups(1,
                3
        );

        // Assert
        verify(groupService).getAllGroups(isA(Pageable.class));
        assertTrue(actualAllGroups instanceof PageImpl);
        assertTrue(actualAllGroups.toList()
                .isEmpty());
        assertSame(pageImpl,
                actualAllGroups
        );
    }
}
