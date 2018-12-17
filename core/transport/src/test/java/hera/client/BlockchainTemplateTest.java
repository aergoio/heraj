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
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.api.tupleorerror.ResultOrErrorFutureFactory;
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
    ResultOrErrorFuture<BlockchainStatus> future = ResultOrErrorFutureFactory
        .supply(() -> new BlockchainStatus(10L, BlockHash.of(BytesValue.EMPTY)));
    when(base.getBlockchainStatusFunction()).thenReturn(() -> future);

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
    ResultOrErrorFuture<List<Peer>> future =
        ResultOrErrorFutureFactory.supply(() -> new ArrayList<Peer>());
    when(base.getListPeersFunction()).thenReturn(() -> future);

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
    ResultOrErrorFuture<List<PeerMetric>> future =
        ResultOrErrorFutureFactory.supply(() -> new ArrayList<PeerMetric>());
    when(base.getListPeersMetricsFunction()).thenReturn(() -> future);

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
    ResultOrErrorFuture<NodeStatus> future =
        ResultOrErrorFutureFactory.supply(() -> new NodeStatus(new ArrayList<>()));
    when(base.getNodeStatusFunction()).thenReturn(() -> future);

    final BlockchainTemplate blockchainTemplate =
        supplyBlockchainTemplate(base);

    final NodeStatus nodeStatus = blockchainTemplate.getNodeStatus();
    assertNotNull(nodeStatus);
    assertEquals(BLOCKCHAIN_NODESTATUS,
        ((WithIdentity) blockchainTemplate.getNodeStatusFunction()).getIdentity());
  }

}
