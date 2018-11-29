/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import hera.ContextProvider;
import hera.ContextProviderInjectable;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.BlockchainEitherOperation;
import hera.api.model.BlockchainStatus;
import hera.api.model.NodeStatus;
import hera.api.model.Peer;
import hera.api.tupleorerror.ResultOrError;
import io.grpc.ManagedChannel;
import java.util.List;

@ApiAudience.Private
@ApiStability.Unstable
public class BlockchainEitherTemplate
    implements BlockchainEitherOperation, ChannelInjectable, ContextProviderInjectable {

  protected BlockchainAsyncTemplate blockchainAsyncOperation = new BlockchainAsyncTemplate();

  protected ContextProvider contextProvider;

  @Override
  public void setChannel(final ManagedChannel channel) {
    blockchainAsyncOperation.setChannel(channel);
  }

  @Override
  public void setContextProvider(final ContextProvider contextProvider) {
    this.contextProvider = contextProvider;
    blockchainAsyncOperation.setContextProvider(contextProvider);
  }

  @Override
  public ResultOrError<BlockchainStatus> getBlockchainStatus() {
    return blockchainAsyncOperation.getBlockchainStatus().get();
  }

  @Override
  public ResultOrError<List<Peer>> listPeers() {
    return blockchainAsyncOperation.listPeers().get();
  }

  @Override
  public ResultOrError<NodeStatus> getNodeStatus() {
    return blockchainAsyncOperation.getNodeStatus().get();
  }

}
