package com.unicloudapp.group.application;

import com.unicloudapp.group.domain.Group;
import com.unicloudapp.group.domain.GroupFactory;
import com.unicloudapp.group.domain.GroupStatus;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.Set;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
class GroupToDtoMapperTest {

    @InjectMocks
    private GroupToDtoMapperImpl groupToDtoMapper;

    @Test
    void shouldMapGroupToDtoAndBack() {
        var groupId = UUID.randomUUID();
        var lecturerId = UUID.randomUUID();
        var attenderId = UUID.randomUUID();
        LocalDate startDate = LocalDate.of(2024,
                1,
                1
        );
        LocalDate endDate = LocalDate.of(2025,
                1,
                1
        );
        Group group = new GroupFactory()
                .restore(
                        groupId,
                        "Test Group",
                        GroupStatus.Type.ACTIVE,
                        "2024L",
                        startDate,
                        endDate,
                        Set.of(lecturerId),
                        Set.of(attenderId),
                        Set.of("aws"),
                        "Test description"
                );

        GroupDTO dto = groupToDtoMapper.toDto(group);

        assertThat(dto).isNotNull();
        assertThat(dto.groupId()).isEqualTo(groupId);
        assertThat(dto.name()).isEqualTo("Test Group");
        assertThat(dto.groupStatus()).isEqualTo(GroupStatus.Type.ACTIVE);
        assertThat(dto.semester()).isEqualTo("2024L");
        assertThat(dto.startDate()).isEqualTo(startDate);
        assertThat(dto.endDate()).isEqualTo(endDate);
        assertThat(dto.lecturers()).containsExactly(lecturerId);
        assertThat(dto.attenders()).containsExactly(attenderId);
        assertThat(dto.cloudAccesses()).containsExactly("aws");
    }

}