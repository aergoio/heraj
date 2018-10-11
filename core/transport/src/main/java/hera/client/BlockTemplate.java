/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static types.AergoRPCServiceGrpc.newFutureStub;

import hera.Context;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.BlockEitherOperation;
import hera.api.BlockOperation;
import hera.api.model.Block;
import hera.api.model.BlockHash;
import hera.api.model.BlockHeader;
import io.grpc.ManagedChannel;
import java.util.List;
import lombok.RequiredArgsConstructor;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;

@ApiAudience.Private
@ApiStability.Unstable
@RequiredArgsConstructor
public class BlockTemplate implements BlockOperation {

  protected final BlockEitherOperation blockEitherOperation;

  public BlockTemplate(final ManagedChannel channel, final Context context) {
    this(newFutureStub(channel), context);
  }

  public BlockTemplate(final AergoRPCServiceFutureStub aergoService, final Context context) {
    this(new BlockEitherTemplate(aergoService, context));
  }

  @Override
  public Block getBlock(final BlockHash blockHash) {
    return blockEitherOperation.getBlock(blockHash).getResult();
  }

  @Override
  public Block getBlock(final long height) {
    return blockEitherOperation.getBlock(height).getResult();
  }

  @Override
  public List<BlockHeader> listBlockHeaders(final BlockHash blockHash, final int size) {
    return blockEitherOperation.listBlockHeaders(blockHash, size).getResult();
  }

  @Override
  public List<BlockHeader> listBlockHeaders(final long height, final int size) {
    return blockEitherOperation.listBlockHeaders(height, size).getResult();
  }

}
