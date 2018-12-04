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
public class BlockchainBaseTemplateTest extends AbstractTestCase {

  @Override
  public void setUp() {
    super.setUp();
  }

  protected BlockchainBaseTemplate supplyBlockchainBaseTemplate(
      final AergoRPCServiceFutureStub aergoService) {
    final BlockchainBaseTemplate blockchainBaseTemplate = new BlockchainBaseTemplate();
    blockchainBaseTemplate.aergoService = aergoService;
    return blockchainBaseTemplate;
  }

  @Test
  public void testGetBlockchainStatus() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture =
        service.submit(() -> Rpc.BlockchainStatus.newBuilder().build());
    when(aergoService.blockchain(any())).thenReturn(mockListenableFuture);

    final BlockchainBaseTemplate blockchainBaseTemplate =
        supplyBlockchainBaseTemplate(aergoService);

    final ResultOrErrorFuture<BlockchainStatus> blockchainStatus =
        blockchainBaseTemplate.getBlockchainStatusFunction().apply();
    assertTrue(blockchainStatus.get().hasResult());
  }

  @Test
  public void testListPeers() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture = service.submit(() -> Rpc.PeerList.newBuilder().build());
    when(aergoService.getPeers(any())).thenReturn(mockListenableFuture);

    final BlockchainBaseTemplate blockchainBaseTemplate =
        supplyBlockchainBaseTemplate(aergoService);

    final ResultOrErrorFuture<List<Peer>> peers =
        blockchainBaseTemplate.getListPeersFunction().apply();
    assertTrue(peers.get().hasResult());
  }

  @Test
  public void testGetNodeStatus() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture =
        service.submit(() -> Rpc.SingleBytes.newBuilder().build());
    when(aergoService.nodeState(any())).thenReturn(mockListenableFuture);

    final BlockchainBaseTemplate blockchainBaseTemplate =
        supplyBlockchainBaseTemplate(aergoService);

    final ResultOrErrorFuture<NodeStatus> nodeStatus =
        blockchainBaseTemplate.getNodeStatusFunction().apply();
    assertTrue(nodeStatus.get().hasResult());
  }

}
