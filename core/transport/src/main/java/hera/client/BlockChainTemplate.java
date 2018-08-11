/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.TransportConstants.TIMEOUT;

import hera.api.BlockChainAsyncOperation;
import hera.api.BlockChainOperation;
import hera.api.model.BlockchainStatus;
import hera.api.model.NodeStatus;
import hera.api.model.PeerAddress;
import hera.exception.HerajException;
import io.grpc.ManagedChannel;
import java.util.List;
import java.util.concurrent.TimeUnit;
import lombok.RequiredArgsConstructor;
import types.AergoRPCServiceGrpc;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;

@RequiredArgsConstructor
public class BlockChainTemplate implements BlockChainOperation {

  protected final BlockChainAsyncOperation blockChainAsyncOperation;

  public BlockChainTemplate(final ManagedChannel channel) {
    this(AergoRPCServiceGrpc.newFutureStub(channel));
  }

  public BlockChainTemplate(final AergoRPCServiceFutureStub aergoService) {
    this(new BlockChainAsyncTemplate(aergoService));
  }

  @Override
  public BlockchainStatus getBlockchainStatus() {
    try {
      return blockChainAsyncOperation.getBlockchainStatus().get(TIMEOUT, TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  @Override
  public List<PeerAddress> listPeers() {
    try {
      return blockChainAsyncOperation.listPeers().get(TIMEOUT, TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }

  @Override
  public NodeStatus getNodeStatus() {
    try {
      return blockChainAsyncOperation.getNodeStatus().get(TIMEOUT, TimeUnit.MILLISECONDS);
    } catch (Exception e) {
      throw new HerajException(e);
    }
  }
}
