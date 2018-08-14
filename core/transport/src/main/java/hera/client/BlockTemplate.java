/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.TransportConstants.TIMEOUT;
import static types.AergoRPCServiceGrpc.newFutureStub;

import hera.api.BlockAsyncOperation;
import hera.api.BlockOperation;
import hera.api.model.Block;
import hera.api.model.BlockHeader;
import hera.api.model.Hash;
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
  public Block getBlock(Hash hash) {
    try {
      return blockAsyncOperation.getBlock(hash).get(TIMEOUT, TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  @Override
  public Block getBlock(final long height) {
    try {
      return blockAsyncOperation.getBlock(height).get(TIMEOUT, TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  @Override
  public List<BlockHeader> listBlockHeaders(Hash hash, int size) {
    try {
      return blockAsyncOperation.listBlockHeaders(hash, size).get(TIMEOUT, TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  @Override
  public List<BlockHeader> listBlockHeaders(long height, int size) {
    try {
      return blockAsyncOperation.listBlockHeaders(height, size).get(TIMEOUT, TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }
}

