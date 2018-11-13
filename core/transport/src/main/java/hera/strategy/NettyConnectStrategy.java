/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import hera.DefaultConstants;
import hera.api.model.HostnameAndPort;
import hera.util.Configurable;
import hera.util.Configuration;
import io.grpc.netty.NettyChannelBuilder;
import java.util.concurrent.TimeUnit;
import lombok.Setter;

public class NettyConnectStrategy
    implements ConnectStrategy<NettyChannelBuilder>, Configurable<Configuration> {

  @Setter
  protected Configuration configuration;

  protected HostnameAndPort getEndpoint() {
    return HostnameAndPort
        .of(configuration.getAsString("endpoint", DefaultConstants.DEFAULT_ENDPOINT));
  }

  @Override
  public NettyChannelBuilder connect() {
    final HostnameAndPort endpoint = getEndpoint();
    return NettyChannelBuilder
        .forAddress(endpoint.getHostname(), endpoint.getPort())
        .keepAliveTime(30, TimeUnit.SECONDS)
        .keepAliveTimeout(10, TimeUnit.SECONDS)
        .keepAliveWithoutCalls(true)
        .usePlaintext();
  }

}
