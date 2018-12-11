/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import hera.ContextHolder;
import hera.DefaultConstants;
import hera.api.model.HostnameAndPort;
import io.grpc.netty.NettyChannelBuilder;
import java.util.concurrent.TimeUnit;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@ToString
@EqualsAndHashCode
public class NettyConnectStrategy implements ConnectStrategy<NettyChannelBuilder> {

  protected HostnameAndPort getEndpoint() {
    return HostnameAndPort.of(ContextHolder.get().getConfiguration().getAsString("endpoint",
        DefaultConstants.DEFAULT_ENDPOINT));
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
