package com.unicloudapp.group.infrastructure.persistence;

import com.unicloudapp.common.domain.cloud.CloudResourceAccessId;
import com.unicloudapp.common.group.GroupCloudDto;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class SqlGroupRepositoryAdapterTest {

    @Test
    @DisplayName("findAllGroupCloudDto maps cloudResourceAccesses and uses GroupUniqueName.fromString; malformed name+semester throws")
    void findAllGroupCloudDto_malformedConcatenation_thenThrows() {
        GroupJpaRepository repo = mock(GroupJpaRepository.class);
        GroupToEntityMapper mapper = mock(GroupToEntityMapper.class);
        SqlGroupRepositoryAdapter adapter = new SqlGroupRepositoryAdapter(repo, mapper);

        // Projection without space between name and semester (as in current adapter implementation)
        GroupCloudDtoProjection projection = new GroupCloudDtoProjection() {
            @Override public String getName() { return "AI"; }
            @Override public List<UUID> getCloudResourceAccesses() { return List.of(UUID.randomUUID()); }
            @Override public String getSemester() { return "2024L"; }
        };
        when(repo.findAllProjectedBy()).thenReturn(List.of(projection));

        IllegalArgumentException ex = assertThrows(IllegalArgumentException.class, adapter::findAllGroupCloudDto);
        assertTrue(ex.getMessage().contains("Niepoprawny format"));
    }

    @Test
    @DisplayName("findAllGroupCloudDto maps projection to GroupCloudDto when name includes trailing space")
    void findAllGroupCloudDto_valid_whenNameEndsWithSpace() {
        GroupJpaRepository repo = mock(GroupJpaRepository.class);
        GroupToEntityMapper mapper = mock(GroupToEntityMapper.class);
        SqlGroupRepositoryAdapter adapter = new SqlGroupRepositoryAdapter(repo, mapper);

        UUID id1 = UUID.randomUUID();
        GroupCloudDtoProjection projection = new GroupCloudDtoProjection() {
            @Override public String getName() { return "AI "; }
            @Override public List<UUID> getCloudResourceAccesses() { return List.of(id1); }
            @Override public String getSemester() { return "2024L"; }
        };
        when(repo.findAllProjectedBy()).thenReturn(List.of(projection));

        List<GroupCloudDto> result = adapter.findAllGroupCloudDto();
        assertEquals(1, result.size());
        GroupCloudDto dto = result.getFirst();
        assertEquals("AI 2024L", dto.groupUniqueName().toString());
        assertEquals(List.of(CloudResourceAccessId.of(id1)), dto.cloudResourceAccesses());
    }
}
