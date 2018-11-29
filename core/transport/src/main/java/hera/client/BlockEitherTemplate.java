/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import hera.ContextProvider;
import hera.ContextProviderInjectable;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.BlockEitherOperation;
import hera.api.model.Block;
import hera.api.model.BlockHash;
import hera.api.model.BlockHeader;
import hera.api.tupleorerror.ResultOrError;
import io.grpc.ManagedChannel;
import java.util.List;

@ApiAudience.Private
@ApiStability.Unstable
public class BlockEitherTemplate
    implements BlockEitherOperation, ChannelInjectable, ContextProviderInjectable {

  protected BlockAsyncTemplate blockAsyncOperation = new BlockAsyncTemplate();

  protected ContextProvider contextProvider;

  @Override
  public void setChannel(final ManagedChannel channel) {
    blockAsyncOperation.setChannel(channel);
  }

  @Override
  public void setContextProvider(final ContextProvider contextProvider) {
    this.contextProvider = contextProvider;
    blockAsyncOperation.setContextProvider(contextProvider);
  }

  @Override
  public ResultOrError<Block> getBlock(final BlockHash blockHash) {
    return blockAsyncOperation.getBlock(blockHash).get();
  }

  @Override
  public ResultOrError<Block> getBlock(final long height) {
    return blockAsyncOperation.getBlock(height).get();
  }

  @Override
  public ResultOrError<List<BlockHeader>> listBlockHeaders(final BlockHash blockHash,
      final int size) {
    return blockAsyncOperation.listBlockHeaders(blockHash, size).get();
  }

  @Override
  public ResultOrError<List<BlockHeader>> listBlockHeaders(final long height, final int size) {
    return blockAsyncOperation.listBlockHeaders(height, size).get();
  }

}

