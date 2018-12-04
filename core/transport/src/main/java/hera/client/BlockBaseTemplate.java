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
import static types.AergoRPCServiceGrpc.newFutureStub;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.protobuf.ByteString;
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
import hera.transport.ModelConverter;
import io.grpc.ManagedChannel;
import java.util.List;
import lombok.Getter;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.Blockchain;
import types.Rpc;

@ApiAudience.Private
@ApiStability.Unstable
@SuppressWarnings("unchecked")
public class BlockBaseTemplate implements ChannelInjectable {

  protected final ModelConverter<Block, Blockchain.Block> blockConverter =
      new BlockConverterFactory().create();

  @Getter
  protected AergoRPCServiceFutureStub aergoService;

  @Override
  public void setChannel(final ManagedChannel channel) {
    this.aergoService = newFutureStub(channel);
  }

  @Getter
  private final Function1<BlockHash, ResultOrErrorFuture<Block>> blockByHashFunction = (hash) -> {
    final ResultOrErrorFuture<Block> nextFuture =
        ResultOrErrorFutureFactory.supplyEmptyFuture();
    try {
      final ByteString byteString = copyFrom(hash.getBytesValue());
      final Rpc.SingleBytes bytes = Rpc.SingleBytes.newBuilder().setValue(byteString).build();
      ListenableFuture<Blockchain.Block> listenableFuture = aergoService.getBlock(bytes);
      FutureChain<Blockchain.Block, Block> callback = new FutureChain<>(nextFuture);
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
    try {
      assertArgument(height > 0, "Height", "postive");

      final ByteString byteString = copyFrom(BytesValue.of(longToByteArray(height)));
      final Rpc.SingleBytes bytes = Rpc.SingleBytes.newBuilder().setValue(byteString).build();
      ListenableFuture<Blockchain.Block> listenableFuture = aergoService.getBlock(bytes);
      FutureChain<Blockchain.Block, Block> callback = new FutureChain<>(nextFuture);
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
        try {
          assertArgument(size > 0, "Block list size", "postive");

          final Rpc.ListParams listParams = Rpc.ListParams.newBuilder()
              .setHash(copyFrom(hash.getBytesValue()))
              .setSize(size)
              .build();
          ListenableFuture<Rpc.BlockHeaderList> listenableFuture =
              aergoService.listBlockHeaders(listParams);
          FutureChain<Rpc.BlockHeaderList, List<BlockHeader>> callback =
              new FutureChain<>(nextFuture);
          callback.setSuccessHandler(headers -> of(
              () -> headers.getBlocksList().stream().map(blockConverter::convertToDomainModel)
                  .map(BlockHeader.class::cast).collect(toList())));
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
        try {
          assertArgument(height > 0, "Height", "postive");
          assertArgument(size > 0, "Block list size", "postive");

          final Rpc.ListParams listParams =
              Rpc.ListParams.newBuilder().setHeight(height).setSize(size).build();
          ListenableFuture<Rpc.BlockHeaderList> listenableFuture =
              aergoService.listBlockHeaders(listParams);
          FutureChain<Rpc.BlockHeaderList, List<BlockHeader>> callback =
              new FutureChain<>(nextFuture);
          callback.setSuccessHandler(headers -> of(
              () -> headers.getBlocksList().stream().map(blockConverter::convertToDomainModel)
                  .map(BlockHeader.class::cast).collect(toList())));
          addCallback(listenableFuture, callback, directExecutor());

          return nextFuture;
        } catch (Exception e) {
          nextFuture.complete(fail(e));
          return nextFuture;
        }
      };

}

