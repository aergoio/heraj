/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import hera.api.model.HostnameAndPort;
import io.grpc.okhttp.OkHttpChannelBuilder;
import java.util.concurrent.TimeUnit;
import lombok.ToString;

@ToString
public class OkHttpConnectStrategy implements ConnectStrategy<OkHttpChannelBuilder> {

  @Override
  public OkHttpChannelBuilder connect(final HostnameAndPort hostnameAndPort) {
    return OkHttpChannelBuilder.forAddress(hostnameAndPort.getHostname(), hostnameAndPort.getPort())
        .keepAliveTime(300L, TimeUnit.SECONDS)
        .keepAliveWithoutCalls(true);
  }

}
