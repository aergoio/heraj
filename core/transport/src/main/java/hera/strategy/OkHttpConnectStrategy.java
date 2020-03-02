/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import static hera.client.ClientConstants.KEEP_ALIVE_INTERVAL;
import static org.slf4j.LoggerFactory.getLogger;

import hera.Context;
import hera.DefaultConstants;
import hera.api.model.internal.HostnameAndPort;
import io.grpc.okhttp.OkHttpChannelBuilder;
import java.util.concurrent.TimeUnit;
import lombok.Setter;
import lombok.ToString;
import org.slf4j.Logger;

@ToString(exclude = {"logger", "context"})
public class OkHttpConnectStrategy implements ConnectStrategy<OkHttpChannelBuilder> {

  protected final Logger logger = getLogger(getClass());

  @Setter
  protected Context context;

  protected HostnameAndPort getEndpoint() {
    return HostnameAndPort.of(this.context.getConfiguration().getAsString("endpoint",
        DefaultConstants.DEFAULT_ENDPOINT));
  }

  @Override
  public OkHttpChannelBuilder connect() {
    final HostnameAndPort endpoint = getEndpoint();
    logger.info("Connect to {} with strategy {}", endpoint, getClass().getName());
    return OkHttpChannelBuilder.forAddress(endpoint.getHostname(), endpoint.getPort())
        .keepAliveTime(KEEP_ALIVE_INTERVAL, TimeUnit.SECONDS)
        .keepAliveWithoutCalls(true);
  }

  @Override
  public boolean equals(final Object obj) {
    return (null != obj) && (obj instanceof ConnectStrategy);
  }

  @Override
  public int hashCode() {
    return OkHttpConnectStrategy.class.hashCode();
  }

}
