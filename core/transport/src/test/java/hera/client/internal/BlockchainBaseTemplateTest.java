/*
 * @copyright defined in LICENSE.txt
 */

package hera.client.internal;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.util.concurrent.ListenableFuture;
import hera.AbstractTestCase;
import hera.ThreadLocalContextProvider;
import hera.api.model.BlockchainStatus;
import hera.api.model.ChainInfo;
import hera.api.model.ChainStats;
import hera.api.model.NodeStatus;
import hera.api.model.Peer;
import hera.api.model.PeerMetric;
import hera.api.model.ServerInfo;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.Metric;
import types.Rpc;

@PrepareForTest({AergoRPCServiceFutureStub.class})
public class BlockchainBaseTemplateTest extends AbstractTestCase {

  protected BlockchainBaseTemplate supplyBlockchainBaseTemplate(
      final AergoRPCServiceFutureStub aergoService) {
    final BlockchainBaseTemplate blockchainBaseTemplate = new BlockchainBaseTemplate();
    blockchainBaseTemplate.aergoService = aergoService;
    blockchainBaseTemplate.contextProvider = new ThreadLocalContextProvider(context, this);
    return blockchainBaseTemplate;
  }

  @Test
  public void testGetBlockchainStatus() throws Exception {
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

    final Future<BlockchainStatus> blockchainStatus =
        blockchainBaseTemplate.getBlockchainStatusFunction().apply();
    assertNotNull(blockchainStatus.get());
  }

  @Test
  public void testGetChainInfo() throws Exception {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture<Rpc.ChainInfo> mockListenableFuture =
        service.submit(new Callable<Rpc.ChainInfo>() {
          @Override
          public Rpc.ChainInfo call() throws Exception {
            return Rpc.ChainInfo.newBuilder().build();
          }
        });
    when(aergoService.getChainInfo(any(Rpc.Empty.class))).thenReturn(mockListenableFuture);

    final BlockchainBaseTemplate blockchainBaseTemplate =
        supplyBlockchainBaseTemplate(aergoService);

    final Future<ChainInfo> chainInfo =
        blockchainBaseTemplate.getChainInfoFunction().apply();
    assertNotNull(chainInfo.get());
  }

  @Test
  public void testGetChainStats() throws Exception {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture<Rpc.ChainStats> mockListenableFuture =
        service.submit(new Callable<Rpc.ChainStats>() {
          @Override
          public Rpc.ChainStats call() throws Exception {
            return Rpc.ChainStats.newBuilder().build();
          }
        });
    when(aergoService.chainStat(any(Rpc.Empty.class))).thenReturn(mockListenableFuture);

    final BlockchainBaseTemplate blockchainBaseTemplate =
        supplyBlockchainBaseTemplate(aergoService);

    final Future<ChainStats> chainStats =
        blockchainBaseTemplate.getChainStatsFunction().apply();
    assertNotNull(chainStats.get());
  }

  @Test
  public void testListPeers() throws Exception {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture<Rpc.PeerList> mockListenableFuture =
        service.submit(new Callable<Rpc.PeerList>() {
          @Override
          public Rpc.PeerList call() throws Exception {
            return Rpc.PeerList.newBuilder().build();
          }
        });
    when(aergoService.getPeers(any(Rpc.PeersParams.class))).thenReturn(mockListenableFuture);

    final BlockchainBaseTemplate blockchainBaseTemplate =
        supplyBlockchainBaseTemplate(aergoService);

    final Future<List<Peer>> peers =
        blockchainBaseTemplate.getListPeersFunction().apply(false, false);
    assertNotNull(peers.get());
  }

  @Test
  public void testListPeerMetrics() throws Exception {
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

    final Future<List<PeerMetric>> peers =
        blockchainBaseTemplate.getListPeersMetricsFunction().apply();
    assertNotNull(peers.get());
  }

  @Test
  public void testGetServerInfo() throws Exception {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture<Rpc.ServerInfo> mockListenableFuture =
        service.submit(new Callable<Rpc.ServerInfo>() {
          @Override
          public Rpc.ServerInfo call() throws Exception {
            return Rpc.ServerInfo.newBuilder().build();
          }
        });
    when(aergoService.getServerInfo(any(Rpc.KeyParams.class))).thenReturn(mockListenableFuture);

    final BlockchainBaseTemplate blockchainBaseTemplate =
        supplyBlockchainBaseTemplate(aergoService);

    final Future<ServerInfo> nodeStatus =
        blockchainBaseTemplate.getServerInfoFunction().apply(new ArrayList<String>());
    assertNotNull(nodeStatus.get());
  }

  @Test
  public void testGetNodeStatus() throws Exception {
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

    final Future<NodeStatus> nodeStatus =
        blockchainBaseTemplate.getNodeStatusFunction().apply();
    assertNotNull(nodeStatus.get());
  }

}
