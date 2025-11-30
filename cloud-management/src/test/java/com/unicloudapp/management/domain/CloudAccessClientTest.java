package com.unicloudapp.management.domain;

import com.unicloudapp.common.vo.cloud.CloudAccessClientId;
import com.unicloudapp.common.vo.cloud.CloudResourceType;
import com.unicloudapp.common.vo.user.UserLogin;
import com.unicloudapp.common.group.GroupUniqueName;
import com.unicloudapp.management.domain.access.CloudResourceAccessFactory;
import com.unicloudapp.management.domain.access_client.CloudResourceAccessClient;
import com.unicloudapp.management.domain.access_client.CloudResourceAccessClientController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.mockito.Mockito.*;

class CloudResourceAccessClientTest {

    @Test
    @DisplayName("createGroup delegates to controller (method changed to void)")
    void createGroup_delegates() {
        CloudResourceAccessClientController controller = mock(CloudResourceAccessClientController.class);
        CloudResourceAccessClient client = CloudResourceAccessClient.builder()
                .CloudAccessClientId(CloudAccessClientId.of("test-client"))
                .controller(controller)
                .name("Test")
                .resourceTypes(List.of(CloudResourceType.of("S3")))
                .CloudResourceAccessFactory(new CloudResourceAccessFactory())
                .build();

        GroupUniqueName groupUniqueName = GroupUniqueName.fromString("AI 2024L");
        List<UserLogin> lecturers = List.of(UserLogin.of("john"));
        CloudResourceType type = CloudResourceType.of("S3");

        client.createGroup(groupUniqueName, lecturers, type);

        verify(controller).createGroup(groupUniqueName, lecturers, type);
        verifyNoMoreInteractions(controller);
    }
}
