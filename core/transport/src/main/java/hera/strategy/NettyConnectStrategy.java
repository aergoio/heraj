/*
 * @copyright defined in LICENSE.txt
 */

package hera.strategy;

import static hera.DefaultConstants.DEFAULT_RPC_PORT;

import hera.api.model.HostnameAndPort;
import io.grpc.ManagedChannel;
import io.grpc.netty.NettyChannelBuilder;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class NettyConnectStrategy implements ConnectStrategy<ManagedChannel> {

  @NonNull
  protected final HostnameAndPort endpoint;

  @Override
  public ManagedChannel connect() {
    return NettyChannelBuilder
        .forAddress(endpoint.getHostname(), endpoint.getPort(DEFAULT_RPC_PORT)).usePlaintext()
        .build();
  }
}
