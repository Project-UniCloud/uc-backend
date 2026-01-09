package com.unicloudapp.cloud.infrastructure.grpc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import adapter.AdapterInterface;
import adapter.CloudAdapterGrpc;
import com.unicloudapp.common.group.GroupUniqueName;
import com.unicloudapp.common.vo.cloud.CloudResourceDetail;
import com.unicloudapp.common.vo.cloud.CloudResourceType;
import com.unicloudapp.common.vo.user.UserLogin;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

class GrpcCloudConnectorClientAdapterTest {

    private CloudAdapterGrpc.CloudAdapterBlockingStub stub;
    private GrpcCloudConnectorClientAdapter adapter;

    @BeforeEach
    void setUp() {
        stub = Mockito.mock(CloudAdapterGrpc.CloudAdapterBlockingStub.class);
        adapter = new GrpcCloudConnectorClientAdapter(stub);
    }

    @Test
    void getSupportedResourceTypes_returnsMappedList_whenServiceImplemented() {
        AdapterInterface.GetAvailableServicesResponse response =
                AdapterInterface.GetAvailableServicesResponse.newBuilder()
                        .addServices("EC2")
                        .addServices("S3")
                        .build();
        when(stub.getAvailableServices(any())).thenReturn(response);

        List<CloudResourceType> result = adapter.getSupportedResourceTypes();

        assertThat(result).containsExactly(CloudResourceType.of("EC2"), CloudResourceType.of("S3"));
    }

    @Test
    void getSupportedResourceTypes_returnsEmptyList_whenUnimplemented() {
        when(stub.getAvailableServices(any())).thenThrow(new StatusRuntimeException(Status.UNIMPLEMENTED));

        List<CloudResourceType> result = adapter.getSupportedResourceTypes();

        assertThat(result).isEmpty();
    }

    @Test
    void getSupportedResourceTypes_rethrows_forOtherStatusCodes() {
        when(stub.getAvailableServices(any())).thenThrow(new StatusRuntimeException(Status.INTERNAL));

        assertThrows(StatusRuntimeException.class, () -> adapter.getSupportedResourceTypes());
    }

    @Test
    void removeUser_returnsMessage_whenSuccessful() {
        UserLogin user = UserLogin.of("test-user");
        GroupUniqueName groupUniqueName = GroupUniqueName.fromString("test-group 2023Z");
        AdapterInterface.DeleteUserResponse response = AdapterInterface.DeleteUserResponse.newBuilder()
                .setMessage("User removed successfully")
                .build();
        when(stub.deleteUser(any())).thenReturn(response);

        String result = adapter.removeUser(user, groupUniqueName);

        assertThat(result).isEqualTo("User removed successfully");
    }

    @Test
    void addLecturerForGroup_returnsSuccess_whenGrcpReturnsSuccess() {
        GroupUniqueName groupUniqueName = GroupUniqueName.fromString("test-group 2023Z");
        UserLogin lecturerLogin = UserLogin.of("lecturer1");
        AdapterInterface.AddLeaderToGroupResponse response = AdapterInterface.AddLeaderToGroupResponse.newBuilder()
                .setSuccess(true)
                .setMessage("Lecturer added successfully")
                .build();
        when(stub.addLeaderToGroup(any())).thenReturn(response);

        Map.Entry<Boolean, String> result = adapter.addLecturerForGroup(groupUniqueName, lecturerLogin);

        assertThat(result.getKey()).isTrue();
        assertThat(result.getValue()).isEqualTo("Lecturer added successfully");
    }

    @Test
    void addLecturerForGroup_returnsFailure_whenGrcpReturnsFailure() {
        GroupUniqueName groupUniqueName = GroupUniqueName.fromString("test-group 2023Z");
        UserLogin lecturerLogin = UserLogin.of("lecturer1");
        AdapterInterface.AddLeaderToGroupResponse response = AdapterInterface.AddLeaderToGroupResponse.newBuilder()
                .setSuccess(false)
                .setMessage("Failed to add lecturer")
                .build();
        when(stub.addLeaderToGroup(any())).thenReturn(response);

        Map.Entry<Boolean, String> result = adapter.addLecturerForGroup(groupUniqueName, lecturerLogin);

        assertThat(result.getKey()).isFalse();
        assertThat(result.getValue()).isEqualTo("Failed to add lecturer");
    }

    @Test
    void getGroupResourcesList_returnsMappedList_whenSuccess() {
        GroupUniqueName groupUniqueName = GroupUniqueName.fromString("test-group 2023Z");
        AdapterInterface.ResourceDetail resourceProto = AdapterInterface.ResourceDetail.newBuilder()
                .setArn("arn:aws:ec2:region:account:instance/i-1234567890abcdef0")
                .setName("test-instance")
                .setType("instance")
                .setService("ec2")
                .setCreatedBy("user1")
                .setResourceId("i-1234567890abcdef0")
                .build();
        AdapterInterface.GetGroupResourcesListResponse response =
                AdapterInterface.GetGroupResourcesListResponse.newBuilder()
                        .setSuccess(true)
                        .addResources(resourceProto)
                        .build();
        when(stub.getGroupResourcesList(any())).thenReturn(response);

        List<CloudResourceDetail> result = adapter.getGroupResourcesList(groupUniqueName);

        assertThat(result).hasSize(1);
        CloudResourceDetail detail = result.getFirst();
        assertThat(detail.getArn()).isEqualTo(resourceProto.getArn());
        assertThat(detail.getName()).isEqualTo(resourceProto.getName());
        assertThat(detail.getType()).isEqualTo(resourceProto.getType());
        assertThat(detail.getService()).isEqualTo(resourceProto.getService());
        assertThat(detail.getCreatedBy()).isEqualTo(resourceProto.getCreatedBy());
        assertThat(detail.getResourceId()).isEqualTo(resourceProto.getResourceId());
    }

    @Test
    void getGroupResourcesList_returnsEmptyList_whenFailure() {
        GroupUniqueName groupUniqueName = GroupUniqueName.fromString("test-group 2023Z");
        AdapterInterface.GetGroupResourcesListResponse response =
                AdapterInterface.GetGroupResourcesListResponse.newBuilder()
                        .setSuccess(false)
                        .setMessage("Error")
                        .build();
        when(stub.getGroupResourcesList(any())).thenReturn(response);

        List<CloudResourceDetail> result = adapter.getGroupResourcesList(groupUniqueName);

        assertThat(result).isEmpty();
    }
}
