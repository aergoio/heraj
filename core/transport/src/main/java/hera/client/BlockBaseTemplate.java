/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static com.google.common.util.concurrent.Futures.addCallback;
import static com.google.common.util.concurrent.MoreExecutors.directExecutor;
import static hera.util.TransportUtils.assertArgument;
import static hera.util.TransportUtils.copyFrom;
import static hera.util.TransportUtils.longToByteArray;
import static org.slf4j.LoggerFactory.getLogger;
import static types.AergoRPCServiceGrpc.newFutureStub;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.ByteString;
import hera.ContextProvider;
import hera.ContextProviderInjectable;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.function.Function1;
import hera.api.function.Function2;
import hera.api.model.Block;
import hera.api.model.BlockHash;
import hera.api.model.BlockHeader;
import hera.api.model.BytesValue;
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

  protected final Logger logger = getLogger(getClass());

  protected final ModelConverter<BlockHeader, Rpc.BlockMetadata> blockMetadataConverter =
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
  private final Function1<BlockHash,
      FinishableFuture<Block>> blockByHashFunction = new Function1<
          BlockHash, FinishableFuture<Block>>() {

        @Override
        public FinishableFuture<Block> apply(final BlockHash hash) {
          if (logger.isDebugEnabled()) {
            logger.debug("Get block by hash: {}, Context: {}", hash, contextProvider.get());
          }

          FinishableFuture<Block> nextFuture = new FinishableFuture<Block>();

          try {
            final ByteString byteString = copyFrom(hash.getBytesValue());
            final Rpc.SingleBytes bytes =
                Rpc.SingleBytes.newBuilder().setValue(byteString).build();
            ListenableFuture<Blockchain.Block> listenableFuture = aergoService.getBlock(bytes);

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
          assertArgument(height >= 0, "Height", ">= 0");

          if (logger.isDebugEnabled()) {
            logger.debug("Get block by height: {}, Context: {}", height, contextProvider.get());
          }

          FinishableFuture<Block> nextFuture = new FinishableFuture<Block>();

          try {
            final ByteString byteString = copyFrom(BytesValue.of(longToByteArray(height)));
            final Rpc.SingleBytes bytes =
                Rpc.SingleBytes.newBuilder().setValue(byteString).build();
            ListenableFuture<Blockchain.Block> listenableFuture = aergoService.getBlock(bytes);

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
  private final Function2<BlockHash, Integer,
      FinishableFuture<List<BlockHeader>>> listBlockHeadersByHashFunction = new Function2<BlockHash,
          Integer, FinishableFuture<List<BlockHeader>>>() {

        @Override
        public FinishableFuture<List<BlockHeader>> apply(final BlockHash hash,
            final Integer size) {
          assertArgument(size > 0, "Block list size", "postive");

          if (logger.isDebugEnabled()) {
            logger.debug("List block headers by hash: {}, size: {}, Context: {}", hash,
                size, contextProvider.get());
          }

          FinishableFuture<List<BlockHeader>> nextFuture =
              new FinishableFuture<List<BlockHeader>>();

          try {
            final Rpc.ListParams listParams = Rpc.ListParams.newBuilder()
                .setHash(copyFrom(hash.getBytesValue())).setSize(size).build();
            ListenableFuture<Rpc.BlockMetadataList> listenableFuture =
                aergoService.listBlockMetadata(listParams);

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
          assertArgument(height >= 0, "Height", ">= 0");
          assertArgument(size > 0, "Block list size", "postive");

          if (logger.isDebugEnabled()) {
            logger.debug("List block headers by height: {}, size: {}, Context: {}", height,
                size, contextProvider.get());
          }

          FinishableFuture<List<BlockHeader>> nextFuture =
              new FinishableFuture<List<BlockHeader>>();

          try {
            final Rpc.ListParams listParams =
                Rpc.ListParams.newBuilder().setHeight(height).setSize(size).build();
            ListenableFuture<Rpc.BlockMetadataList> listenableFuture =
                aergoService.listBlockMetadata(listParams);

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

}

