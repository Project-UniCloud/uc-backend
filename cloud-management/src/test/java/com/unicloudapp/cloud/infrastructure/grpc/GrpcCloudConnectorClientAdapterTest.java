package com.unicloudapp.cloud.infrastructure.grpc;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

import adapter.AdapterInterface;
import adapter.CloudAdapterGrpc;
import com.unicloudapp.common.vo.cloud.CloudResourceType;
import io.grpc.Status;
import io.grpc.StatusRuntimeException;
import java.util.List;
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
}
