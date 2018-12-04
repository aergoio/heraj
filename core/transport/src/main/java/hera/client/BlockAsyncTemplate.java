/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.TransportConstants.BLOCK_GETBLOCK_BY_HASH_ASYNC;
import static hera.TransportConstants.BLOCK_GETBLOCK_BY_HEIGHT_ASYNC;
import static hera.TransportConstants.BLOCK_LIST_HEADERS_BY_HASH_ASYNC;
import static hera.TransportConstants.BLOCK_LIST_HEADERS_BY_HEIGHT_ASYNC;
import static hera.api.tupleorerror.Functions.identify;

import hera.ContextProvider;
import hera.ContextProviderInjectable;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.BlockAsyncOperation;
import hera.api.model.Block;
import hera.api.model.BlockHash;
import hera.api.model.BlockHeader;
import hera.api.tupleorerror.Function1;
import hera.api.tupleorerror.Function2;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.strategy.StrategyChain;
import io.grpc.ManagedChannel;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@ApiAudience.Private
@ApiStability.Unstable
public class BlockAsyncTemplate
    implements BlockAsyncOperation, ChannelInjectable, ContextProviderInjectable {

  protected BlockBaseTemplate blockBaseTemplate = new BlockBaseTemplate();

  @Setter
  protected ContextProvider contextProvider;

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final StrategyChain strategyChain = StrategyChain.of(contextProvider.get());

  @Override
  public void setChannel(final ManagedChannel channel) {
    blockBaseTemplate.setChannel(channel);
  }

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function1<BlockHash, ResultOrErrorFuture<Block>> blockByHashFunction =
      getStrategyChain().apply(
          identify(blockBaseTemplate.getBlockByHashFunction(), BLOCK_GETBLOCK_BY_HASH_ASYNC));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function1<Long, ResultOrErrorFuture<Block>> blockByHeightFunction =
      getStrategyChain().apply(
          identify(blockBaseTemplate.getBlockByHeightFunction(), BLOCK_GETBLOCK_BY_HEIGHT_ASYNC));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function2<BlockHash, Integer,
      ResultOrErrorFuture<List<BlockHeader>>> listBlockHeadersByHashFunction =
          getStrategyChain().apply(identify(blockBaseTemplate.getListBlockHeadersByHashFunction(),
              BLOCK_LIST_HEADERS_BY_HASH_ASYNC));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function2<Long, Integer,
      ResultOrErrorFuture<List<BlockHeader>>> listBlockHeadersByHeightFunction =
          getStrategyChain().apply(identify(blockBaseTemplate.getListBlockHeadersByHeightFunction(),
              BLOCK_LIST_HEADERS_BY_HEIGHT_ASYNC));

  @Override
  public ResultOrErrorFuture<Block> getBlock(final BlockHash blockHash) {
    return getBlockByHashFunction().apply(blockHash);
  }

  @Override
  public ResultOrErrorFuture<Block> getBlock(final long height) {
    return getBlockByHeightFunction().apply(height);
  }

  @Override
  public ResultOrErrorFuture<List<BlockHeader>> listBlockHeaders(final BlockHash blockHash,
      final int size) {
    return getListBlockHeadersByHashFunction().apply(blockHash, size);
  }

  @Override
  public ResultOrErrorFuture<List<BlockHeader>> listBlockHeaders(final long height,
      final int size) {
    return getListBlockHeadersByHeightFunction().apply(height, size);
  }

}
