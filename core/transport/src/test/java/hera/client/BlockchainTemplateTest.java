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
import hera.client.internal.HerajFutures;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
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
    final Future<BlockchainStatus> future =
        HerajFutures.success(BlockchainStatus.newBuilder().build());
    when(base.getBlockchainStatusFunction())
        .thenReturn(new Function0<Future<BlockchainStatus>>() {
          @Override
          public Future<BlockchainStatus> apply() {
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
    final Future<BlockchainStatus> future =
        HerajFutures.success(BlockchainStatus.newBuilder().build());
    when(base.getBlockchainStatusFunction())
        .thenReturn(new Function0<Future<BlockchainStatus>>() {
          @Override
          public Future<BlockchainStatus> apply() {
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
    final Future<ChainInfo> future = HerajFutures.success(ChainInfo.newBuilder().build());
    when(base.getChainInfoFunction())
        .thenReturn(new Function0<Future<ChainInfo>>() {
          @Override
          public Future<ChainInfo> apply() {
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
    final Future<ChainStats> future = HerajFutures.success(ChainStats.newBuilder().build());
    when(base.getChainStatsFunction())
        .thenReturn(new Function0<Future<ChainStats>>() {
          @Override
          public Future<ChainStats> apply() {
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
    final List<Peer> list = new ArrayList<Peer>();
    final Future<List<Peer>> future = HerajFutures.success(list);
    when(base.getListPeersFunction())
        .thenReturn(new Function2<Boolean, Boolean, Future<List<Peer>>>() {
          @Override
          public Future<List<Peer>> apply(Boolean t1, Boolean t2) {
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
    final List<PeerMetric> list = new ArrayList<PeerMetric>();
    final Future<List<PeerMetric>> future = HerajFutures.success(list);
    when(base.getListPeersMetricsFunction())
        .thenReturn(new Function0<Future<List<PeerMetric>>>() {
          @Override
          public Future<List<PeerMetric>> apply() {
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
    final Future<ServerInfo> future = HerajFutures.success(ServerInfo.newBuilder().build());
    when(base.getServerInfoFunction())
        .thenReturn(new Function1<List<String>, Future<ServerInfo>>() {
          @Override
          public Future<ServerInfo> apply(List<String> t) {
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
    final Future<NodeStatus> future = HerajFutures.success(NodeStatus.newBuilder().build());
    when(base.getNodeStatusFunction()).thenReturn(new Function0<Future<NodeStatus>>() {
      @Override
      public Future<NodeStatus> apply() {
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
