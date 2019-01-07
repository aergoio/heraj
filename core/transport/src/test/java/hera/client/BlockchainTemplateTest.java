/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.TransportConstants.BLOCKCHAIN_BLOCKCHAINSTATUS;
import static hera.TransportConstants.BLOCKCHAIN_LISTPEERS;
import static hera.TransportConstants.BLOCKCHAIN_NODESTATUS;
import static hera.TransportConstants.BLOCKCHAIN_PEERMETRICS;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hera.AbstractTestCase;
import hera.ContextProvider;
import hera.api.model.BlockHash;
import hera.api.model.BlockchainStatus;
import hera.api.model.BytesValue;
import hera.api.model.NodeStatus;
import hera.api.model.Peer;
import hera.api.model.PeerMetric;
import hera.api.tupleorerror.Function0;
import hera.api.tupleorerror.WithIdentity;
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
  public void testGetBlockchainStatus() {
    final BlockchainBaseTemplate base = mock(BlockchainBaseTemplate.class);
    FinishableFuture<BlockchainStatus> future = new FinishableFuture<BlockchainStatus>();
    future.success(new BlockchainStatus(10L, BlockHash.of(BytesValue.EMPTY)));
    when(base.getBlockchainStatusFunction())
        .thenReturn(new Function0<FinishableFuture<BlockchainStatus>>() {
          @Override
          public FinishableFuture<BlockchainStatus> apply() {
            return future;
          }
        });

    final BlockchainTemplate blockchainTemplate =
        supplyBlockchainTemplate(base);

    final BlockchainStatus blockchainStatus =
        blockchainTemplate.getBlockchainStatus();
    assertNotNull(blockchainStatus);
    assertEquals(BLOCKCHAIN_BLOCKCHAINSTATUS,
        ((WithIdentity) blockchainTemplate.getBlockchainStatusFunction()).getIdentity());
  }

  @Test
  public void testListPeers() {
    final BlockchainBaseTemplate base = mock(BlockchainBaseTemplate.class);
    FinishableFuture<List<Peer>> future = new FinishableFuture<List<Peer>>();
    future.success(new ArrayList<Peer>());
    when(base.getListPeersFunction()).thenReturn(new Function0<FinishableFuture<List<Peer>>>() {
      @Override
      public FinishableFuture<List<Peer>> apply() {
        return future;
      }
    });

    final BlockchainTemplate blockchainTemplate =
        supplyBlockchainTemplate(base);

    final List<Peer> peers = blockchainTemplate.listPeers();
    assertNotNull(peers);
    assertEquals(BLOCKCHAIN_LISTPEERS,
        ((WithIdentity) blockchainTemplate.getListPeersFunction()).getIdentity());
  }

  @Test
  public void testListPeerMetrics() {
    final BlockchainBaseTemplate base = mock(BlockchainBaseTemplate.class);
    FinishableFuture<List<PeerMetric>> future = new FinishableFuture<List<PeerMetric>>();
    future.success(new ArrayList<PeerMetric>());
    when(base.getListPeersMetricsFunction())
        .thenReturn(new Function0<FinishableFuture<List<PeerMetric>>>() {
          @Override
          public FinishableFuture<List<PeerMetric>> apply() {
            return future;
          }
        });

    final BlockchainTemplate blockchainTemplate =
        supplyBlockchainTemplate(base);

    final List<PeerMetric> peerMetrics = blockchainTemplate.listPeerMetrics();
    assertNotNull(peerMetrics);
    assertEquals(BLOCKCHAIN_PEERMETRICS,
        ((WithIdentity) blockchainTemplate.getListPeerMetricsFunction()).getIdentity());
  }

  @Test
  public void testGetNodeStatus() {
    final BlockchainBaseTemplate base = mock(BlockchainBaseTemplate.class);
    FinishableFuture<NodeStatus> future = new FinishableFuture<NodeStatus>();
    future.success(new NodeStatus(new ArrayList<>()));
    when(base.getNodeStatusFunction()).thenReturn(new Function0<FinishableFuture<NodeStatus>>() {
      @Override
      public FinishableFuture<NodeStatus> apply() {
        return future;
      }
    });

    final BlockchainTemplate blockchainTemplate =
        supplyBlockchainTemplate(base);

    final NodeStatus nodeStatus = blockchainTemplate.getNodeStatus();
    assertNotNull(nodeStatus);
    assertEquals(BLOCKCHAIN_NODESTATUS,
        ((WithIdentity) blockchainTemplate.getNodeStatusFunction()).getIdentity());
  }

}
