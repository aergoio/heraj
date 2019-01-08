/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.util.concurrent.ListenableFuture;
import hera.AbstractTestCase;
import hera.Context;
import hera.ContextProvider;
import hera.api.model.BlockchainStatus;
import hera.api.model.NodeStatus;
import hera.api.model.Peer;
import hera.api.model.PeerMetric;
import java.util.List;
import java.util.concurrent.Callable;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.Metric;
import types.Rpc;

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
    blockchainBaseTemplate.contextProvider = new ContextProvider() {
      @Override
      public Context get() {
        return context;
      }
    };
    return blockchainBaseTemplate;
  }

  @Test
  public void testGetBlockchainStatus() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture<Rpc.BlockchainStatus> mockListenableFuture =
        service.submit(new Callable<Rpc.BlockchainStatus>() {
          @Override
          public Rpc.BlockchainStatus call() throws Exception {
            return Rpc.BlockchainStatus.newBuilder().build();
          }
        });
    when(aergoService.blockchain(any(Rpc.Empty.class))).thenReturn(mockListenableFuture);

    final BlockchainBaseTemplate blockchainBaseTemplate =
        supplyBlockchainBaseTemplate(aergoService);

    final FinishableFuture<BlockchainStatus> blockchainStatus =
        blockchainBaseTemplate.getBlockchainStatusFunction().apply();
    assertNotNull(blockchainStatus.get());
  }

  @Test
  public void testListPeers() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture<Rpc.PeerList> mockListenableFuture =
        service.submit(new Callable<Rpc.PeerList>() {
          @Override
          public Rpc.PeerList call() throws Exception {
            return Rpc.PeerList.newBuilder().build();
          }
        });
    when(aergoService.getPeers(any(Rpc.Empty.class))).thenReturn(mockListenableFuture);

    final BlockchainBaseTemplate blockchainBaseTemplate =
        supplyBlockchainBaseTemplate(aergoService);

    final FinishableFuture<List<Peer>> peers =
        blockchainBaseTemplate.getListPeersFunction().apply();
    assertNotNull(peers.get());
  }

  @Test
  public void testListPeerMetrics() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture<Metric.Metrics> mockListenableFuture =
        service.submit(new Callable<Metric.Metrics>() {
          @Override
          public Metric.Metrics call() throws Exception {
            return Metric.Metrics.newBuilder().build();
          }
        });
    when(aergoService.metric(any(Metric.MetricsRequest.class))).thenReturn(mockListenableFuture);

    final BlockchainBaseTemplate blockchainBaseTemplate =
        supplyBlockchainBaseTemplate(aergoService);

    final FinishableFuture<List<PeerMetric>> peers =
        blockchainBaseTemplate.getListPeersMetricsFunction().apply();
    assertNotNull(peers.get());
  }

  @Test
  public void testGetNodeStatus() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture<Rpc.SingleBytes> mockListenableFuture =
        service.submit(new Callable<Rpc.SingleBytes>() {
          @Override
          public Rpc.SingleBytes call() throws Exception {
            return Rpc.SingleBytes.newBuilder().build();
          }
        });
    when(aergoService.nodeState(any(Rpc.NodeReq.class))).thenReturn(mockListenableFuture);

    final BlockchainBaseTemplate blockchainBaseTemplate =
        supplyBlockchainBaseTemplate(aergoService);

    final FinishableFuture<NodeStatus> nodeStatus =
        blockchainBaseTemplate.getNodeStatusFunction().apply();
    assertNotNull(nodeStatus.get());
  }

}
