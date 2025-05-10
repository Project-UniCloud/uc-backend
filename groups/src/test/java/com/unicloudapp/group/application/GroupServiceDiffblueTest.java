package com.unicloudapp.group.application;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isA;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.unicloudapp.common.domain.user.UserId;
import com.unicloudapp.group.domain.Group;
import com.unicloudapp.group.domain.GroupFactory;
import com.unicloudapp.group.domain.GroupId;
import com.unicloudapp.group.domain.GroupStatus;
import com.unicloudapp.group.domain.GroupStatus.Type;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.aot.DisabledInAotMode;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ContextConfiguration(classes = {GroupService.class})
@DisabledInAotMode
@ExtendWith(SpringExtension.class)
class GroupServiceDiffblueTest {

    @MockitoBean
    private GroupFactory groupFactory;

    @MockitoBean
    private GroupRepositoryPort groupRepositoryPort;

    @Autowired
    private GroupService groupService;

    @MockitoBean
    private GroupToDtoMapper groupToDtoMapper;

    /**
     * Test {@link GroupService#createGroup(GroupDTO)}.
     * <ul>
     *   <li>When {@link LocalDate} with {@code 1970} and one and one.</li>
     *   <li>Then return {@code null}.</li>
     * </ul>
     * <p>
     * Method under test: {@link GroupService#createGroup(GroupDTO)}
     */
    @Test
    @DisplayName("Test createGroup(GroupDTO); when LocalDate with '1970' and one and one; then return 'null'")
    @Tag("MaintainedByDiffblue")
    void testCreateGroup_whenLocalDateWith1970AndOneAndOne_thenReturnNull() {
        // Arrange
        when(groupRepositoryPort.save(Mockito.<Group>any())).thenReturn(null);
        when(groupFactory.create(Mockito.<String>any(),
                Mockito.<String>any(),
                Mockito.<Set<UUID>>any(),
                Mockito.<LocalDate>any(),
                Mockito.<LocalDate>any()
        )).thenReturn(null);
        UUID groupId = UUID.randomUUID();
        LocalDate startDate = LocalDate.of(1970,
                1,
                1
        );
        LocalDate endDate = LocalDate.of(1970,
                1,
                1
        );
        HashSet<UUID> lecturers = new HashSet<>();
        HashSet<UUID> attenders = new HashSet<>();

        // Act
        Group actualCreateGroupResult = groupService.createGroup(new GroupDTO(groupId,
                "Name",
                Type.ACTIVE,
                "Semester",
                startDate,
                endDate,
                lecturers,
                attenders,
                new HashSet<>()
        ));

        // Assert
        verify(groupRepositoryPort).save(isNull());
        verify(groupFactory).create(eq("Name"),
                eq("Semester"),
                isA(Set.class),
                isA(LocalDate.class),
                isA(LocalDate.class)
        );
        assertNull(actualCreateGroupResult);
    }

    /**
     * Test {@link GroupService#addAttender(GroupId, UserId)}.
     * <ul>
     *   <li>Given {@link Group} {@link Group#addAttender(UserId)} does nothing.</li>
     *   <li>Then calls {@link GroupRepositoryPort#findById(UUID)}.</li>
     * </ul>
     * <p>
     * Method under test: {@link GroupService#addAttender(GroupId, UserId)}
     */
    @Test
    @DisplayName("Test addAttender(GroupId, UserId); given Group addAttender(UserId) does nothing; then calls findById(UUID)")
    void testAddAttender_givenGroupAddAttenderDoesNothing_thenCallsFindById() {
        // Arrange
        Group group = mock(Group.class);
        doNothing().when(group)
                .addAttender(Mockito.<UserId>any());
        Optional<Group> ofResult = Optional.of(group);
        when(groupRepositoryPort.save(Mockito.<Group>any())).thenReturn(null);
        when(groupRepositoryPort.findById(Mockito.<UUID>any())).thenReturn(ofResult);

        // Act
        groupService.addAttender(new GroupId(UUID.randomUUID()),
                null
        );

        // Assert
        verify(groupRepositoryPort).findById(isA(UUID.class));
        verify(groupRepositoryPort).save(isA(Group.class));
        verify(group).addAttender(isNull());
    }

    /**
     * Test {@link GroupService#getAllGroups(Pageable)}.
     * <p>
     * Method under test: {@link GroupService#getAllGroups(Pageable)}
     */
    @Test
    @DisplayName("Test getAllGroups(Pageable)")
    @Disabled("TODO: Complete this test")
    @Tag("MaintainedByDiffblue")
    void testGetAllGroups() {
        // TODO: Diffblue Cover was only able to create a partial test for this method:
        //   Reason: No inputs found that don't throw a trivial exception.
        //   Diffblue Cover tried to run the arrange/act section, but the method under
        //   test threw
        //   java.lang.NullPointerException: Cannot invoke "org.springframework.data.domain.Pageable.getPageSize()" because "pageable" is null
        //       at com.unicloudapp.group.application.GroupService.getAllGroups(GroupService.java:45)
        //   See https://diff.blue/R013 to resolve this issue.

        // Arrange and Act
        groupService.getAllGroups(null);
    }
}
