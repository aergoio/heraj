/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.client.ClientContextKeys.GRPC_CONNECTION_ENDPOINT;
import static hera.client.ClientContextKeys.GRPC_CONNECTION_NEGOTIATION;
import static hera.client.ClientContextKeys.GRPC_CONNECTION_STRATEGY;
import static org.slf4j.LoggerFactory.getLogger;

import hera.Context;
import hera.ContextHolder;
import hera.api.model.HostnameAndPort;
import hera.exception.RpcException;
import hera.strategy.ChannelConfigurationStrategy;
import hera.strategy.ConnectStrategy;
import hera.strategy.NettyConnectStrategy;
import hera.strategy.PlainTextChannelStrategy;
import io.grpc.Channel;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import org.slf4j.Logger;

class GrpcConnectionManager implements ConnectionManager {

  protected final Object lock = new Object();

  protected final transient Logger logger = getLogger(getClass());

  protected volatile ManagedChannel channel;

  @Override
  public Channel getConnection() {
    if (null == channel) {
      synchronized (lock) {
        if (null == channel) {
          final Context current = ContextHolder.current();
          final ManagedChannelBuilder<?> raw = getChannelBuilder(current);
          final ManagedChannelBuilder<?> configured = configure(raw, current);
          channel = configured.build();
        }
      }
    }
    return channel;
  }

  protected ManagedChannelBuilder<?> getChannelBuilder(final Context context) {
    final HostnameAndPort hostnameAndPort = context.getOrDefault(GRPC_CONNECTION_ENDPOINT,
        HostnameAndPort.of("localhost:7845"));
    final ConnectStrategy connectStrategy = context.getOrDefault(GRPC_CONNECTION_STRATEGY,
        new NettyConnectStrategy());
    logger.debug("Use connection strategy: {} with endpoint: {}", connectStrategy, hostnameAndPort);
    return (ManagedChannelBuilder<?>) connectStrategy.connect(hostnameAndPort);
  }

  protected ManagedChannelBuilder<?> configure(final ManagedChannelBuilder<?> builder,
      final Context context) {
    final List<ChannelConfigurationStrategy> configurationStrategies = new ArrayList<>();
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
      throw new RpcException(e);
    }
  }

}
