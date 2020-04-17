/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static types.AergoRPCServiceGrpc.newBlockingStub;
import static types.AergoRPCServiceGrpc.newFutureStub;
import static types.AergoRPCServiceGrpc.newStub;

import io.grpc.Channel;
import lombok.Getter;
import types.AergoRPCServiceGrpc.AergoRPCServiceBlockingStub;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.AergoRPCServiceGrpc.AergoRPCServiceStub;

class GrpcClient {

  @Getter
  protected final AergoRPCServiceBlockingStub blockingStub;

  @Getter
  protected final AergoRPCServiceFutureStub futureStub;

  @Getter
  protected final AergoRPCServiceStub streamStub;

  GrpcClient(final Channel channel) {
    blockingStub = newBlockingStub(channel);
    futureStub = newFutureStub(channel);
    streamStub = newStub(channel);
  }

}
