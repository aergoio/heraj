package hera.client;

import java.io.Closeable;
import types.AergoRPCServiceGrpc.AergoRPCServiceBlockingStub;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.AergoRPCServiceGrpc.AergoRPCServiceStub;

interface GrpcClient extends Closeable {

  AergoRPCServiceBlockingStub getBlockingStub();

  AergoRPCServiceFutureStub getFutureStub();

  AergoRPCServiceStub getStreamStub();

  void close();

}
