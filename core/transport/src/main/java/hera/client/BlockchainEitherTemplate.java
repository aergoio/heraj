/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import hera.Context;
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
public class BlockchainEitherTemplate implements BlockchainEitherOperation, ChannelInjectable {

  protected BlockchainAsyncTemplate blockchainAsyncOperation = new BlockchainAsyncTemplate();

  @Override
  public void setContext(final Context context) {
    blockchainAsyncOperation.setContext(context);
  }

  @Override
  public void injectChannel(final ManagedChannel channel) {
    blockchainAsyncOperation.injectChannel(channel);
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
