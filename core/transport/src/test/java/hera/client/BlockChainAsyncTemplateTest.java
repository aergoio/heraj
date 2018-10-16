/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.util.concurrent.ListenableFuture;
import hera.AbstractTestCase;
import hera.Context;
import hera.api.model.BlockchainStatus;
import hera.api.model.NodeStatus;
import hera.api.model.PeerAddress;
import hera.api.tupleorerror.ResultOrErrorFuture;
import java.util.List;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.Rpc;

@SuppressWarnings({"rawtypes", "unchecked"})
@PrepareForTest({AergoRPCServiceFutureStub.class})
public class BlockChainAsyncTemplateTest extends AbstractTestCase {

  protected static final Context context = AergoClientBuilder.getDefaultContext();

  protected BlockChainAsyncTemplate supplyBlockChainAsyncTemplate(
      final AergoRPCServiceFutureStub aergoService) {
    final BlockChainAsyncTemplate blockchainAsyncTemplate = new BlockChainAsyncTemplate();
    blockchainAsyncTemplate.setContext(AergoClientBuilder.getDefaultContext());
    blockchainAsyncTemplate.aergoService = aergoService;
    return blockchainAsyncTemplate;
  }

  @Test
  public void testGetBlockchainStatus() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture =
        service.submit(() -> Rpc.BlockchainStatus.newBuilder().build());
    when(aergoService.blockchain(any())).thenReturn(mockListenableFuture);

    final BlockChainAsyncTemplate blockChainAsyncTemplate =
        supplyBlockChainAsyncTemplate(aergoService);

    final ResultOrErrorFuture<BlockchainStatus> blockchainStatus =
        blockChainAsyncTemplate.getBlockchainStatus();
    assertTrue(blockchainStatus.get().hasResult());
  }

  @Test
  public void testListPeers() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture = service.submit(() -> Rpc.PeerList.newBuilder().build());
    when(aergoService.getPeers(any())).thenReturn(mockListenableFuture);

    final BlockChainAsyncTemplate blockChainAsyncTemplate =
        supplyBlockChainAsyncTemplate(aergoService);

    final ResultOrErrorFuture<List<PeerAddress>> peers = blockChainAsyncTemplate.listPeers();
    assertTrue(peers.get().hasResult());
  }

  @Test
  public void testGetNodeStatus() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture =
        service.submit(() -> Rpc.SingleBytes.newBuilder().build());
    when(aergoService.nodeState(any())).thenReturn(mockListenableFuture);

    final BlockChainAsyncTemplate blockChainAsyncTemplate =
        supplyBlockChainAsyncTemplate(aergoService);

    final ResultOrErrorFuture<NodeStatus> nodeStatus = blockChainAsyncTemplate.getNodeStatus();
    assertTrue(nodeStatus.get().hasResult());
  }

}
