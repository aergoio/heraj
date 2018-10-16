/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import hera.Context;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.BlockChainOperation;
import hera.api.model.BlockchainStatus;
import hera.api.model.NodeStatus;
import hera.api.model.PeerAddress;
import io.grpc.ManagedChannel;
import java.util.List;

@ApiAudience.Private
@ApiStability.Unstable
public class BlockChainTemplate implements BlockChainOperation, ChannelInjectable {

  protected Context context;

  protected BlockChainEitherTemplate blockChainEitherOperation =
      new BlockChainEitherTemplate();

  @Override
  public void setContext(final Context context) {
    this.context = context;
    blockChainEitherOperation.setContext(context);
  }

  @Override
  public void injectChannel(final ManagedChannel channel) {
    blockChainEitherOperation.injectChannel(channel);
  }

  @Override
  public BlockchainStatus getBlockchainStatus() {
    return blockChainEitherOperation.getBlockchainStatus().getResult();
  }

  @Override
  public List<PeerAddress> listPeers() {
    return blockChainEitherOperation.listPeers().getResult();
  }

  @Override
  public NodeStatus getNodeStatus() {
    return blockChainEitherOperation.getNodeStatus().getResult();
  }

}
