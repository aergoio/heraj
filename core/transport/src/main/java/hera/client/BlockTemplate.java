/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.util.TransportUtils.copyFrom;
import static java.util.stream.Collectors.toList;
import static types.AergoRPCServiceGrpc.newBlockingStub;

import com.google.protobuf.ByteString;
import hera.api.BlockOperation;
import hera.api.model.Block;
import hera.api.model.BlockHeader;
import hera.api.model.Hash;
import hera.transport.BlockConverterFactory;
import hera.transport.ModelConverter;
import io.grpc.ManagedChannel;
import java.util.List;
import lombok.RequiredArgsConstructor;
import types.AergoRPCServiceGrpc.AergoRPCServiceBlockingStub;
import types.Blockchain;
import types.Rpc.ListParams;
import types.Rpc.SingleBytes;

@RequiredArgsConstructor
public class BlockTemplate implements BlockOperation {

  protected final AergoRPCServiceBlockingStub aergoService;

  protected final ModelConverter<Block, Blockchain.Block> blockConverter;

  public BlockTemplate(final ManagedChannel channel) {
    this(newBlockingStub(channel));
  }

  public BlockTemplate(final AergoRPCServiceBlockingStub aergoService) {
    this(aergoService, new BlockConverterFactory().create());
  }

  @Override
  public Block getBlock(final Hash hash) {
    final ByteString byteString = copyFrom(hash);
    final SingleBytes bytes = SingleBytes.newBuilder().setValue(byteString).build();
    return blockConverter.convertToDomainModel(aergoService.getBlock(bytes));
  }

  @Override
  public List<BlockHeader> listBlockHeaders(final Hash hash, final int size) {
    final ListParams listParams = ListParams.newBuilder()
        .setHash(copyFrom(hash))
        .setSize(size)
        .build();
    return aergoService.listBlockHeaders(listParams).getBlocksList().stream()
        .map(b -> blockConverter.convertToDomainModel(b))
        .map(BlockHeader.class::cast)
        .collect(toList());
  }

  @Override
  public List<BlockHeader> listBlockHeaders(final long height, final int size) {
    final ListParams listParams = ListParams.newBuilder()
        .setHeight(height)
        .setSize(size)
        .build();
    return aergoService.listBlockHeaders(listParams).getBlocksList().stream()
        .map(b -> blockConverter.convertToDomainModel(b))
        .map(BlockHeader.class::cast)
        .collect(toList());
  }

}
