package com.unicloudapp.cloud.domain;

import com.unicloudapp.cloud.application.port.CloudConnectorClientPort;
import com.unicloudapp.cloud.domain.connector.CloudConnector;
import com.unicloudapp.common.group.GroupUniqueName;
import com.unicloudapp.common.vo.cloud.CloudResourceType;
import com.unicloudapp.common.vo.cloud.CloudConnectorId;
import com.unicloudapp.common.vo.user.UserLogin;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;

class CloudConnectorTest {

    @Test
    @DisplayName("createGroup delegates to controller (method changed to void)")
    void createGroup_delegates() {
        CloudConnectorClientPort controller = mock(CloudConnectorClientPort.class);
        CloudConnector client = CloudConnector.builder()
                .cloudConnectorId(CloudConnectorId.of("test-client"))
                .name("Test")
                .resourceTypes(List.of(CloudResourceType.of("S3")))
                .build();

        GroupUniqueName groupUniqueName = GroupUniqueName.fromString("AI 2024L");
        List<UserLogin> lecturers = List.of(UserLogin.of("john"));
        CloudResourceType type = CloudResourceType.of("S3");

        client.createGroup(groupUniqueName, lecturers, type);

        verify(controller).createGroup(groupUniqueName, lecturers, type);
        verifyNoMoreInteractions(controller);
    }
}
