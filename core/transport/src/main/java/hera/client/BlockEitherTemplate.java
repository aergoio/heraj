/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.TransportConstants.TIMEOUT;
import static hera.api.tupleorerror.FunctionChain.fail;
import static types.AergoRPCServiceGrpc.newFutureStub;

import hera.Context;
import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.BlockAsyncOperation;
import hera.api.BlockEitherOperation;
import hera.api.model.Block;
import hera.api.model.BlockHash;
import hera.api.model.BlockHeader;
import hera.api.tupleorerror.ResultOrError;
import hera.exception.RpcException;
import io.grpc.ManagedChannel;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;

@ApiAudience.Private
@ApiStability.Unstable
@SuppressWarnings("unchecked")
@RequiredArgsConstructor
public class BlockEitherTemplate implements BlockEitherOperation {

  protected final BlockAsyncOperation blockAsyncOperation;

  public BlockEitherTemplate(final ManagedChannel channel, final Context context) {
    this(newFutureStub(channel), context);
  }

  public BlockEitherTemplate(final AergoRPCServiceFutureStub aergoService, final Context context) {
    this(new BlockAsyncTemplate(aergoService, context));
  }

  @Override
  public ResultOrError<Block> getBlock(BlockHash blockHash) {
    try {
      return blockAsyncOperation.getBlock(blockHash).get(TIMEOUT, TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      return fail(new RpcException(e));
    }
  }

  @Override
  public ResultOrError<Block> getBlock(final long height) {
    try {
      return blockAsyncOperation.getBlock(height).get(TIMEOUT, TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      return fail(new RpcException(e));
    }
  }

  @Override
  public ResultOrError<List<BlockHeader>> listBlockHeaders(BlockHash blockHash, int size) {
    try {
      return blockAsyncOperation.listBlockHeaders(blockHash, size).get(TIMEOUT,
          TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      return fail(new RpcException(e));
    }
  }

  @Override
  public ResultOrError<List<BlockHeader>> listBlockHeaders(long height, int size) {
    try {
      return blockAsyncOperation.listBlockHeaders(height, size).get(TIMEOUT, TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      return fail(new RpcException(e));
    }
  }
}

