/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.client.ClientContextKeys.GRPC_CONNECTION_ENDPOINT;
import static hera.client.ClientContextKeys.GRPC_CONNECTION_NEGOTIATION;
import static hera.client.ClientContextKeys.GRPC_CONNECTION_STRATEGY;
import static org.slf4j.LoggerFactory.getLogger;
import static types.AergoRPCServiceGrpc.newBlockingStub;
import static types.AergoRPCServiceGrpc.newFutureStub;
import static types.AergoRPCServiceGrpc.newStub;

import hera.Context;
import hera.ContextHolder;
import hera.api.model.HostnameAndPort;
import hera.exception.HerajException;
import hera.strategy.ChannelConfigurationStrategy;
import hera.strategy.ConnectStrategy;
import hera.strategy.NettyConnectStrategy;
import hera.strategy.PlainTextChannelStrategy;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.io.Closeable;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;
import types.AergoRPCServiceGrpc.AergoRPCServiceBlockingStub;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.AergoRPCServiceGrpc.AergoRPCServiceStub;

class GrpcClient implements Closeable {

  protected final transient Logger logger = getLogger(getClass());

  protected final Object lock = new Object();
  protected volatile ManagedChannel channel;
  protected volatile AergoRPCServiceBlockingStub blockingStub;
  protected volatile AergoRPCServiceFutureStub futureStub;
  protected volatile AergoRPCServiceStub streamStub;

  GrpcClient() {
  }

  public AergoRPCServiceBlockingStub getBlockingStub() {
    if (null == channel) {
      synchronized (lock) {
        if (null == channel) {
          initStub();
        }
      }
    }
    return blockingStub;
  }

  public AergoRPCServiceFutureStub getFutureStub() {
    if (null == channel) {
      synchronized (lock) {
        if (null == channel) {
          initStub();
        }
      }
    }
    return futureStub;
  }

  public AergoRPCServiceStub getStreamStub() {
    if (null == channel) {
      synchronized (lock) {
        if (null == channel) {
          initStub();
        }
      }
    }
    return streamStub;
  }

  protected void initStub() {
    final Context current = ContextHolder.current();
    logger.trace("Context: {}", current);
    final ManagedChannelBuilder<?> raw = getChannelBuilder(current);
    final ManagedChannelBuilder<?> configured = configure(raw, current);
    final ManagedChannel channel = configured.build();
    this.channel = channel;
    this.blockingStub = newBlockingStub(channel);
    this.futureStub = newFutureStub(channel);
    this.streamStub = newStub(channel);
  }

  protected ManagedChannelBuilder<?> getChannelBuilder(final Context context) {
    final HostnameAndPort hostnameAndPort = context.getOrDefault(GRPC_CONNECTION_ENDPOINT,
        HostnameAndPort.of("localhost:7845"));
    final ConnectStrategy<?> connectStrategy = context.getOrDefault(GRPC_CONNECTION_STRATEGY,
        new NettyConnectStrategy());
    logger.debug("Use connection strategy: {} with endpoint: {}", connectStrategy, hostnameAndPort);
    return (ManagedChannelBuilder<?>) connectStrategy.connect(hostnameAndPort);
  }

  protected ManagedChannelBuilder<?> configure(final ManagedChannelBuilder<?> builder,
      final Context context) {
    final List<ChannelConfigurationStrategy> configurationStrategies = new LinkedList<>();
    configurationStrategies.add(context.getOrDefault(GRPC_CONNECTION_NEGOTIATION,
        new PlainTextChannelStrategy()));
    for (final ChannelConfigurationStrategy strategy : configurationStrategies) {
      logger.debug("Configure channel with: {}", strategy);
      strategy.configure(builder);
    }
    return builder;
  }

  @Override
  public void close() {
    try {
      if (null != this.channel) {
        this.channel.shutdown().awaitTermination(3, TimeUnit.SECONDS);
      }
    } catch (Throwable e) {
      logger.debug("Fail to close grpc client by {}", e.toString());
      throw new HerajException(e);
    }
  }

  @Override
  public String toString() {
    return String.format("GrpcClient(channel=%s)", channel);
  }

}
