/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.TransportConstants.TIMEOUT;
import static types.AergoRPCServiceGrpc.newFutureStub;

import hera.api.BlockAsyncOperation;
import hera.api.BlockOperation;
import hera.api.model.Block;
import hera.api.model.BlockHash;
import hera.api.model.BlockHeader;
import hera.api.tupleorerror.ResultOrError;
import hera.exception.HerajException;
import io.grpc.ManagedChannel;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;

@RequiredArgsConstructor
public class BlockTemplate implements BlockOperation {

  protected final BlockAsyncOperation blockAsyncOperation;

  public BlockTemplate(final ManagedChannel channel) {
    this(newFutureStub(channel));
  }

  public BlockTemplate(final AergoRPCServiceFutureStub aergoService) {
    this(new BlockAsyncTemplate(aergoService));
  }

  @Override
  public ResultOrError<Block> getBlock(BlockHash blockHash) {
    try {
      return blockAsyncOperation.getBlock(blockHash).get(TIMEOUT, TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  @Override
  public ResultOrError<Block> getBlock(final long height) {
    try {
      return blockAsyncOperation.getBlock(height).get(TIMEOUT, TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  @Override
  public ResultOrError<List<BlockHeader>> listBlockHeaders(BlockHash blockHash, int size) {
    try {
      return blockAsyncOperation.listBlockHeaders(blockHash, size).get(TIMEOUT,
          TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  @Override
  public ResultOrError<List<BlockHeader>> listBlockHeaders(long height, int size) {
    try {
      return blockAsyncOperation.listBlockHeaders(height, size).get(TIMEOUT, TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }
}

