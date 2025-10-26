package com.unicloudapp.group.application;

import com.unicloudapp.common.group.GroupCloudDto;
import com.unicloudapp.group.application.port.GroupRepositoryPort;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.*;

class BasicGroupQueryServiceTest {

    @Test
    @DisplayName("getGroupCloudDto delegates to repository")
    void getActiveGroups_delegates() {
        GroupRepositoryPort repo = mock(GroupRepositoryPort.class);
        BasicGroupQueryService svc = new BasicGroupQueryService(repo);
        List<GroupCloudDto> expected = List.of(mock(GroupCloudDto.class));
        when(repo.findActiveGroups()).thenReturn(expected);

        List<GroupCloudDto> actual = svc.getActiveGroups();

        verify(repo).findActiveGroups();
        assertSame(expected, actual);
    }
}
