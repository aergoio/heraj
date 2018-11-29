/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import hera.ContextProvider;
import hera.ContextProviderInjectable;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.BlockOperation;
import hera.api.model.Block;
import hera.api.model.BlockHash;
import hera.api.model.BlockHeader;
import io.grpc.ManagedChannel;
import java.util.List;

@ApiAudience.Private
@ApiStability.Unstable
public class BlockTemplate implements BlockOperation, ChannelInjectable, ContextProviderInjectable {

  protected BlockEitherTemplate blockEitherOperation = new BlockEitherTemplate();

  protected ContextProvider contextProvider;

  @Override
  public void setChannel(final ManagedChannel channel) {
    blockEitherOperation.setChannel(channel);
  }

  @Override
  public void setContextProvider(final ContextProvider contextProvider) {
    this.contextProvider = contextProvider;
    blockEitherOperation.setContextProvider(contextProvider);
  }

  @Override
  public Block getBlock(final BlockHash blockHash) {
    return blockEitherOperation.getBlock(blockHash).getResult();
  }

  @Override
  public Block getBlock(final long height) {
    return blockEitherOperation.getBlock(height).getResult();
  }

  @Override
  public List<BlockHeader> listBlockHeaders(final BlockHash blockHash, final int size) {
    return blockEitherOperation.listBlockHeaders(blockHash, size).getResult();
  }

  @Override
  public List<BlockHeader> listBlockHeaders(final long height, final int size) {
    return blockEitherOperation.listBlockHeaders(height, size).getResult();
  }

}
