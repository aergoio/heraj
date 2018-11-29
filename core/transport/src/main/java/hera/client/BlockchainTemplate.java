/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import hera.ContextProvider;
import hera.ContextProviderInjectable;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.BlockchainOperation;
import hera.api.model.BlockchainStatus;
import hera.api.model.NodeStatus;
import hera.api.model.Peer;
import io.grpc.ManagedChannel;
import java.util.List;

@ApiAudience.Private
@ApiStability.Unstable
public class BlockchainTemplate
    implements BlockchainOperation, ChannelInjectable, ContextProviderInjectable {

  protected BlockchainEitherTemplate blockchainEitherOperation =
      new BlockchainEitherTemplate();

  protected ContextProvider contextProvider;

  @Override
  public void setChannel(final ManagedChannel channel) {
    blockchainEitherOperation.setChannel(channel);
  }

  @Override
  public void setContextProvider(final ContextProvider contextProvider) {
    this.contextProvider = contextProvider;
    blockchainEitherOperation.setContextProvider(contextProvider);
  }

  @Override
  public BlockchainStatus getBlockchainStatus() {
    return blockchainEitherOperation.getBlockchainStatus().getResult();
  }

  @Override
  public List<Peer> listPeers() {
    return blockchainEitherOperation.listPeers().getResult();
  }

  @Override
  public NodeStatus getNodeStatus() {
    return blockchainEitherOperation.getNodeStatus().getResult();
  }

}
