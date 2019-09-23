/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.client.ClientConstants.BLOCKCHAIN_BLOCKCHAINSTATUS;
import static hera.client.ClientConstants.BLOCKCHAIN_CHAININFO;
import static hera.client.ClientConstants.BLOCKCHAIN_CHAINSTATS;
import static hera.client.ClientConstants.BLOCKCHAIN_LIST_PEERS;
import static hera.client.ClientConstants.BLOCKCHAIN_NODESTATUS;
import static hera.client.ClientConstants.BLOCKCHAIN_PEERMETRICS;
import static hera.client.ClientConstants.BLOCKCHAIN_SERVERINFO;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hera.AbstractTestCase;
import hera.ContextProvider;
import hera.api.function.Function0;
import hera.api.function.Function1;
import hera.api.function.Function2;
import hera.api.function.WithIdentity;
import hera.api.model.BlockchainStatus;
import hera.api.model.ChainIdHash;
import hera.api.model.ChainInfo;
import hera.api.model.ChainStats;
import hera.api.model.NodeStatus;
import hera.api.model.Peer;
import hera.api.model.PeerMetric;
import hera.api.model.ServerInfo;
import hera.client.internal.BlockchainBaseTemplate;
import hera.client.internal.FinishableFuture;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

@PrepareForTest({BlockchainBaseTemplate.class})
public class BlockchainTemplateTest extends AbstractTestCase {

  @Override
  public void setUp() {
    super.setUp();
  }

  protected BlockchainTemplate supplyBlockchainTemplate(
      final BlockchainBaseTemplate blockchainBaseTemplate) {
    final BlockchainTemplate blockchainTemplate = new BlockchainTemplate();
    blockchainTemplate.blockchainBaseTemplate = blockchainBaseTemplate;
    blockchainTemplate.setContextProvider(ContextProvider.defaultProvider);
    return blockchainTemplate;
  }

  @Test
  public void testGetChainIdHash() {
    final BlockchainBaseTemplate base = mock(BlockchainBaseTemplate.class);
    final FinishableFuture<BlockchainStatus> future = new FinishableFuture<BlockchainStatus>();
    future.success(BlockchainStatus.newBuilder().build());
    when(base.getBlockchainStatusFunction())
        .thenReturn(new Function0<FinishableFuture<BlockchainStatus>>() {
          @Override
          public FinishableFuture<BlockchainStatus> apply() {
            return future;
          }
        });

    final BlockchainTemplate blockchainTemplate = supplyBlockchainTemplate(base);

    final ChainIdHash chainIdHash = blockchainTemplate.getChainIdHash();
    assertNotNull(chainIdHash);
  }

  @Test
  public void testGetBlockchainStatus() {
    final BlockchainBaseTemplate base = mock(BlockchainBaseTemplate.class);
    final FinishableFuture<BlockchainStatus> future = new FinishableFuture<BlockchainStatus>();
    future.success(BlockchainStatus.newBuilder().build());
    when(base.getBlockchainStatusFunction())
        .thenReturn(new Function0<FinishableFuture<BlockchainStatus>>() {
          @Override
          public FinishableFuture<BlockchainStatus> apply() {
            return future;
          }
        });

    final BlockchainTemplate blockchainTemplate = supplyBlockchainTemplate(base);

    final BlockchainStatus blockchainStatus =
        blockchainTemplate.getBlockchainStatus();
    assertNotNull(blockchainStatus);
    assertEquals(BLOCKCHAIN_BLOCKCHAINSTATUS,
        ((WithIdentity) blockchainTemplate.getBlockchainStatusFunction()).getIdentity());
  }

  @Test
  public void testGetChainInfo() {
    final BlockchainBaseTemplate base = mock(BlockchainBaseTemplate.class);
    final FinishableFuture<ChainInfo> future = new FinishableFuture<ChainInfo>();
    future.success(ChainInfo.newBuilder().build());
    when(base.getChainInfoFunction())
        .thenReturn(new Function0<FinishableFuture<ChainInfo>>() {
          @Override
          public FinishableFuture<ChainInfo> apply() {
            return future;
          }
        });

    final BlockchainTemplate blockchainTemplate = supplyBlockchainTemplate(base);

    final ChainInfo chainInfo = blockchainTemplate.getChainInfo();
    assertNotNull(chainInfo);
    assertEquals(BLOCKCHAIN_CHAININFO,
        ((WithIdentity) blockchainTemplate.getChainInfoFunction()).getIdentity());
  }

  @Test
  public void testGetChainStats() {
    final BlockchainBaseTemplate base = mock(BlockchainBaseTemplate.class);
    final FinishableFuture<ChainStats> future = new FinishableFuture<ChainStats>();
    future.success(ChainStats.newBuilder().build());
    when(base.getChainStatsFunction())
        .thenReturn(new Function0<FinishableFuture<ChainStats>>() {
          @Override
          public FinishableFuture<ChainStats> apply() {
            return future;
          }
        });

    final BlockchainTemplate blockchainTemplate = supplyBlockchainTemplate(base);

    final ChainStats chainStats = blockchainTemplate.getChainStats();
    assertNotNull(chainStats);
    assertEquals(BLOCKCHAIN_CHAINSTATS,
        ((WithIdentity) blockchainTemplate.getChainStatsFunction()).getIdentity());
  }

  @Test
  public void testListPeers() {
    final BlockchainBaseTemplate base = mock(BlockchainBaseTemplate.class);
    final FinishableFuture<List<Peer>> future = new FinishableFuture<List<Peer>>();
    future.success(new ArrayList<Peer>());
    when(base.getListPeersFunction()).thenReturn(new Function2<Boolean, Boolean,
        FinishableFuture<List<Peer>>>() {
      @Override
      public FinishableFuture<List<Peer>> apply(Boolean t1, Boolean t2) {
        return future;
      }
    });

    final BlockchainTemplate blockchainTemplate = supplyBlockchainTemplate(base);

    final List<Peer> peers = blockchainTemplate.listPeers(true, true);
    assertNotNull(peers);
    assertEquals(BLOCKCHAIN_LIST_PEERS,
        ((WithIdentity) blockchainTemplate.getListPeersFunction()).getIdentity());
  }

  @Test
  public void testListPeerMetrics() {
    final BlockchainBaseTemplate base = mock(BlockchainBaseTemplate.class);
    final FinishableFuture<List<PeerMetric>> future = new FinishableFuture<List<PeerMetric>>();
    future.success(new ArrayList<PeerMetric>());
    when(base.getListPeersMetricsFunction())
        .thenReturn(new Function0<FinishableFuture<List<PeerMetric>>>() {
          @Override
          public FinishableFuture<List<PeerMetric>> apply() {
            return future;
          }
        });

    final BlockchainTemplate blockchainTemplate = supplyBlockchainTemplate(base);

    final List<PeerMetric> peerMetrics = blockchainTemplate.listPeerMetrics();
    assertNotNull(peerMetrics);
    assertEquals(BLOCKCHAIN_PEERMETRICS,
        ((WithIdentity) blockchainTemplate.getListPeerMetricsFunction()).getIdentity());
  }

  @Test
  public void testGetServerInfo() {
    final BlockchainBaseTemplate base = mock(BlockchainBaseTemplate.class);
    final FinishableFuture<ServerInfo> future = new FinishableFuture<ServerInfo>();
    future.success(ServerInfo.newBuilder().build());
    when(base.getServerInfoFunction())
        .thenReturn(new Function1<List<String>, FinishableFuture<ServerInfo>>() {
          @Override
          public FinishableFuture<ServerInfo> apply(List<String> t) {
            return future;
          }
        });

    final BlockchainTemplate blockchainTemplate = supplyBlockchainTemplate(base);

    final ServerInfo nodeStatus = blockchainTemplate.getServerInfo(new ArrayList<String>());
    assertNotNull(nodeStatus);
    assertEquals(BLOCKCHAIN_SERVERINFO,
        ((WithIdentity) blockchainTemplate.getServerInfoFunction()).getIdentity());
  }

  @Test
  public void testGetNodeStatus() {
    final BlockchainBaseTemplate base = mock(BlockchainBaseTemplate.class);
    final FinishableFuture<NodeStatus> future = new FinishableFuture<NodeStatus>();
    future.success(NodeStatus.newBuilder().build());
    when(base.getNodeStatusFunction()).thenReturn(new Function0<FinishableFuture<NodeStatus>>() {
      @Override
      public FinishableFuture<NodeStatus> apply() {
        return future;
      }
    });

    final BlockchainTemplate blockchainTemplate = supplyBlockchainTemplate(base);

    final NodeStatus nodeStatus = blockchainTemplate.getNodeStatus();
    assertNotNull(nodeStatus);
    assertEquals(BLOCKCHAIN_NODESTATUS,
        ((WithIdentity) blockchainTemplate.getNodeStatusFunction()).getIdentity());
  }

}
