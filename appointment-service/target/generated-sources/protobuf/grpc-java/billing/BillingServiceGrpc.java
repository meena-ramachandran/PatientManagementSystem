package billing;

import static io.grpc.MethodDescriptor.generateFullMethodName;

/**
 */
@javax.annotation.Generated(
    value = "by gRPC proto compiler (version 1.68.1)",
    comments = "Source: billing_service.proto")
@io.grpc.stub.annotations.GrpcGenerated
public final class BillingServiceGrpc {

  private BillingServiceGrpc() {}

  public static final java.lang.String SERVICE_NAME = "BillingService";

  // Static method descriptors that strictly reflect the proto.
  private static volatile io.grpc.MethodDescriptor<billing.BillingRequest,
      billing.BillingResponse> getCreateBillingAccountMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CreateBillingAccount",
      requestType = billing.BillingRequest.class,
      responseType = billing.BillingResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<billing.BillingRequest,
      billing.BillingResponse> getCreateBillingAccountMethod() {
    io.grpc.MethodDescriptor<billing.BillingRequest, billing.BillingResponse> getCreateBillingAccountMethod;
    if ((getCreateBillingAccountMethod = BillingServiceGrpc.getCreateBillingAccountMethod) == null) {
      synchronized (BillingServiceGrpc.class) {
        if ((getCreateBillingAccountMethod = BillingServiceGrpc.getCreateBillingAccountMethod) == null) {
          BillingServiceGrpc.getCreateBillingAccountMethod = getCreateBillingAccountMethod =
              io.grpc.MethodDescriptor.<billing.BillingRequest, billing.BillingResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CreateBillingAccount"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  billing.BillingRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  billing.BillingResponse.getDefaultInstance()))
              .setSchemaDescriptor(new BillingServiceMethodDescriptorSupplier("CreateBillingAccount"))
              .build();
        }
      }
    }
    return getCreateBillingAccountMethod;
  }

  private static volatile io.grpc.MethodDescriptor<billing.BillingAmountRequest,
      billing.BillingResponse> getCreditBillingAccountMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "CreditBillingAccount",
      requestType = billing.BillingAmountRequest.class,
      responseType = billing.BillingResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<billing.BillingAmountRequest,
      billing.BillingResponse> getCreditBillingAccountMethod() {
    io.grpc.MethodDescriptor<billing.BillingAmountRequest, billing.BillingResponse> getCreditBillingAccountMethod;
    if ((getCreditBillingAccountMethod = BillingServiceGrpc.getCreditBillingAccountMethod) == null) {
      synchronized (BillingServiceGrpc.class) {
        if ((getCreditBillingAccountMethod = BillingServiceGrpc.getCreditBillingAccountMethod) == null) {
          BillingServiceGrpc.getCreditBillingAccountMethod = getCreditBillingAccountMethod =
              io.grpc.MethodDescriptor.<billing.BillingAmountRequest, billing.BillingResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "CreditBillingAccount"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  billing.BillingAmountRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  billing.BillingResponse.getDefaultInstance()))
              .setSchemaDescriptor(new BillingServiceMethodDescriptorSupplier("CreditBillingAccount"))
              .build();
        }
      }
    }
    return getCreditBillingAccountMethod;
  }

  private static volatile io.grpc.MethodDescriptor<billing.BillingAmountRequest,
      billing.BillingResponse> getChargeBillingAccountMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "ChargeBillingAccount",
      requestType = billing.BillingAmountRequest.class,
      responseType = billing.BillingResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<billing.BillingAmountRequest,
      billing.BillingResponse> getChargeBillingAccountMethod() {
    io.grpc.MethodDescriptor<billing.BillingAmountRequest, billing.BillingResponse> getChargeBillingAccountMethod;
    if ((getChargeBillingAccountMethod = BillingServiceGrpc.getChargeBillingAccountMethod) == null) {
      synchronized (BillingServiceGrpc.class) {
        if ((getChargeBillingAccountMethod = BillingServiceGrpc.getChargeBillingAccountMethod) == null) {
          BillingServiceGrpc.getChargeBillingAccountMethod = getChargeBillingAccountMethod =
              io.grpc.MethodDescriptor.<billing.BillingAmountRequest, billing.BillingResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "ChargeBillingAccount"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  billing.BillingAmountRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  billing.BillingResponse.getDefaultInstance()))
              .setSchemaDescriptor(new BillingServiceMethodDescriptorSupplier("ChargeBillingAccount"))
              .build();
        }
      }
    }
    return getChargeBillingAccountMethod;
  }

  private static volatile io.grpc.MethodDescriptor<billing.BillingPatientRequest,
      billing.BillingResponse> getDeleteBillingAccountByPatientIdMethod;

  @io.grpc.stub.annotations.RpcMethod(
      fullMethodName = SERVICE_NAME + '/' + "DeleteBillingAccountByPatientId",
      requestType = billing.BillingPatientRequest.class,
      responseType = billing.BillingResponse.class,
      methodType = io.grpc.MethodDescriptor.MethodType.UNARY)
  public static io.grpc.MethodDescriptor<billing.BillingPatientRequest,
      billing.BillingResponse> getDeleteBillingAccountByPatientIdMethod() {
    io.grpc.MethodDescriptor<billing.BillingPatientRequest, billing.BillingResponse> getDeleteBillingAccountByPatientIdMethod;
    if ((getDeleteBillingAccountByPatientIdMethod = BillingServiceGrpc.getDeleteBillingAccountByPatientIdMethod) == null) {
      synchronized (BillingServiceGrpc.class) {
        if ((getDeleteBillingAccountByPatientIdMethod = BillingServiceGrpc.getDeleteBillingAccountByPatientIdMethod) == null) {
          BillingServiceGrpc.getDeleteBillingAccountByPatientIdMethod = getDeleteBillingAccountByPatientIdMethod =
              io.grpc.MethodDescriptor.<billing.BillingPatientRequest, billing.BillingResponse>newBuilder()
              .setType(io.grpc.MethodDescriptor.MethodType.UNARY)
              .setFullMethodName(generateFullMethodName(SERVICE_NAME, "DeleteBillingAccountByPatientId"))
              .setSampledToLocalTracing(true)
              .setRequestMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  billing.BillingPatientRequest.getDefaultInstance()))
              .setResponseMarshaller(io.grpc.protobuf.ProtoUtils.marshaller(
                  billing.BillingResponse.getDefaultInstance()))
              .setSchemaDescriptor(new BillingServiceMethodDescriptorSupplier("DeleteBillingAccountByPatientId"))
              .build();
        }
      }
    }
    return getDeleteBillingAccountByPatientIdMethod;
  }

  /**
   * Creates a new async stub that supports all call types for the service
   */
  public static BillingServiceStub newStub(io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<BillingServiceStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<BillingServiceStub>() {
        @java.lang.Override
        public BillingServiceStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new BillingServiceStub(channel, callOptions);
        }
      };
    return BillingServiceStub.newStub(factory, channel);
  }

  /**
   * Creates a new blocking-style stub that supports unary and streaming output calls on the service
   */
  public static BillingServiceBlockingStub newBlockingStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<BillingServiceBlockingStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<BillingServiceBlockingStub>() {
        @java.lang.Override
        public BillingServiceBlockingStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new BillingServiceBlockingStub(channel, callOptions);
        }
      };
    return BillingServiceBlockingStub.newStub(factory, channel);
  }

  /**
   * Creates a new ListenableFuture-style stub that supports unary calls on the service
   */
  public static BillingServiceFutureStub newFutureStub(
      io.grpc.Channel channel) {
    io.grpc.stub.AbstractStub.StubFactory<BillingServiceFutureStub> factory =
      new io.grpc.stub.AbstractStub.StubFactory<BillingServiceFutureStub>() {
        @java.lang.Override
        public BillingServiceFutureStub newStub(io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
          return new BillingServiceFutureStub(channel, callOptions);
        }
      };
    return BillingServiceFutureStub.newStub(factory, channel);
  }

  /**
   */
  public interface AsyncService {

    /**
     */
    default void createBillingAccount(billing.BillingRequest request,
        io.grpc.stub.StreamObserver<billing.BillingResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCreateBillingAccountMethod(), responseObserver);
    }

    /**
     */
    default void creditBillingAccount(billing.BillingAmountRequest request,
        io.grpc.stub.StreamObserver<billing.BillingResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getCreditBillingAccountMethod(), responseObserver);
    }

    /**
     */
    default void chargeBillingAccount(billing.BillingAmountRequest request,
        io.grpc.stub.StreamObserver<billing.BillingResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getChargeBillingAccountMethod(), responseObserver);
    }

    /**
     */
    default void deleteBillingAccountByPatientId(billing.BillingPatientRequest request,
        io.grpc.stub.StreamObserver<billing.BillingResponse> responseObserver) {
      io.grpc.stub.ServerCalls.asyncUnimplementedUnaryCall(getDeleteBillingAccountByPatientIdMethod(), responseObserver);
    }
  }

  /**
   * Base class for the server implementation of the service BillingService.
   */
  public static abstract class BillingServiceImplBase
      implements io.grpc.BindableService, AsyncService {

    @java.lang.Override public final io.grpc.ServerServiceDefinition bindService() {
      return BillingServiceGrpc.bindService(this);
    }
  }

  /**
   * A stub to allow clients to do asynchronous rpc calls to service BillingService.
   */
  public static final class BillingServiceStub
      extends io.grpc.stub.AbstractAsyncStub<BillingServiceStub> {
    private BillingServiceStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected BillingServiceStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new BillingServiceStub(channel, callOptions);
    }

    /**
     */
    public void createBillingAccount(billing.BillingRequest request,
        io.grpc.stub.StreamObserver<billing.BillingResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCreateBillingAccountMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void creditBillingAccount(billing.BillingAmountRequest request,
        io.grpc.stub.StreamObserver<billing.BillingResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getCreditBillingAccountMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void chargeBillingAccount(billing.BillingAmountRequest request,
        io.grpc.stub.StreamObserver<billing.BillingResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getChargeBillingAccountMethod(), getCallOptions()), request, responseObserver);
    }

    /**
     */
    public void deleteBillingAccountByPatientId(billing.BillingPatientRequest request,
        io.grpc.stub.StreamObserver<billing.BillingResponse> responseObserver) {
      io.grpc.stub.ClientCalls.asyncUnaryCall(
          getChannel().newCall(getDeleteBillingAccountByPatientIdMethod(), getCallOptions()), request, responseObserver);
    }
  }

  /**
   * A stub to allow clients to do synchronous rpc calls to service BillingService.
   */
  public static final class BillingServiceBlockingStub
      extends io.grpc.stub.AbstractBlockingStub<BillingServiceBlockingStub> {
    private BillingServiceBlockingStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected BillingServiceBlockingStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new BillingServiceBlockingStub(channel, callOptions);
    }

    /**
     */
    public billing.BillingResponse createBillingAccount(billing.BillingRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCreateBillingAccountMethod(), getCallOptions(), request);
    }

    /**
     */
    public billing.BillingResponse creditBillingAccount(billing.BillingAmountRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getCreditBillingAccountMethod(), getCallOptions(), request);
    }

    /**
     */
    public billing.BillingResponse chargeBillingAccount(billing.BillingAmountRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getChargeBillingAccountMethod(), getCallOptions(), request);
    }

    /**
     */
    public billing.BillingResponse deleteBillingAccountByPatientId(billing.BillingPatientRequest request) {
      return io.grpc.stub.ClientCalls.blockingUnaryCall(
          getChannel(), getDeleteBillingAccountByPatientIdMethod(), getCallOptions(), request);
    }
  }

  /**
   * A stub to allow clients to do ListenableFuture-style rpc calls to service BillingService.
   */
  public static final class BillingServiceFutureStub
      extends io.grpc.stub.AbstractFutureStub<BillingServiceFutureStub> {
    private BillingServiceFutureStub(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      super(channel, callOptions);
    }

    @java.lang.Override
    protected BillingServiceFutureStub build(
        io.grpc.Channel channel, io.grpc.CallOptions callOptions) {
      return new BillingServiceFutureStub(channel, callOptions);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<billing.BillingResponse> createBillingAccount(
        billing.BillingRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCreateBillingAccountMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<billing.BillingResponse> creditBillingAccount(
        billing.BillingAmountRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getCreditBillingAccountMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<billing.BillingResponse> chargeBillingAccount(
        billing.BillingAmountRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getChargeBillingAccountMethod(), getCallOptions()), request);
    }

    /**
     */
    public com.google.common.util.concurrent.ListenableFuture<billing.BillingResponse> deleteBillingAccountByPatientId(
        billing.BillingPatientRequest request) {
      return io.grpc.stub.ClientCalls.futureUnaryCall(
          getChannel().newCall(getDeleteBillingAccountByPatientIdMethod(), getCallOptions()), request);
    }
  }

  private static final int METHODID_CREATE_BILLING_ACCOUNT = 0;
  private static final int METHODID_CREDIT_BILLING_ACCOUNT = 1;
  private static final int METHODID_CHARGE_BILLING_ACCOUNT = 2;
  private static final int METHODID_DELETE_BILLING_ACCOUNT_BY_PATIENT_ID = 3;

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
        case METHODID_CREATE_BILLING_ACCOUNT:
          serviceImpl.createBillingAccount((billing.BillingRequest) request,
              (io.grpc.stub.StreamObserver<billing.BillingResponse>) responseObserver);
          break;
        case METHODID_CREDIT_BILLING_ACCOUNT:
          serviceImpl.creditBillingAccount((billing.BillingAmountRequest) request,
              (io.grpc.stub.StreamObserver<billing.BillingResponse>) responseObserver);
          break;
        case METHODID_CHARGE_BILLING_ACCOUNT:
          serviceImpl.chargeBillingAccount((billing.BillingAmountRequest) request,
              (io.grpc.stub.StreamObserver<billing.BillingResponse>) responseObserver);
          break;
        case METHODID_DELETE_BILLING_ACCOUNT_BY_PATIENT_ID:
          serviceImpl.deleteBillingAccountByPatientId((billing.BillingPatientRequest) request,
              (io.grpc.stub.StreamObserver<billing.BillingResponse>) responseObserver);
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
          getCreateBillingAccountMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              billing.BillingRequest,
              billing.BillingResponse>(
                service, METHODID_CREATE_BILLING_ACCOUNT)))
        .addMethod(
          getCreditBillingAccountMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              billing.BillingAmountRequest,
              billing.BillingResponse>(
                service, METHODID_CREDIT_BILLING_ACCOUNT)))
        .addMethod(
          getChargeBillingAccountMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              billing.BillingAmountRequest,
              billing.BillingResponse>(
                service, METHODID_CHARGE_BILLING_ACCOUNT)))
        .addMethod(
          getDeleteBillingAccountByPatientIdMethod(),
          io.grpc.stub.ServerCalls.asyncUnaryCall(
            new MethodHandlers<
              billing.BillingPatientRequest,
              billing.BillingResponse>(
                service, METHODID_DELETE_BILLING_ACCOUNT_BY_PATIENT_ID)))
        .build();
  }

  private static abstract class BillingServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoFileDescriptorSupplier, io.grpc.protobuf.ProtoServiceDescriptorSupplier {
    BillingServiceBaseDescriptorSupplier() {}

    @java.lang.Override
    public com.google.protobuf.Descriptors.FileDescriptor getFileDescriptor() {
      return billing.BillingServiceOuterClass.getDescriptor();
    }

    @java.lang.Override
    public com.google.protobuf.Descriptors.ServiceDescriptor getServiceDescriptor() {
      return getFileDescriptor().findServiceByName("BillingService");
    }
  }

  private static final class BillingServiceFileDescriptorSupplier
      extends BillingServiceBaseDescriptorSupplier {
    BillingServiceFileDescriptorSupplier() {}
  }

  private static final class BillingServiceMethodDescriptorSupplier
      extends BillingServiceBaseDescriptorSupplier
      implements io.grpc.protobuf.ProtoMethodDescriptorSupplier {
    private final java.lang.String methodName;

    BillingServiceMethodDescriptorSupplier(java.lang.String methodName) {
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
      synchronized (BillingServiceGrpc.class) {
        result = serviceDescriptor;
        if (result == null) {
          serviceDescriptor = result = io.grpc.ServiceDescriptor.newBuilder(SERVICE_NAME)
              .setSchemaDescriptor(new BillingServiceFileDescriptorSupplier())
              .addMethod(getCreateBillingAccountMethod())
              .addMethod(getCreditBillingAccountMethod())
              .addMethod(getChargeBillingAccountMethod())
              .addMethod(getDeleteBillingAccountByPatientIdMethod())
              .build();
        }
      }
    }
    return result;
  }
}
