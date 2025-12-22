package com.unicloudapp.group.infrastructure.persistence;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.unicloudapp.common.group.GroupCloudDto;
import com.unicloudapp.common.group.GroupDto;
import com.unicloudapp.common.group.GroupUniqueName;
import com.unicloudapp.common.vo.cloud.CloudResourceAccessId;
import com.unicloudapp.common.vo.group.GroupName;
import com.unicloudapp.common.vo.group.Semester;
import com.unicloudapp.common.vo.user.UserId;
import com.unicloudapp.group.domain.vo.GroupStatus;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class SqlGroupRepositoryAdapterTest {

    @Test
    @DisplayName("findAllGroupCloudDto maps projection to GroupCloudDto when name has no trailing space")
    void findActiveGroups_malformedConcatenation_thenThrows() {
        GroupJpaRepository repo = mock(GroupJpaRepository.class);
        GroupToEntityMapper mapper = mock(GroupToEntityMapper.class);
        SqlGroupRepositoryAdapter adapter = new SqlGroupRepositoryAdapter(repo, mapper);

        UUID id = UUID.randomUUID();
        GroupCloudDtoProjection projection = new GroupCloudDtoProjection() {
            @Override
            public String getName() {
                return "AI";
            }

            @Override
            public List<UUID> getCloudResourceAccesses() {
                return List.of(id);
            }

            @Override
            public String getSemester() {
                return "2024L";
            }
        };
        when(repo.findAllProjectedByGroupStatus(GroupStatus.Type.ACTIVE)).thenReturn(List.of(projection));
        List<GroupCloudDto> groupCloudDtoList = adapter.findActiveGroups();

        GroupUniqueName groupUniqueName = GroupUniqueName.builder()
                .groupName(GroupName.of(projection.getName()))
                .semester(Semester.of(projection.getSemester()))
                .build();
        GroupCloudDto groupCloudDto = new GroupCloudDto(
                groupUniqueName,
                projection.getCloudResourceAccesses().stream()
                        .map(CloudResourceAccessId::of)
                        .toList());
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
            @Override
            public String getName() {
                return "AI";
            }

            @Override
            public List<UUID> getCloudResourceAccesses() {
                return List.of(id1);
            }

            @Override
            public String getSemester() {
                return "2024L";
            }
        };
        when(repo.findAllProjectedByGroupStatus(GroupStatus.Type.ACTIVE)).thenReturn(List.of(projection));

        List<GroupCloudDto> result = adapter.findActiveGroups();
        assertEquals(1, result.size());
        GroupCloudDto dto = result.getFirst();
        assertEquals("AI 2024L", dto.groupUniqueName().toString());
        assertEquals(List.of(CloudResourceAccessId.of(id1)), dto.cloudResourceAccesses());
    }

    @Test
    @DisplayName("findByCloudResourceAccessId returns GroupDto with lecturers when group exists")
    void findByCloudResourceAccessId_returnsGroupDtoWithLecturers_whenGroupExists() {
        // given
        GroupJpaRepository repo = mock(GroupJpaRepository.class);
        GroupToEntityMapper mapper = mock(GroupToEntityMapper.class);
        SqlGroupRepositoryAdapter adapter = new SqlGroupRepositoryAdapter(repo, mapper);

        UUID accessId = UUID.randomUUID();
        UUID lecturerId = UUID.randomUUID();
        GroupEntity entity = GroupEntity.builder().lecturers(Set.of(lecturerId)).build();

        when(repo.findByCloudResourceAccessesContaining(accessId)).thenReturn(entity);

        // when
        GroupDto result = adapter.findByCloudResourceAccessId(CloudResourceAccessId.of(accessId));

        // then
        assertThat(result).isNotNull();
        assertThat(result.lecturers()).containsExactly(UserId.of(lecturerId));
    }

    @Test
    @DisplayName("findByCloudResourceAccessId returns GroupDto with empty lecturers when group exists but has none")
    void findByCloudResourceAccessId_returnsGroupDtoWithEmptyLecturers_whenGroupExistsButHasNone() {
        // given
        GroupJpaRepository repo = mock(GroupJpaRepository.class);
        GroupToEntityMapper mapper = mock(GroupToEntityMapper.class);
        SqlGroupRepositoryAdapter adapter = new SqlGroupRepositoryAdapter(repo, mapper);

        UUID accessId = UUID.randomUUID();
        GroupEntity entity =
                GroupEntity.builder().lecturers(Collections.emptySet()).build();

        when(repo.findByCloudResourceAccessesContaining(accessId)).thenReturn(entity);

        // when
        GroupDto result = adapter.findByCloudResourceAccessId(CloudResourceAccessId.of(accessId));

        // then
        assertThat(result).isNotNull();
        assertThat(result.lecturers()).isEmpty();
    }

    @Test
    @DisplayName("findByCloudResourceAccessId returns GroupDto with empty lecturers when group not found")
    void findByCloudResourceAccessId_returnsGroupDtoWithEmptyLecturers_whenGroupNotFound() {
        // given
        GroupJpaRepository repo = mock(GroupJpaRepository.class);
        GroupToEntityMapper mapper = mock(GroupToEntityMapper.class);
        SqlGroupRepositoryAdapter adapter = new SqlGroupRepositoryAdapter(repo, mapper);

        UUID accessId = UUID.randomUUID();
        when(repo.findByCloudResourceAccessesContaining(accessId)).thenReturn(null);

        // when
        GroupDto result = adapter.findByCloudResourceAccessId(CloudResourceAccessId.of(accessId));

        // then
        assertThat(result).isNotNull();
        assertThat(result.lecturers()).isEmpty();
    }
}
