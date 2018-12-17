/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.TransportConstants.BLOCKCHAIN_BLOCKCHAINSTATUS_EITHER;
import static hera.TransportConstants.BLOCKCHAIN_LISTPEERS_EITHER;
import static hera.TransportConstants.BLOCKCHAIN_NODESTATUS_EITHER;
import static hera.TransportConstants.BLOCKCHAIN_PEERMETRICS_EITHER;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
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
import hera.api.tupleorerror.ResultOrError;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.api.tupleorerror.ResultOrErrorFutureFactory;
import hera.api.tupleorerror.WithIdentity;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

@PrepareForTest({BlockchainBaseTemplate.class})
public class BlockchainEitherTemplateTest extends AbstractTestCase {

  @Override
  public void setUp() {
    super.setUp();
  }

  protected BlockchainEitherTemplate supplyBlockchainEitherTemplate(
      final BlockchainBaseTemplate blockchainBaseTemplate) {
    final BlockchainEitherTemplate blockchainEitherTemplate = new BlockchainEitherTemplate();
    blockchainEitherTemplate.blockchainBaseTemplate = blockchainBaseTemplate;
    blockchainEitherTemplate.setContextProvider(ContextProvider.defaultProvider);
    return blockchainEitherTemplate;
  }

  @Test
  public void testGetBlockchainStatus() {
    final BlockchainBaseTemplate base = mock(BlockchainBaseTemplate.class);
    ResultOrErrorFuture<BlockchainStatus> future = ResultOrErrorFutureFactory
        .supply(() -> new BlockchainStatus(10L, BlockHash.of(BytesValue.EMPTY)));
    when(base.getBlockchainStatusFunction()).thenReturn(() -> future);

    final BlockchainEitherTemplate blockchainEitherTemplate =
        supplyBlockchainEitherTemplate(base);

    final ResultOrError<BlockchainStatus> blockchainStatus =
        blockchainEitherTemplate.getBlockchainStatus();
    assertTrue(blockchainStatus.hasResult());
    assertEquals(BLOCKCHAIN_BLOCKCHAINSTATUS_EITHER,
        ((WithIdentity) blockchainEitherTemplate.getBlockchainStatusFunction()).getIdentity());
  }

  @Test
  public void testListPeers() {
    final BlockchainBaseTemplate base = mock(BlockchainBaseTemplate.class);
    ResultOrErrorFuture<List<Peer>> future =
        ResultOrErrorFutureFactory.supply(() -> new ArrayList<Peer>());
    when(base.getListPeersFunction()).thenReturn(() -> future);

    final BlockchainEitherTemplate blockchainEitherTemplate =
        supplyBlockchainEitherTemplate(base);

    final ResultOrError<List<Peer>> peers = blockchainEitherTemplate.listPeers();
    assertTrue(peers.hasResult());
    assertEquals(BLOCKCHAIN_LISTPEERS_EITHER,
        ((WithIdentity) blockchainEitherTemplate.getListPeersFunction()).getIdentity());
  }

  @Test
  public void testListPeerMetrics() {
    final BlockchainBaseTemplate base = mock(BlockchainBaseTemplate.class);
    ResultOrErrorFuture<List<PeerMetric>> future =
        ResultOrErrorFutureFactory.supply(() -> new ArrayList<PeerMetric>());
    when(base.getListPeersMetricsFunction()).thenReturn(() -> future);

    final BlockchainEitherTemplate blockchainEitherTemplate =
        supplyBlockchainEitherTemplate(base);

    final ResultOrError<List<PeerMetric>> peerMetrics =
        blockchainEitherTemplate.listPeerMetrics();
    assertTrue(peerMetrics.hasResult());
    assertEquals(BLOCKCHAIN_PEERMETRICS_EITHER,
        ((WithIdentity) blockchainEitherTemplate.getListPeerMetricsFunction()).getIdentity());
  }

  @Test
  public void testGetNodeStatus() {
    final BlockchainBaseTemplate base = mock(BlockchainBaseTemplate.class);
    ResultOrErrorFuture<NodeStatus> future =
        ResultOrErrorFutureFactory.supply(() -> new NodeStatus(new ArrayList<>()));
    when(base.getNodeStatusFunction()).thenReturn(() -> future);

    final BlockchainEitherTemplate blockchainEitherTemplate =
        supplyBlockchainEitherTemplate(base);

    final ResultOrError<NodeStatus> nodeStatus = blockchainEitherTemplate.getNodeStatus();
    assertTrue(nodeStatus.hasResult());
    assertEquals(BLOCKCHAIN_NODESTATUS_EITHER,
        ((WithIdentity) blockchainEitherTemplate.getNodeStatusFunction()).getIdentity());
  }

}
