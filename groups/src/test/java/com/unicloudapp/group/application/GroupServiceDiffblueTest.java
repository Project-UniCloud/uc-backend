package com.unicloudapp.group.application;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.Mockito.*;

import com.unicloudapp.common.cloud.CloudResourceAccessCommandService;
import com.unicloudapp.common.cloud.CloudResourceAccessQueryService;
import com.unicloudapp.common.user.UserCommandService;
import com.unicloudapp.common.user.UserQueryService;
import com.unicloudapp.common.user.UserValidationService;
import com.unicloudapp.group.application.port.GroupRepositoryPort;
import com.unicloudapp.group.domain.Group;
import com.unicloudapp.group.domain.GroupFactory;
import com.unicloudapp.group.domain.vo.GroupStatus.Type;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
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

    @MockitoBean
    private UserValidationService userValidationService;

    @MockitoBean
    private UserQueryService userQueryService;

    @MockitoBean
    private CloudResourceAccessQueryService cloudResourceAccessQueryService;

    @MockitoBean
    private CloudResourceAccessCommandService cloudResourceAccessCommandService;

    @MockitoBean
    private UserCommandService userCommandService;

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
    @SuppressWarnings({"unchecked"})
    void testCreateGroup_whenLocalDateWith1970AndOneAndOne_thenReturnNull() {
        // Arrange
        when(groupRepositoryPort.save(Mockito.any())).thenReturn(null);
        when(groupFactory.create(
                        Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any(), Mockito.any()))
                .thenReturn(null);
        UUID groupId = UUID.randomUUID();
        LocalDate startDate = LocalDate.of(1970, 1, 1);
        LocalDate endDate = LocalDate.of(1970, 2, 1);
        HashSet<UUID> lecturers = new HashSet<>();
        HashSet<UUID> students = new HashSet<>();

        // Act
        Group actualCreateGroupResult = groupService.createGroup(new GroupDTO(
                groupId,
                "Name",
                Type.ACTIVE,
                "2024L",
                startDate,
                endDate,
                lecturers,
                students,
                new HashSet<>(),
                "Description"));

        // Assert
        verify(groupRepositoryPort).save(isNull());
        verify(groupFactory)
                .create(
                        eq("Name"),
                        eq("2024L"),
                        isA(Set.class),
                        isA(LocalDate.class),
                        isA(LocalDate.class),
                        isA(String.class));
        assertNull(actualCreateGroupResult);
    }
}
