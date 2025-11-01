package com.unicloudapp.group.infrastructure.persistence;

import com.unicloudapp.common.vo.cloud.CloudResourceAccessId;
import com.unicloudapp.common.vo.group.GroupName;
import com.unicloudapp.common.vo.group.Semester;
import com.unicloudapp.common.group.GroupCloudDto;
import com.unicloudapp.common.group.GroupUniqueName;
import com.unicloudapp.group.domain.GroupStatus;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SqlGroupRepositoryAdapterTest {

    @Test
    @DisplayName("findAllGroupCloudDto maps projection to GroupCloudDto when name has no trailing space")
    void findActiveGroups_malformedConcatenation_thenThrows() {
        GroupJpaRepository repo = mock(GroupJpaRepository.class);
        GroupToEntityMapper mapper = mock(GroupToEntityMapper.class);
        SqlGroupRepositoryAdapter adapter = new SqlGroupRepositoryAdapter(repo, mapper);

        UUID id = UUID.randomUUID();
        GroupCloudDtoProjection projection = new GroupCloudDtoProjection() {
            @Override public String getName() { return "AI"; }
            @Override public List<UUID> getCloudResourceAccesses() { return List.of(id); }
            @Override public String getSemester() { return "2024L"; }
        };
        when(repo.findAllProjectedByGroupStatus(GroupStatus.Type.ACTIVE)).thenReturn(List.of(projection));
        List<GroupCloudDto> groupCloudDtoList = adapter.findActiveGroups();

        GroupUniqueName groupUniqueName = GroupUniqueName.builder()
                .groupName(GroupName.of(projection.getName()))
                .semester(Semester.of(projection.getSemester()))
                .build();
        GroupCloudDto groupCloudDto = new GroupCloudDto(
                groupUniqueName,
                projection.getCloudResourceAccesses()
                        .stream()
                        .map(CloudResourceAccessId::of)
                        .toList()
        );
        assertTrue(groupCloudDtoList.contains(groupCloudDto));
    }

    @Test
    @DisplayName("findAllGroupCloudDto maps projection to GroupCloudDto when name includes trailing space")
    void findActiveGroupWithActiveCloudResourcesDto_valid_whenNameEndsWithSpace() {
        GroupJpaRepository repo = mock(GroupJpaRepository.class);
        GroupToEntityMapper mapper = mock(GroupToEntityMapper.class);
        SqlGroupRepositoryAdapter adapter = new SqlGroupRepositoryAdapter(repo, mapper);

        UUID id1 = UUID.randomUUID();
        GroupCloudDtoProjection projection = new GroupCloudDtoProjection() {
            @Override public String getName() { return "AI"; }
            @Override public List<UUID> getCloudResourceAccesses() { return List.of(id1); }
            @Override public String getSemester() { return "2024L"; }
        };
        when(repo.findAllProjectedByGroupStatus(GroupStatus.Type.ACTIVE)).thenReturn(List.of(projection));

        List<GroupCloudDto> result = adapter.findActiveGroups();
        assertEquals(1, result.size());
        GroupCloudDto dto = result.getFirst();
        assertEquals("AI 2024L", dto.groupUniqueName().toString());
        assertEquals(List.of(CloudResourceAccessId.of(id1)), dto.cloudResourceAccesses());
    }
}
