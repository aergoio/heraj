/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static com.google.common.util.concurrent.Futures.addCallback;
import static com.google.common.util.concurrent.MoreExecutors.directExecutor;
import static hera.api.tupleorerror.FunctionChain.fail;
import static hera.api.tupleorerror.FunctionChain.of;
import static hera.util.TransportUtils.assertArgument;
import static hera.util.TransportUtils.copyFrom;
import static hera.util.TransportUtils.longToByteArray;
import static java.util.stream.Collectors.toList;
import static org.slf4j.LoggerFactory.getLogger;
import static types.AergoRPCServiceGrpc.newFutureStub;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.ByteString;
import hera.ContextProvider;
import hera.ContextProviderInjectable;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.model.Block;
import hera.api.model.BlockHash;
import hera.api.model.BlockHeader;
import hera.api.model.BytesValue;
import hera.api.tupleorerror.Function1;
import hera.api.tupleorerror.Function2;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.api.tupleorerror.ResultOrErrorFutureFactory;
import hera.transport.BlockConverterFactory;
import hera.transport.BlockMetadataConverterFactory;
import hera.transport.ModelConverter;
import io.grpc.ManagedChannel;
import java.util.List;
import lombok.Getter;
import org.slf4j.Logger;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.Blockchain;
import types.Rpc;

@ApiAudience.Private
@ApiStability.Unstable
@SuppressWarnings("unchecked")
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
  private final Function1<BlockHash, ResultOrErrorFuture<Block>> blockByHashFunction = (hash) -> {
    final ResultOrErrorFuture<Block> nextFuture =
        ResultOrErrorFutureFactory.supplyEmptyFuture();
    logger.debug("Get block by hash: {}, Context: {}", hash, contextProvider.get());

    try {
      final ByteString byteString = copyFrom(hash.getBytesValue());
      final Rpc.SingleBytes bytes = Rpc.SingleBytes.newBuilder().setValue(byteString).build();
      ListenableFuture<Blockchain.Block> listenableFuture = aergoService.getBlock(bytes);
      FutureChain<Blockchain.Block, Block> callback =
          new FutureChain<>(nextFuture, contextProvider.get());
      callback.setSuccessHandler(
          block -> of(() -> blockConverter.convertToDomainModel(block)));
      addCallback(listenableFuture, callback, directExecutor());

      return nextFuture;
    } catch (Exception e) {
      nextFuture.complete(fail(e));
      return nextFuture;
    }
  };

  @Getter
  private final Function1<Long, ResultOrErrorFuture<Block>> blockByHeightFunction = (height) -> {
    final ResultOrErrorFuture<Block> nextFuture =
        ResultOrErrorFutureFactory.supplyEmptyFuture();
    logger.debug("Get block by height: {}, Context: {}", height, contextProvider.get());

    try {
      assertArgument(height >= 0, "Height", ">= 0");

      final ByteString byteString = copyFrom(BytesValue.of(longToByteArray(height)));
      final Rpc.SingleBytes bytes = Rpc.SingleBytes.newBuilder().setValue(byteString).build();
      ListenableFuture<Blockchain.Block> listenableFuture = aergoService.getBlock(bytes);
      FutureChain<Blockchain.Block, Block> callback =
          new FutureChain<>(nextFuture, contextProvider.get());
      callback.setSuccessHandler(
          block -> of(() -> blockConverter.convertToDomainModel(block)));
      addCallback(listenableFuture, callback, directExecutor());

      return nextFuture;
    } catch (Exception e) {
      nextFuture.complete(fail(e));
      return nextFuture;
    }
  };

  @Getter
  private final Function2<BlockHash, Integer,
      ResultOrErrorFuture<List<BlockHeader>>> listBlockHeadersByHashFunction = (hash, size) -> {
        final ResultOrErrorFuture<List<BlockHeader>> nextFuture =
            ResultOrErrorFutureFactory.supplyEmptyFuture();
        logger.debug("List block headers by hash: {}, size: {}, Context: {}", hash, size,
            contextProvider.get());

        try {
          assertArgument(size > 0, "Block list size", "postive");

          final Rpc.ListParams listParams = Rpc.ListParams.newBuilder()
              .setHash(copyFrom(hash.getBytesValue()))
              .setSize(size)
              .build();
          ListenableFuture<Rpc.BlockMetadataList> listenableFuture =
              aergoService.listBlockMetadata(listParams);
          FutureChain<Rpc.BlockMetadataList, List<BlockHeader>> callback =
              new FutureChain<>(nextFuture, contextProvider.get());
          callback.setSuccessHandler(metadatas -> of(
              () -> metadatas.getBlocksList().stream()
                  .map(blockMetadataConverter::convertToDomainModel).collect(toList())));
          addCallback(listenableFuture, callback, directExecutor());

          return nextFuture;
        } catch (Exception e) {
          nextFuture.complete(fail(e));
          return nextFuture;
        }
      };

  @Getter
  private final Function2<Long, Integer,
      ResultOrErrorFuture<List<BlockHeader>>> listBlockHeadersByHeightFunction = (height, size) -> {
        final ResultOrErrorFuture<List<BlockHeader>> nextFuture =
            ResultOrErrorFutureFactory.supplyEmptyFuture();
        logger.debug("List block headers by height: {}, size: {}, Context: {}", height, size,
            contextProvider.get());

        try {
          assertArgument(height >= 0, "Height", ">= 0");
          assertArgument(size > 0, "Block list size", "postive");

          final Rpc.ListParams listParams =
              Rpc.ListParams.newBuilder().setHeight(height).setSize(size).build();
          ListenableFuture<Rpc.BlockMetadataList> listenableFuture =
              aergoService.listBlockMetadata(listParams);
          FutureChain<Rpc.BlockMetadataList, List<BlockHeader>> callback =
              new FutureChain<>(nextFuture, contextProvider.get());
          callback.setSuccessHandler(metadatas -> of(
              () -> metadatas.getBlocksList().stream()
                  .map(blockMetadataConverter::convertToDomainModel).collect(toList())));
          addCallback(listenableFuture, callback, directExecutor());

          return nextFuture;
        } catch (Exception e) {
          nextFuture.complete(fail(e));
          return nextFuture;
        }
      };

}

