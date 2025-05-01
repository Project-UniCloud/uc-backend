package adapter;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.72.0)",
    comments = "Source: adapter_interface.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class CloudAdapterGrpc {

  private CloudAdapterGrpc() {}

  public static final java.lang.String SERVICE_NAME = "adapter.CloudAdapter";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<adapter.AdapterInterface.StatusRequest,
      adapter.AdapterInterface.StatusResponse> getGetStatusMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "GetStatus",
      requestType = adapter.AdapterInterface.StatusRequest.class,
      responseType = adapter.AdapterInterface.StatusResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<adapter.AdapterInterface.StatusRequest,
      adapter.AdapterInterface.StatusResponse> getGetStatusMethod() {
    io.grpc.MethodDescriptor<adapter.AdapterInterface.StatusRequest, adapter.AdapterInterface.StatusResponse> getGetStatusMethod;
    if ((getGetStatusMethod = CloudAdapterGrpc.getGetStatusMethod) == null) {
      synchronized (CloudAdapterGrpc.class) {
        if ((getGetStatusMethod = CloudAdapterGrpc.getGetStatusMethod) == null) {
          CloudAdapterGrpc.getGetStatusMethod = getGetStatusMethod =
              io.grpc.MethodDescriptor.<adapter.AdapterInterface.StatusRequest, adapter.AdapterInterface.StatusResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "GetStatus"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  adapter.AdapterInterface.StatusRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  adapter.AdapterInterface.StatusResponse.getDefaultInstance()))
              .setSchemaDescriptor(new CloudAdapterMethodDescriptorSupplier("GetStatus"))
              .build();
        }
      }
    }
    return getGetStatusMethod;
  }

  private static volatile io.grpc.MethodDescriptor<adapter.AdapterInterface.CreateUserRequest,
      adapter.AdapterInterface.UserCreatedResponse> getCreateUserMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CreateUser",
      requestType = adapter.AdapterInterface.CreateUserRequest.class,
      responseType = adapter.AdapterInterface.UserCreatedResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<adapter.AdapterInterface.CreateUserRequest,
      adapter.AdapterInterface.UserCreatedResponse> getCreateUserMethod() {
    io.grpc.MethodDescriptor<adapter.AdapterInterface.CreateUserRequest, adapter.AdapterInterface.UserCreatedResponse> getCreateUserMethod;
    if ((getCreateUserMethod = CloudAdapterGrpc.getCreateUserMethod) == null) {
      synchronized (CloudAdapterGrpc.class) {
        if ((getCreateUserMethod = CloudAdapterGrpc.getCreateUserMethod) == null) {
          CloudAdapterGrpc.getCreateUserMethod = getCreateUserMethod =
              io.grpc.MethodDescriptor.<adapter.AdapterInterface.CreateUserRequest, adapter.AdapterInterface.UserCreatedResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CreateUser"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  adapter.AdapterInterface.CreateUserRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  adapter.AdapterInterface.UserCreatedResponse.getDefaultInstance()))
              .setSchemaDescriptor(new CloudAdapterMethodDescriptorSupplier("CreateUser"))
              .build();
        }
      }
    }
    return getCreateUserMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static CloudAdapterStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<CloudAdapterStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<CloudAdapterStub>() {
        @java.lang.Override
        public CloudAdapterStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new CloudAdapterStub(channel, callOptions);
        }
      };
    return CloudAdapterStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports all types of calls on the service
   */
  public static CloudAdapterBlockingV2Stub newBlockingV2Stub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<CloudAdapterBlockingV2Stub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<CloudAdapterBlockingV2Stub>() {
        @java.lang.Override
        public CloudAdapterBlockingV2Stub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new CloudAdapterBlockingV2Stub(channel, callOptions);
        }
      };
    return CloudAdapterBlockingV2Stub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static CloudAdapterBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<CloudAdapterBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<CloudAdapterBlockingStub>() {
        @java.lang.Override
        public CloudAdapterBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new CloudAdapterBlockingStub(channel, callOptions);
        }
      };
    return CloudAdapterBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static CloudAdapterFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<CloudAdapterFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<CloudAdapterFutureStub>() {
        @java.lang.Override
        public CloudAdapterFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new CloudAdapterFutureStub(channel, callOptions);
        }
      };
    return CloudAdapterFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void getStatus(adapter.AdapterInterface.StatusRequest request,
        io.grpc.stub.StreamObserver<adapter.AdapterInterface.StatusResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getGetStatusMethod(), responseObserver);
    }

    /**
     */
    default void createUser(adapter.AdapterInterface.CreateUserRequest request,
        io.grpc.stub.StreamObserver<adapter.AdapterInterface.UserCreatedResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCreateUserMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service CloudAdapter.
   */
  public static abstract class CloudAdapterImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return CloudAdapterGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service CloudAdapter.
   */
  public static final class CloudAdapterStub
      extends io.grpc.stub.AbstractAsyncStub<CloudAdapterStub> {
    private CloudAdapterStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CloudAdapterStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new CloudAdapterStub(channel, callOptions);
    }

    /**
     */
    public void getStatus(adapter.AdapterInterface.StatusRequest request,
        io.grpc.stub.StreamObserver<adapter.AdapterInterface.StatusResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getGetStatusMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void createUser(adapter.AdapterInterface.CreateUserRequest request,
        io.grpc.stub.StreamObserver<adapter.AdapterInterface.UserCreatedResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCreateUserMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service CloudAdapter.
   */
  public static final class CloudAdapterBlockingV2Stub
      extends io.grpc.stub.AbstractBlockingStub<CloudAdapterBlockingV2Stub> {
    private CloudAdapterBlockingV2Stub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CloudAdapterBlockingV2Stub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new CloudAdapterBlockingV2Stub(channel, callOptions);
    }

    /**
     */
    public adapter.AdapterInterface.StatusResponse getStatus(adapter.AdapterInterface.StatusRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetStatusMethod(), getCallOptions(), request);
    }

    /**
     */
    public adapter.AdapterInterface.UserCreatedResponse createUser(adapter.AdapterInterface.CreateUserRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCreateUserMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do limited synchronous rpc calls to service CloudAdapter.
   */
  public static final class CloudAdapterBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<CloudAdapterBlockingStub> {
    private CloudAdapterBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CloudAdapterBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new CloudAdapterBlockingStub(channel, callOptions);
    }

    /**
     */
    public adapter.AdapterInterface.StatusResponse getStatus(adapter.AdapterInterface.StatusRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getGetStatusMethod(), getCallOptions(), request);
    }

    /**
     */
    public adapter.AdapterInterface.UserCreatedResponse createUser(adapter.AdapterInterface.CreateUserRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCreateUserMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service CloudAdapter.
   */
  public static final class CloudAdapterFutureStub
      extends io.grpc.stub.AbstractFutureStub<CloudAdapterFutureStub> {
    private CloudAdapterFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected CloudAdapterFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new CloudAdapterFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<adapter.AdapterInterface.StatusResponse> getStatus(
        adapter.AdapterInterface.StatusRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getGetStatusMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<adapter.AdapterInterface.UserCreatedResponse> createUser(
        adapter.AdapterInterface.CreateUserRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCreateUserMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_GET_STATUS = 0;
  private static final int METHODID_CREATE_USER = 1;

  private static final class MethodHandlers<Req, Resp> implements
      io.grpc.stub.ServerCalls.UnaryMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ServerStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.ClientStreamingMethod<Req, Resp>,
      io.grpc.stub.ServerCalls.BidiStreamingMethod<Req, Resp> {
    private final AsyncService serviceImpl;
    private final int methodId;

    MethodHandlers(AsyncService serviceImpl, int methodId) {
      this.serviceImpl = serviceImpl;
      this.methodId = methodId;
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public void invoke(Req request, io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        case METHODID_GET_STATUS:
          serviceImpl.getStatus((adapter.AdapterInterface.StatusRequest) request,
              (io.grpc.stub.StreamObserver<adapter.AdapterInterface.StatusResponse>) responseObserver);
          break;
        case METHODID_CREATE_USER:
          serviceImpl.createUser((adapter.AdapterInterface.CreateUserRequest) request,
              (io.grpc.stub.StreamObserver<adapter.AdapterInterface.UserCreatedResponse>) responseObserver);
          break;
        default:
          throw new AssertionError();
      }
    }

    @java.lang.Override
    @java.lang.SuppressWarnings("unchecked")
    public io.grpc.stub.StreamObserver<Req> invoke(
        io.grpc.stub.StreamObserver<Resp> responseObserver) {
      switch (methodId) {
        default:
          throw new AssertionError();
      }
    }
  }

  public static final io.grpc.ServerServiceDefinition bindService(AsyncService service) {
    return io.grpc.ServerServiceDefinition.builder(getServiceDescriptor())
        .addMethod(
          getGetStatusMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              adapter.AdapterInterface.StatusRequest,
              adapter.AdapterInterface.StatusResponse>(
                service, METHODID_GET_STATUS)))
        .addMethod(
          getCreateUserMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              adapter.AdapterInterface.CreateUserRequest,
              adapter.AdapterInterface.UserCreatedResponse>(
                service, METHODID_CREATE_USER)))
        .build();
  }

  private static abstract class CloudAdapterBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    CloudAdapterBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return adapter.AdapterInterface.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("CloudAdapter");
    }
  }

  private static final class CloudAdapterFileDescriptorSupplier
      extends CloudAdapterBaseDescriptorSupplier {
    CloudAdapterFileDescriptorSupplier() {}
  }

  private static final class CloudAdapterMethodDescriptorSupplier
      extends CloudAdapterBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    CloudAdapterMethodDescriptorSupplier(java.lang.String methodName) {
      this.methodName = methodName;
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.MethodDescriptor getMethodDescriptor() {
      return getServiceDescriptor().findMethodByName(methodName);
    }
  }

  private static volatile io.grpc.ServiceDescriptor serviceDescriptor;

  public static io.grpc.ServiceDescriptor getServiceDescriptor() {
    io.grpc.ServiceDescriptor result = serviceDescriptor;
    if (result == null) {
      synchronized (CloudAdapterGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new CloudAdapterFileDescriptorSupplier())
              .addMethod(getGetStatusMethod())
              .addMethod(getCreateUserMethod())
              .build();
        }
      }
    }
    return result;
  }
}
