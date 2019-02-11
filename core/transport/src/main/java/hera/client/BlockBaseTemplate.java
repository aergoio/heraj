/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static com.google.common.util.concurrent.Futures.addCallback;
import static com.google.common.util.concurrent.MoreExecutors.directExecutor;
import static hera.util.TransportUtils.assertArgument;
import static hera.util.TransportUtils.copyFrom;
import static org.slf4j.LoggerFactory.getLogger;
import static types.AergoRPCServiceGrpc.newFutureStub;

import com.google.common.util.concurrent.ListenableFuture;
import hera.ContextProvider;
import hera.ContextProviderInjectable;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.function.Function1;
import hera.api.function.Function2;
import hera.api.model.Block;
import hera.api.model.BlockHash;
import hera.api.model.BlockHeader;
import hera.transport.BlockConverterFactory;
import hera.transport.BlockHeaderConverterFactory;
import hera.transport.ModelConverter;
import io.grpc.ManagedChannel;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import org.slf4j.Logger;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.Blockchain;
import types.Rpc;

@ApiAudience.Private
@ApiStability.Unstable
public class BlockBaseTemplate implements ChannelInjectable, ContextProviderInjectable {

  protected final Logger logger = getLogger(getClass());

  protected final ModelConverter<BlockHeader, Rpc.BlockMetadata> blockMetadataConverter =
      new BlockHeaderConverterFactory().create();

  protected final ModelConverter<Block, Blockchain.Block> blockConverter =
      new BlockConverterFactory().create();

  @Getter
  protected AergoRPCServiceFutureStub aergoService;

  protected ContextProvider contextProvider;

  @Override
  public void setChannel(final ManagedChannel channel) {
    this.aergoService = newFutureStub(channel);
  }

  @Override
  public void setContextProvider(final ContextProvider contextProvider) {
    this.contextProvider = contextProvider;
  }

  @Getter
  private final Function1<BlockHash, FinishableFuture<BlockHeader>> blockHeaderByHashFunction =
      new Function1<BlockHash, FinishableFuture<BlockHeader>>() {

        @Override
        public FinishableFuture<BlockHeader> apply(final BlockHash hash) {
          logger.debug("Get block header with hash: {}", hash);

          FinishableFuture<BlockHeader> nextFuture = new FinishableFuture<BlockHeader>();
          try {
            final Rpc.SingleBytes rpcHash = Rpc.SingleBytes.newBuilder()
                .setValue(copyFrom(hash.getBytesValue()))
                .build();
            logger.trace("AergoService getBlockMetadata arg: {}", rpcHash);

            ListenableFuture<Rpc.BlockMetadata> listenableFuture =
                aergoService.getBlockMetadata(rpcHash);
            FutureChain<Rpc.BlockMetadata, BlockHeader> callback =
                new FutureChain<Rpc.BlockMetadata, BlockHeader>(nextFuture,
                    contextProvider.get());
            callback.setSuccessHandler(
                new Function1<Rpc.BlockMetadata, BlockHeader>() {

                  @Override
                  public BlockHeader apply(final Rpc.BlockMetadata metadata) {
                    return blockMetadataConverter.convertToDomainModel(metadata);
                  }
                });
            addCallback(listenableFuture, callback, directExecutor());
          } catch (Exception e) {
            nextFuture.fail(e);
          }

          return nextFuture;
        }
      };

  @Getter
  private final Function1<Long, FinishableFuture<BlockHeader>> blockHeaderByHeightFunction =
      new Function1<Long, FinishableFuture<BlockHeader>>() {

        @Override
        public FinishableFuture<BlockHeader> apply(final Long height) {
          logger.debug("Get block header with height: {}", height);
          assertArgument(height >= 0, "Height", ">= 0");

          FinishableFuture<BlockHeader> nextFuture = new FinishableFuture<BlockHeader>();
          try {
            final Rpc.SingleBytes rpcHeight = Rpc.SingleBytes.newBuilder()
                .setValue(copyFrom(height.longValue()))
                .build();
            logger.trace("AergoService getBlockMetadata arg: {}", rpcHeight);

            ListenableFuture<Rpc.BlockMetadata> listenableFuture =
                aergoService.getBlockMetadata(rpcHeight);
            FutureChain<Rpc.BlockMetadata, BlockHeader> callback =
                new FutureChain<Rpc.BlockMetadata, BlockHeader>(nextFuture,
                    contextProvider.get());
            callback.setSuccessHandler(new Function1<Rpc.BlockMetadata, BlockHeader>() {
              @Override
              public BlockHeader apply(final Rpc.BlockMetadata metadata) {
                return blockMetadataConverter.convertToDomainModel(metadata);
              }
            });
            addCallback(listenableFuture, callback, directExecutor());
          } catch (Exception e) {
            nextFuture.fail(e);
          }

          return nextFuture;
        }
      };

  @Getter
  private final Function2<BlockHash, Integer,
      FinishableFuture<List<BlockHeader>>> listBlockHeadersByHashFunction = new Function2<BlockHash,
          Integer, FinishableFuture<List<BlockHeader>>>() {

        @Override
        public FinishableFuture<List<BlockHeader>> apply(final BlockHash hash,
            final Integer size) {
          logger.debug("List block headers with hash: {}, size: {}", hash, size);
          assertArgument(size > 0, "Block list size", "postive");

          FinishableFuture<List<BlockHeader>> nextFuture =
              new FinishableFuture<List<BlockHeader>>();
          try {
            final Rpc.ListParams rpcHashAndSize = Rpc.ListParams.newBuilder()
                .setHash(copyFrom(hash.getBytesValue()))
                .setSize(size)
                .build();
            logger.trace("AergoService listBlockMetadata arg: {}", rpcHashAndSize);

            ListenableFuture<Rpc.BlockMetadataList> listenableFuture =
                aergoService.listBlockMetadata(rpcHashAndSize);
            FutureChain<Rpc.BlockMetadataList, List<BlockHeader>> callback =
                new FutureChain<Rpc.BlockMetadataList, List<BlockHeader>>(nextFuture,
                    contextProvider.get());
            callback.setSuccessHandler(
                new Function1<Rpc.BlockMetadataList, List<BlockHeader>>() {

                  @Override
                  public List<BlockHeader> apply(final Rpc.BlockMetadataList metadatas) {
                    final List<BlockHeader> blockHeaders = new ArrayList<BlockHeader>();
                    for (final Rpc.BlockMetadata metadata : metadatas.getBlocksList()) {
                      blockHeaders
                          .add(blockMetadataConverter.convertToDomainModel(metadata));
                    }
                    return blockHeaders;
                  }
                });
            addCallback(listenableFuture, callback, directExecutor());
          } catch (Exception e) {
            nextFuture.fail(e);
          }

          return nextFuture;
        }
      };

  @Getter
  private final Function2<Long, Integer,
      FinishableFuture<List<BlockHeader>>> listBlockHeadersByHeightFunction = new Function2<
          Long, Integer, FinishableFuture<List<BlockHeader>>>() {

        @Override
        public FinishableFuture<List<BlockHeader>> apply(final Long height,
            final Integer size) {
          logger.debug("List block headers with height: {}, size: {}", height, size);
          assertArgument(height >= 0, "Height", ">= 0");
          assertArgument(size > 0, "Block list size", "postive");

          FinishableFuture<List<BlockHeader>> nextFuture =
              new FinishableFuture<List<BlockHeader>>();
          try {
            final Rpc.ListParams rpcHeightAndSize = Rpc.ListParams.newBuilder()
                .setHeight(height)
                .setSize(size)
                .build();
            logger.trace("AergoService listBlockMetadata arg: {}", rpcHeightAndSize);

            ListenableFuture<Rpc.BlockMetadataList> listenableFuture =
                aergoService.listBlockMetadata(rpcHeightAndSize);
            FutureChain<Rpc.BlockMetadataList, List<BlockHeader>> callback =
                new FutureChain<Rpc.BlockMetadataList, List<BlockHeader>>(nextFuture,
                    contextProvider.get());
            callback
                .setSuccessHandler(new Function1<Rpc.BlockMetadataList, List<BlockHeader>>() {
                  @Override
                  public List<BlockHeader> apply(final Rpc.BlockMetadataList metadatas) {
                    final List<BlockHeader> blockHeaders = new ArrayList<BlockHeader>();
                    for (final Rpc.BlockMetadata metadata : metadatas.getBlocksList()) {
                      blockHeaders.add(blockMetadataConverter.convertToDomainModel(metadata));
                    }
                    return blockHeaders;
                  }
                });
            addCallback(listenableFuture, callback, directExecutor());
          } catch (Exception e) {
            nextFuture.fail(e);
          }

          return nextFuture;
        }
      };

  @Getter
  private final Function1<BlockHash,
      FinishableFuture<Block>> blockByHashFunction = new Function1<
          BlockHash, FinishableFuture<Block>>() {

        @Override
        public FinishableFuture<Block> apply(final BlockHash hash) {
          logger.debug("Get block with hash: {}", hash);

          FinishableFuture<Block> nextFuture = new FinishableFuture<Block>();
          try {
            final Rpc.SingleBytes rpcHash = Rpc.SingleBytes.newBuilder()
                .setValue(copyFrom(hash.getBytesValue()))
                .build();
            logger.trace("AergoService getBlock arg: {}", rpcHash);

            ListenableFuture<Blockchain.Block> listenableFuture = aergoService.getBlock(rpcHash);
            FutureChain<Blockchain.Block, Block> callback =
                new FutureChain<Blockchain.Block, Block>(nextFuture, contextProvider.get());
            callback.setSuccessHandler(new Function1<Blockchain.Block, Block>() {
              @Override
              public Block apply(final Blockchain.Block block) {
                return blockConverter.convertToDomainModel(block);
              }
            });
            addCallback(listenableFuture, callback, directExecutor());
          } catch (Exception e) {
            nextFuture.fail(e);
          }

          return nextFuture;
        }
      };

  @Getter
  private final Function1<Long,
      FinishableFuture<Block>> blockByHeightFunction = new Function1<
          Long, FinishableFuture<Block>>() {

        @Override
        public FinishableFuture<Block> apply(final Long height) {
          logger.debug("Get block with height: {}", height);
          assertArgument(height >= 0, "Height", ">= 0");

          FinishableFuture<Block> nextFuture = new FinishableFuture<Block>();
          try {
            final Rpc.SingleBytes rpcHeight = Rpc.SingleBytes.newBuilder()
                .setValue(copyFrom(height.longValue()))
                .build();
            logger.trace("AergoService getBlock arg: {}", rpcHeight);

            ListenableFuture<Blockchain.Block> listenableFuture = aergoService.getBlock(rpcHeight);
            FutureChain<Blockchain.Block, Block> callback =
                new FutureChain<Blockchain.Block, Block>(nextFuture, contextProvider.get());
            callback.setSuccessHandler(new Function1<Blockchain.Block, Block>() {
              @Override
              public Block apply(final Blockchain.Block block) {
                return blockConverter.convertToDomainModel(block);
              }
            });
            addCallback(listenableFuture, callback, directExecutor());
          } catch (Exception e) {
            nextFuture.fail(e);
          }

          return nextFuture;
        }
      };

}
