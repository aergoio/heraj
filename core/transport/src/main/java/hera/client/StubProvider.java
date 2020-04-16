/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static types.AergoRPCServiceGrpc.newBlockingStub;
import static types.AergoRPCServiceGrpc.newFutureStub;
import static types.AergoRPCServiceGrpc.newStub;

import io.grpc.Channel;
import lombok.RequiredArgsConstructor;
import types.AergoRPCServiceGrpc.AergoRPCServiceBlockingStub;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.AergoRPCServiceGrpc.AergoRPCServiceStub;

@RequiredArgsConstructor
class StubProvider {

  protected final Object lock = new Object();
  protected volatile AergoRPCServiceBlockingStub blockingStub;
  protected volatile AergoRPCServiceFutureStub futureStub;
  protected volatile AergoRPCServiceStub streamStub;

  protected final ConnectionManager connectionManager;

  public AergoRPCServiceBlockingStub getBlockingStub() {
    if (null == blockingStub) {
      synchronized (lock) {
        if (null == blockingStub) {
          final Channel channel = connectionManager.getConnection();
          initStub(channel);
        }
      }
    }
    return blockingStub;
  }

  public AergoRPCServiceFutureStub getFutureStub() {
    if (null == futureStub) {
      synchronized (lock) {
        if (null == futureStub) {
          final Channel channel = connectionManager.getConnection();
          initStub(channel);
        }
      }
    }
    return futureStub;
  }

  public AergoRPCServiceStub getStreamStub() {
    if (null == streamStub) {
      synchronized (lock) {
        if (null == streamStub) {
          final Channel channel = connectionManager.getConnection();
          initStub(channel);
        }
      }
    }
    return streamStub;
  }

  protected void initStub(final Channel channel) {
    blockingStub = newBlockingStub(channel);
    futureStub = newFutureStub(channel);
    streamStub = newStub(channel);
  }

}
