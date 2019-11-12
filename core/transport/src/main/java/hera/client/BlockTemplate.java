/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.api.function.Functions.identify;
import static hera.client.ClientConstants.BLOCK_GET_BLOCK_BY_HASH;
import static hera.client.ClientConstants.BLOCK_GET_BLOCK_BY_HEIGHT;
import static hera.client.ClientConstants.BLOCK_GET_METADATA_BY_HASH;
import static hera.client.ClientConstants.BLOCK_GET_METADATA_BY_HEIGHT;
import static hera.client.ClientConstants.BLOCK_LIST_METADATAS_BY_HASH;
import static hera.client.ClientConstants.BLOCK_LIST_METADATAS_BY_HEIGHT;
import static hera.client.ClientConstants.BLOCK_SUBSCRIBE_BLOCK;
import static hera.client.ClientConstants.BLOCK_SUBSCRIBE_BLOCKMETADATA;

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
import hera.api.model.StreamObserver;
import hera.api.model.Subscription;
import hera.client.internal.BlockBaseTemplate;
import hera.exception.RpcException;
import hera.exception.RpcExceptionConverter;
import hera.strategy.PriorityProvider;
import hera.strategy.StrategyApplier;
import hera.util.ExceptionConverter;
import io.grpc.ManagedChannel;
import java.util.List;
import java.util.concurrent.Future;
import lombok.AccessLevel;
import lombok.Getter;

@ApiAudience.Private
@ApiStability.Unstable
public class BlockTemplate
    implements BlockOperation, ChannelInjectable, ContextProviderInjectable {

  protected final ExceptionConverter<RpcException> exceptionConverter = new RpcExceptionConverter();

  protected BlockBaseTemplate blockBaseTemplate = new BlockBaseTemplate();

  protected ContextProvider contextProvider;

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final StrategyApplier strategyApplier =
      StrategyApplier.of(contextProvider.get(), PriorityProvider.get());

  @Override
  public void setChannel(final ManagedChannel channel) {
    this.blockBaseTemplate.setChannel(channel);
  }

  @Override
  public void setContextProvider(ContextProvider contextProvider) {
    this.contextProvider = contextProvider;
    this.blockBaseTemplate.setContextProvider(contextProvider);
  }

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function1<BlockHash, Future<BlockMetadata>> blockMetadataByHashFunction =
      getStrategyApplier()
          .apply(identify(this.blockBaseTemplate.getBlockMetatdataByHashFunction(),
              BLOCK_GET_METADATA_BY_HASH));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function1<Long, Future<BlockMetadata>> blockMetadataByHeightFunction =
      getStrategyApplier().apply(
          identify(this.blockBaseTemplate.getBlockMetadataByHeightFunction(),
              BLOCK_GET_METADATA_BY_HEIGHT));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function2<BlockHash, Integer,
      Future<List<BlockMetadata>>> listBlockMetadatasByHashFunction =
          getStrategyApplier()
              .apply(identify(this.blockBaseTemplate.getListBlockMetadatasByHashFunction(),
                  BLOCK_LIST_METADATAS_BY_HASH));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function2<Long, Integer,
      Future<List<BlockMetadata>>> listBlockMetadatasByHeightFunction =
          getStrategyApplier()
              .apply(identify(this.blockBaseTemplate.getListBlockMetadatasByHeightFunction(),
                  BLOCK_LIST_METADATAS_BY_HEIGHT));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function1<BlockHash, Future<Block>> blockByHashFunction =
      getStrategyApplier()
          .apply(
              identify(this.blockBaseTemplate.getBlockByHashFunction(), BLOCK_GET_BLOCK_BY_HASH));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function1<Long, Future<Block>> blockByHeightFunction =
      getStrategyApplier().apply(
          identify(this.blockBaseTemplate.getBlockByHeightFunction(), BLOCK_GET_BLOCK_BY_HEIGHT));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function1<StreamObserver<BlockMetadata>,
      Future<Subscription<BlockMetadata>>> subscribeBlockMetadataFunction =
          getStrategyApplier().apply(
              identify(this.blockBaseTemplate.getSubscribeBlockMetadataFunction(),
                  BLOCK_SUBSCRIBE_BLOCKMETADATA));

  @Getter(lazy = true, value = AccessLevel.PROTECTED)
  private final Function1<StreamObserver<Block>,
      Future<Subscription<Block>>> subscribeBlockFunction =
          getStrategyApplier().apply(
              identify(this.blockBaseTemplate.getSubscribeBlockFunction(), BLOCK_SUBSCRIBE_BLOCK));

  @Override
  public BlockMetadata getBlockMetadata(final BlockHash blockHash) {
    try {
      return getBlockMetadataByHashFunction().apply(blockHash).get();
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public BlockMetadata getBlockMetadata(final long height) {
    try {
      return getBlockMetadataByHeightFunction().apply(height).get();
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public List<BlockMetadata> listBlockMetadatas(final BlockHash blockHash,
      final int size) {
    try {
      return getListBlockMetadatasByHashFunction().apply(blockHash, size).get();
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public List<BlockMetadata> listBlockMetadatas(final long height,
      final int size) {
    try {
      return getListBlockMetadatasByHeightFunction().apply(height, size).get();
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public Block getBlock(final BlockHash blockHash) {
    try {
      return getBlockByHashFunction().apply(blockHash).get();
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public Block getBlock(final long height) {
    try {
      return getBlockByHeightFunction().apply(height).get();
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public Subscription<BlockMetadata> subscribeNewBlockMetadata(
      final StreamObserver<BlockMetadata> observer) {
    try {
      return getSubscribeBlockMetadataFunction().apply(observer).get();
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

  @Override
  public Subscription<Block> subscribeNewBlock(final StreamObserver<Block> observer) {
    try {
      return getSubscribeBlockFunction().apply(observer).get();
    } catch (Exception e) {
      throw exceptionConverter.convert(e);
    }
  }

}
