/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.TransportConstants.TIMEOUT;
import static hera.api.tupleorerror.FunctionChain.fail;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.api.BlockChainAsyncOperation;
import hera.api.BlockChainEitherOperation;
import hera.api.model.BlockchainStatus;
import hera.api.model.NodeStatus;
import hera.api.model.PeerAddress;
import hera.api.tupleorerror.ResultOrError;
import hera.exception.RpcException;
import io.grpc.ManagedChannel;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import types.AergoRPCServiceGrpc;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;

@ApiAudience.Private
@ApiStability.Unstable
@SuppressWarnings("unchecked")
@RequiredArgsConstructor
public class BlockChainEitherTemplate implements BlockChainEitherOperation {

  protected final BlockChainAsyncOperation blockChainAsyncOperation;

  public BlockChainEitherTemplate(final ManagedChannel channel) {
    this(AergoRPCServiceGrpc.newFutureStub(channel));
  }

  public BlockChainEitherTemplate(final AergoRPCServiceFutureStub aergoService) {
    this(new BlockChainAsyncTemplate(aergoService));
  }

  @Override
  public ResultOrError<BlockchainStatus> getBlockchainStatus() {
    try {
      return blockChainAsyncOperation.getBlockchainStatus().get(TIMEOUT, TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      return fail(new RpcException(e));
    }
  }

  @Override
  public ResultOrError<List<PeerAddress>> listPeers() {
    try {
      return blockChainAsyncOperation.listPeers().get(TIMEOUT, TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      return fail(new RpcException(e));
    }
  }

  @Override
  public ResultOrError<NodeStatus> getNodeStatus() {
    try {
      return blockChainAsyncOperation.getNodeStatus().get(TIMEOUT, TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      return fail(new RpcException(e));
    }
  }
}
