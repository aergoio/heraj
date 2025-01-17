/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.HostnameAndPort;
import io.grpc.netty.shaded.io.grpc.netty.NettyChannelBuilder;
import java.util.concurrent.TimeUnit;
import lombok.ToString;

@ApiAudience.Private
@ApiStability.Unstable
@ToString
public class NettyConnectStrategy implements ConnectStrategy<NettyChannelBuilder> {

  @Override
  public NettyChannelBuilder connect(final HostnameAndPort hostnameAndPort) {
    return connect(hostnameAndPort, null);
  }

  @Override
  public NettyChannelBuilder connect(HostnameAndPort hostnameAndPort, Long keepAliveTime) {
    NettyChannelBuilder builder = NettyChannelBuilder.
        forAddress(hostnameAndPort.getHostname(), hostnameAndPort.getPort());
    if (keepAliveTime != null) {
      builder = builder.keepAliveTime(keepAliveTime, TimeUnit.SECONDS).keepAliveWithoutCalls(true);
    }
    return builder;
  }
}
