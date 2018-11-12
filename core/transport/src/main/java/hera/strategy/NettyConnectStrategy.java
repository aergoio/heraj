/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import static hera.DefaultConstants.DEFAULT_RPC_PORT;

import hera.DefaultConstants;
import hera.api.model.HostnameAndPort;
import hera.util.Configurable;
import hera.util.Configuration;
import io.grpc.ManagedChannel;
import io.grpc.netty.NettyChannelBuilder;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class NettyConnectStrategy
    implements ConnectStrategy<ManagedChannel>, Configurable<Configuration> {

  @Setter
  protected Configuration configuration;

  protected HostnameAndPort getEndpoint() {
    return HostnameAndPort
        .of(configuration.getAsString("endpoint", DefaultConstants.DEFAULT_ENDPOINT));
  }

  @Override
  public ManagedChannel connect() {
    final HostnameAndPort endpoint = getEndpoint();
    return NettyChannelBuilder
        .forAddress(endpoint.getHostname(), endpoint.getPort(DEFAULT_RPC_PORT)).usePlaintext()
        .build();
  }

}
