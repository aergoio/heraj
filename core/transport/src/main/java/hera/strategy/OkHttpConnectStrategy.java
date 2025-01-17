/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.HostnameAndPort;
import io.grpc.okhttp.OkHttpChannelBuilder;
import lombok.ToString;

import java.util.concurrent.TimeUnit;

@ApiAudience.Private
@ApiStability.Unstable
@ToString
public class OkHttpConnectStrategy implements ConnectStrategy<OkHttpChannelBuilder> {

  @Override
  public OkHttpChannelBuilder connect(final HostnameAndPort hostnameAndPort) {
    return connect(hostnameAndPort, null);
  }

  @Override
  public OkHttpChannelBuilder connect(HostnameAndPort hostnameAndPort, Long keepAliveTime) {
    OkHttpChannelBuilder builder = OkHttpChannelBuilder.
        forAddress(hostnameAndPort.getHostname(), hostnameAndPort.getPort());
    if (keepAliveTime != null) {
      builder = builder.keepAliveTime(keepAliveTime, TimeUnit.SECONDS).keepAliveWithoutCalls(true);
    }
    return builder;
  }
}
