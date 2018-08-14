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
import hera.api.model.BlockHeader;
import hera.api.model.BytesValue;
import hera.api.model.Hash;
import hera.transport.BlockConverterFactory;
import hera.transport.ModelConverter;
import io.grpc.ManagedChannel;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import lombok.RequiredArgsConstructor;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.Blockchain;
import types.Rpc.BlockHeaderList;
import types.Rpc.ListParams;
import types.Rpc.SingleBytes;

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
  public CompletableFuture<Block> getBlock(final Hash hash) {
    final CompletableFuture<Block> nextFuture = new CompletableFuture<>();

    final ByteString byteString = copyFrom(hash);
    final SingleBytes bytes = SingleBytes.newBuilder().setValue(byteString).build();
    ListenableFuture<Blockchain.Block> listenableFuture = aergoService.getBlock(bytes);
    FutureChainer<Blockchain.Block, Block> callback = new FutureChainer<>(nextFuture,
        b -> blockConverter.convertToDomainModel(b));
    Futures.addCallback(listenableFuture, callback, MoreExecutors.directExecutor());

    return nextFuture;
  }

  @Override
  public CompletableFuture<Block> getBlock(final long height) {
    final CompletableFuture<Block> nextFuture = new CompletableFuture<>();

    final ByteString byteString = copyFrom(BytesValue.of(longToByteArray(height)));
    final SingleBytes bytes = SingleBytes.newBuilder().setValue(byteString).build();
    ListenableFuture<Blockchain.Block> listenableFuture = aergoService.getBlock(bytes);
    FutureChainer<Blockchain.Block, Block> callback =
        new FutureChainer<>(nextFuture, b -> blockConverter.convertToDomainModel(b));
    Futures.addCallback(listenableFuture, callback, MoreExecutors.directExecutor());

    return nextFuture;
  }

  @Override
  public CompletableFuture<List<BlockHeader>> listBlockHeaders(final Hash hash, final int size) {
    final CompletableFuture<List<BlockHeader>> nextFuture = new CompletableFuture<>();

    final ListParams listParams = ListParams.newBuilder()
        .setHash(copyFrom(hash))
        .setSize(size)
        .build();
    ListenableFuture<BlockHeaderList> listenableFuture = aergoService.listBlockHeaders(listParams);
    FutureChainer<BlockHeaderList, List<BlockHeader>> callback = new FutureChainer<>(nextFuture,
        blockHeaders -> blockHeaders.getBlocksList().stream()
            .map(BlockHeader.class::cast)
            .collect(toList()));
    Futures.addCallback(listenableFuture, callback, MoreExecutors.directExecutor());

    return nextFuture;
  }

  @Override
  public CompletableFuture<List<BlockHeader>> listBlockHeaders(final long height, final int size) {
    final CompletableFuture<List<BlockHeader>> nextFuture = new CompletableFuture<>();

    final ListParams listParams = ListParams.newBuilder()
        .setHeight(height)
        .setSize(size)
        .build();
    ListenableFuture<BlockHeaderList> listenableFuture = aergoService.listBlockHeaders(listParams);
    FutureChainer<BlockHeaderList, List<BlockHeader>> callback = new FutureChainer<>(nextFuture,
        blockHeaders -> blockHeaders.getBlocksList().stream()
            .map(BlockHeader.class::cast)
            .collect(toList()));
    Futures.addCallback(listenableFuture, callback, MoreExecutors.directExecutor());

    return nextFuture;
  }

}
