/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import static hera.DefaultConstants.DEFAULT_ENDPOINT;
import static hera.DefaultConstants.DEFAULT_RPC_PORT;

import hera.api.model.HostnameAndPort;
import hera.util.Configurable;
import hera.util.Configuration;
import io.grpc.ManagedChannel;
import io.grpc.netty.NettyChannelBuilder;
import lombok.Setter;

public class NettyConnectStrategy
    implements ConnectStrategy<ManagedChannel>, Configurable<Configuration> {

  @Setter
  protected Configuration configuration;

  protected HostnameAndPort getEndpoint() {
    return HostnameAndPort.of(configuration.getAsString("endpoint", DEFAULT_ENDPOINT));
  }

  /**
   * Connect to {@code endpoint}.
   *
   * @param endpoint endpoint to connect to
   *
   * @return channel to be connected
   */
  public ManagedChannel connect(final HostnameAndPort endpoint) {
    return NettyChannelBuilder
        .forAddress(endpoint.getHostname(), endpoint.getPort(DEFAULT_RPC_PORT))
        .usePlaintext()
        .build();
  }

  @Override
  public ManagedChannel connect() {
    return connect(getEndpoint());
  }
}
