/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.util.TransportUtils.copyFrom;
import static hera.util.TransportUtils.longToByteArray;
import static java.util.stream.Collectors.toList;
import static types.AergoRPCServiceGrpc.newFutureStub;

import com.google.common.util.concurrent.Futures;
import com.google.common.util.concurrent.ListenableFuture;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.protobuf.ByteString;
import hera.FutureChainer;
import hera.api.BlockAsyncOperation;
import hera.api.model.Block;
import hera.api.model.BlockHash;
import hera.api.model.BlockHeader;
import hera.api.model.BytesValue;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.api.tupleorerror.ResultOrErrorFutureFactory;
import hera.transport.BlockConverterFactory;
import hera.transport.ModelConverter;
import io.grpc.ManagedChannel;
import java.util.List;
import lombok.RequiredArgsConstructor;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.Blockchain;
import types.Rpc.BlockHeaderList;
import types.Rpc.ListParams;
import types.Rpc.SingleBytes;

@SuppressWarnings("unchecked")
@RequiredArgsConstructor
public class BlockAsyncTemplate implements BlockAsyncOperation {

  protected final AergoRPCServiceFutureStub aergoService;

  protected final ModelConverter<Block, Blockchain.Block> blockConverter;

  public BlockAsyncTemplate(final ManagedChannel channel) {
    this(newFutureStub(channel));
  }

  public BlockAsyncTemplate(final AergoRPCServiceFutureStub aergoService) {
    this(aergoService, new BlockConverterFactory().create());
  }

  @Override
  public ResultOrErrorFuture<Block> getBlock(final BlockHash blockHash) {
    final ResultOrErrorFuture<Block> nextFuture = ResultOrErrorFutureFactory.supplyEmptyFuture();

    final ByteString byteString = copyFrom(blockHash);
    final SingleBytes bytes = SingleBytes.newBuilder().setValue(byteString).build();
    ListenableFuture<Blockchain.Block> listenableFuture = aergoService.getBlock(bytes);
    FutureChainer<Blockchain.Block, Block> callback =
        new FutureChainer<>(nextFuture, b -> blockConverter.convertToDomainModel(b));
    Futures.addCallback(listenableFuture, callback, MoreExecutors.directExecutor());

    return nextFuture;
  }

  @Override
  public ResultOrErrorFuture<Block> getBlock(final long height) {
    final ResultOrErrorFuture<Block> nextFuture = ResultOrErrorFutureFactory.supplyEmptyFuture();

    final ByteString byteString = copyFrom(BytesValue.of(longToByteArray(height)));
    final SingleBytes bytes = SingleBytes.newBuilder().setValue(byteString).build();
    ListenableFuture<Blockchain.Block> listenableFuture = aergoService.getBlock(bytes);
    FutureChainer<Blockchain.Block, Block> callback =
        new FutureChainer<>(nextFuture, b -> blockConverter.convertToDomainModel(b));
    Futures.addCallback(listenableFuture, callback, MoreExecutors.directExecutor());

    return nextFuture;
  }

  @Override
  public ResultOrErrorFuture<List<BlockHeader>> listBlockHeaders(final BlockHash blockHash,
      final int size) {
    final ResultOrErrorFuture<List<BlockHeader>> nextFuture =
        ResultOrErrorFutureFactory.supplyEmptyFuture();

    final ListParams listParams =
        ListParams.newBuilder().setHash(copyFrom(blockHash)).setSize(size).build();
    ListenableFuture<BlockHeaderList> listenableFuture = aergoService.listBlockHeaders(listParams);
    FutureChainer<BlockHeaderList, List<BlockHeader>> callback =
        new FutureChainer<>(nextFuture, blockHeaders -> blockHeaders.getBlocksList().stream()
            .map(blockConverter::convertToDomainModel)
            .map(BlockHeader.class::cast).collect(toList()));
    Futures.addCallback(listenableFuture, callback, MoreExecutors.directExecutor());

    return nextFuture;
  }

  @Override
  public ResultOrErrorFuture<List<BlockHeader>> listBlockHeaders(final long height,
      final int size) {
    final ResultOrErrorFuture<List<BlockHeader>> nextFuture =
        ResultOrErrorFutureFactory.supplyEmptyFuture();

    final ListParams listParams = ListParams.newBuilder().setHeight(height).setSize(size).build();
    ListenableFuture<BlockHeaderList> listenableFuture = aergoService.listBlockHeaders(listParams);
    FutureChainer<BlockHeaderList, List<BlockHeader>> callback =
        new FutureChainer<>(nextFuture, blockHeaders -> blockHeaders.getBlocksList().stream()
            .map(blockConverter::convertToDomainModel)
            .map(BlockHeader.class::cast).collect(toList()));
    Futures.addCallback(listenableFuture, callback, MoreExecutors.directExecutor());

    return nextFuture;
  }

}

