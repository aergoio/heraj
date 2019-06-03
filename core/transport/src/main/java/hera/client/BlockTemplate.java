/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.TransportConstants.BLOCK_GET_BLOCK_BY_HASH;
import static hera.TransportConstants.BLOCK_GET_BLOCK_BY_HEIGHT;
import static hera.TransportConstants.BLOCK_GET_METADATA_BY_HASH;
import static hera.TransportConstants.BLOCK_GET_METADATA_BY_HEIGHT;
import static hera.TransportConstants.BLOCK_LIST_METADATAS_BY_HASH;
import static hera.TransportConstants.BLOCK_LIST_METADATAS_BY_HEIGHT;
import static hera.api.function.Functions.identify;

import hera.ContextProvider;
import hera.ContextProviderInjectable;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.BlockOperation;
import hera.api.function.Function1;
import hera.api.function.Function2;
import hera.api.model.Block;
import hera.api.model.BlockHash;
import hera.api.model.BlockMetadata;
import hera.client.internal.BlockBaseTemplate;
import hera.client.internal.FinishableFuture;
import hera.strategy.StrategyChain;
import io.grpc.ManagedChannel;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;

@ApiAudience.Private
@ApiStability.Unstable
public class BlockTemplate
    implements BlockOperation, ChannelInjectable, ContextProviderInjectable {

  @Getter
  protected BlockBaseTemplate blockBaseTemplate = new BlockBaseTemplate();

  protected ContextProvider contextProvider;

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final StrategyChain strategyChain = StrategyChain.of(contextProvider.get());

  @Override
  public void setChannel(final ManagedChannel channel) {
    getBlockBaseTemplate().setChannel(channel);
  }

  @Override
  public void setContextProvider(ContextProvider contextProvider) {
    this.contextProvider = contextProvider;
    getBlockBaseTemplate().setContextProvider(contextProvider);
  }

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function1<BlockHash, FinishableFuture<BlockMetadata>> blockMetadataByHashFunction =
      getStrategyChain()
          .apply(identify(getBlockBaseTemplate().getBlockMetatdataByHashFunction(),
              BLOCK_GET_METADATA_BY_HASH));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function1<Long, FinishableFuture<BlockMetadata>> blockMetadataByHeightFunction =
      getStrategyChain().apply(
          identify(getBlockBaseTemplate().getBlockMetadataByHeightFunction(),
              BLOCK_GET_METADATA_BY_HEIGHT));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function2<BlockHash, Integer,
      FinishableFuture<List<BlockMetadata>>> listBlockMetadatasByHashFunction =
          getStrategyChain()
              .apply(identify(getBlockBaseTemplate().getListBlockMetadatasByHashFunction(),
                  BLOCK_LIST_METADATAS_BY_HASH));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function2<Long, Integer,
      FinishableFuture<List<BlockMetadata>>> listBlockMetadatasByHeightFunction =
          getStrategyChain()
              .apply(identify(getBlockBaseTemplate().getListBlockMetadatasByHeightFunction(),
                  BLOCK_LIST_METADATAS_BY_HEIGHT));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function1<BlockHash, FinishableFuture<Block>> blockByHashFunction =
      getStrategyChain()
          .apply(
              identify(getBlockBaseTemplate().getBlockByHashFunction(), BLOCK_GET_BLOCK_BY_HASH));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function1<Long, FinishableFuture<Block>> blockByHeightFunction =
      getStrategyChain().apply(
          identify(getBlockBaseTemplate().getBlockByHeightFunction(), BLOCK_GET_BLOCK_BY_HEIGHT));

  @Override
  public BlockMetadata getBlockMetadata(final BlockHash blockHash) {
    return getBlockMetadataByHashFunction().apply(blockHash).get();
  }

  @Override
  public BlockMetadata getBlockMetadata(final long height) {
    return getBlockMetadataByHeightFunction().apply(height).get();
  }

  @Override
  public List<BlockMetadata> listBlockMetadatas(final BlockHash blockHash,
      final int size) {
    return getListBlockMetadatasByHashFunction().apply(blockHash, size).get();
  }

  @Override
  public List<BlockMetadata> listBlockMetadatas(final long height,
      final int size) {
    return getListBlockMetadatasByHeightFunction().apply(height, size).get();
  }

  @Override
  public Block getBlock(final BlockHash blockHash) {
    return getBlockByHashFunction().apply(blockHash).get();
  }

  @Override
  public Block getBlock(final long height) {
    return getBlockByHeightFunction().apply(height).get();
  }

}
