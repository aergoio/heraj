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
import hera.ContextProvider;
import hera.api.model.BlockchainStatus;
import hera.api.model.NodeStatus;
import hera.api.model.Peer;
import hera.api.tupleorerror.ResultOrErrorFuture;
import java.util.List;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.Rpc;

@SuppressWarnings({"rawtypes", "unchecked"})
@PrepareForTest({AergoRPCServiceFutureStub.class})
public class BlockchainAsyncTemplateTest extends AbstractTestCase {
  
  @Override
  public void setUp() {
    super.setUp();
  }

  protected BlockchainAsyncTemplate supplyBlockchainAsyncTemplate(
      final AergoRPCServiceFutureStub aergoService) {
    final BlockchainAsyncTemplate blockchainAsyncTemplate = new BlockchainAsyncTemplate();
    blockchainAsyncTemplate.aergoService = aergoService;
    blockchainAsyncTemplate.setContextProvider(ContextProvider.defaultProvider);
    return blockchainAsyncTemplate;
  }

  @Test
  public void testGetBlockchainStatus() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture =
        service.submit(() -> Rpc.BlockchainStatus.newBuilder().build());
    when(aergoService.blockchain(any())).thenReturn(mockListenableFuture);

    final BlockchainAsyncTemplate blockchainAsyncTemplate =
        supplyBlockchainAsyncTemplate(aergoService);

    final ResultOrErrorFuture<BlockchainStatus> blockchainStatus =
        blockchainAsyncTemplate.getBlockchainStatus();
    assertTrue(blockchainStatus.get().hasResult());
  }

  @Test
  public void testListPeers() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture = service.submit(() -> Rpc.PeerList.newBuilder().build());
    when(aergoService.getPeers(any())).thenReturn(mockListenableFuture);

    final BlockchainAsyncTemplate blockchainAsyncTemplate =
        supplyBlockchainAsyncTemplate(aergoService);

    final ResultOrErrorFuture<List<Peer>> peers = blockchainAsyncTemplate.listPeers();
    assertTrue(peers.get().hasResult());
  }

  @Test
  public void testGetNodeStatus() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture =
        service.submit(() -> Rpc.SingleBytes.newBuilder().build());
    when(aergoService.nodeState(any())).thenReturn(mockListenableFuture);

    final BlockchainAsyncTemplate blockchainAsyncTemplate =
        supplyBlockchainAsyncTemplate(aergoService);

    final ResultOrErrorFuture<NodeStatus> nodeStatus = blockchainAsyncTemplate.getNodeStatus();
    assertTrue(nodeStatus.get().hasResult());
  }

}
