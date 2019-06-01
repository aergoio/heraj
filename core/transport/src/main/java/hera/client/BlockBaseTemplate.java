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
import hera.api.model.BlockMetadata;
import hera.transport.BlockConverterFactory;
import hera.transport.BlockMetadataConverterFactory;
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

  protected final transient Logger logger = getLogger(getClass());

  protected final ModelConverter<BlockMetadata, types.Rpc.BlockMetadata> blockMetadataConverter =
      new BlockMetadataConverterFactory().create();

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
  private final Function1<BlockHash, FinishableFuture<BlockMetadata>> blockMetatdataByHashFunction =
      new Function1<BlockHash, FinishableFuture<BlockMetadata>>() {

        @Override
        public FinishableFuture<BlockMetadata> apply(final BlockHash hash) {
          logger.debug("Get block metadata with hash: {}", hash);

          FinishableFuture<BlockMetadata> nextFuture = new FinishableFuture<BlockMetadata>();
          try {
            final Rpc.SingleBytes rpcHash = Rpc.SingleBytes.newBuilder()
                .setValue(copyFrom(hash.getBytesValue()))
                .build();
            logger.trace("AergoService getBlockMetadata arg: {}", rpcHash);

            ListenableFuture<Rpc.BlockMetadata> listenableFuture =
                aergoService.getBlockMetadata(rpcHash);
            FutureChain<Rpc.BlockMetadata, BlockMetadata> callback =
                new FutureChain<Rpc.BlockMetadata, BlockMetadata>(nextFuture,
                    contextProvider.get());
            callback.setSuccessHandler(
                new Function1<Rpc.BlockMetadata, BlockMetadata>() {

                  @Override
                  public BlockMetadata apply(final Rpc.BlockMetadata metadata) {
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
  private final Function1<Long, FinishableFuture<BlockMetadata>> blockMetadataByHeightFunction =
      new Function1<Long, FinishableFuture<BlockMetadata>>() {

        @Override
        public FinishableFuture<BlockMetadata> apply(final Long height) {
          logger.debug("Get block metadata with height: {}", height);
          assertArgument(height >= 0, "Height", ">= 0");

          FinishableFuture<BlockMetadata> nextFuture = new FinishableFuture<BlockMetadata>();
          try {
            final Rpc.SingleBytes rpcHeight = Rpc.SingleBytes.newBuilder()
                .setValue(copyFrom(height.longValue()))
                .build();
            logger.trace("AergoService getBlockMetadata arg: {}", rpcHeight);

            ListenableFuture<Rpc.BlockMetadata> listenableFuture =
                aergoService.getBlockMetadata(rpcHeight);
            FutureChain<Rpc.BlockMetadata, BlockMetadata> callback =
                new FutureChain<Rpc.BlockMetadata, BlockMetadata>(nextFuture,
                    contextProvider.get());
            callback.setSuccessHandler(new Function1<Rpc.BlockMetadata, BlockMetadata>() {
              @Override
              public BlockMetadata apply(final Rpc.BlockMetadata metadata) {
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
      FinishableFuture<List<BlockMetadata>>> listBlockMetadatasByHashFunction = new Function2<
          BlockHash, Integer, FinishableFuture<List<BlockMetadata>>>() {

        @Override
        public FinishableFuture<List<BlockMetadata>> apply(final BlockHash hash,
            final Integer size) {
          logger.debug("List block meta datas with hash: {}, size: {}", hash, size);
          assertArgument(size > 0, "Block list size", "postive");

          FinishableFuture<List<BlockMetadata>> nextFuture =
              new FinishableFuture<List<BlockMetadata>>();
          try {
            final Rpc.ListParams rpcHashAndSize = Rpc.ListParams.newBuilder()
                .setHash(copyFrom(hash.getBytesValue()))
                .setSize(size)
                .build();
            logger.trace("AergoService listBlockMetadata arg: {}", rpcHashAndSize);

            ListenableFuture<Rpc.BlockMetadataList> listenableFuture =
                aergoService.listBlockMetadata(rpcHashAndSize);
            FutureChain<Rpc.BlockMetadataList, List<BlockMetadata>> callback =
                new FutureChain<Rpc.BlockMetadataList, List<BlockMetadata>>(nextFuture,
                    contextProvider.get());
            callback.setSuccessHandler(
                new Function1<Rpc.BlockMetadataList, List<BlockMetadata>>() {

                  @Override
                  public List<BlockMetadata> apply(final Rpc.BlockMetadataList metadatas) {
                    final List<BlockMetadata> blockMetadatas = new ArrayList<BlockMetadata>();
                    for (final Rpc.BlockMetadata rpcBlockMetadata : metadatas.getBlocksList()) {
                      blockMetadatas
                          .add(blockMetadataConverter.convertToDomainModel(rpcBlockMetadata));
                    }
                    return blockMetadatas;
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
      FinishableFuture<List<BlockMetadata>>> listBlockMetadatasByHeightFunction = new Function2<
          Long, Integer, FinishableFuture<List<BlockMetadata>>>() {

        @Override
        public FinishableFuture<List<BlockMetadata>> apply(final Long height,
            final Integer size) {
          logger.debug("List block meta datas with height: {}, size: {}", height, size);
          assertArgument(height >= 0, "Height", ">= 0");
          assertArgument(size > 0, "Block list size", "postive");

          FinishableFuture<List<BlockMetadata>> nextFuture =
              new FinishableFuture<List<BlockMetadata>>();
          try {
            final Rpc.ListParams rpcHeightAndSize = Rpc.ListParams.newBuilder()
                .setHeight(height)
                .setSize(size)
                .build();
            logger.trace("AergoService listBlockMetadata arg: {}", rpcHeightAndSize);

            ListenableFuture<Rpc.BlockMetadataList> listenableFuture =
                aergoService.listBlockMetadata(rpcHeightAndSize);
            FutureChain<Rpc.BlockMetadataList, List<BlockMetadata>> callback =
                new FutureChain<Rpc.BlockMetadataList, List<BlockMetadata>>(nextFuture,
                    contextProvider.get());
            callback
                .setSuccessHandler(new Function1<Rpc.BlockMetadataList, List<BlockMetadata>>() {
                  @Override
                  public List<BlockMetadata> apply(final Rpc.BlockMetadataList metadatas) {
                    final List<BlockMetadata> blockHeaders = new ArrayList<BlockMetadata>();
                    for (final Rpc.BlockMetadata rpcBlockMetadata : metadatas.getBlocksList()) {
                      blockHeaders
                          .add(blockMetadataConverter.convertToDomainModel(rpcBlockMetadata));
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
