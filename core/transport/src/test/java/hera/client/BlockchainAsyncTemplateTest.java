/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.TransportConstants.BLOCKCHAIN_BLOCKCHAINSTATUS_ASYNC;
import static hera.TransportConstants.BLOCKCHAIN_LISTPEERS_ASYNC;
import static hera.TransportConstants.BLOCKCHAIN_NODESTATUS_ASYNC;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hera.AbstractTestCase;
import hera.ContextProvider;
import hera.api.model.BlockchainStatus;
import hera.api.model.NodeStatus;
import hera.api.model.Peer;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.api.tupleorerror.ResultOrErrorFutureFactory;
import hera.api.tupleorerror.WithIdentity;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

@PrepareForTest({BlockchainBaseTemplate.class})
public class BlockchainAsyncTemplateTest extends AbstractTestCase {

  @Override
  public void setUp() {
    super.setUp();
  }

  protected BlockchainAsyncTemplate supplyBlockchainAsyncTemplate(
      final BlockchainBaseTemplate blockchainBaseTemplate) {
    final BlockchainAsyncTemplate blockchainAsyncTemplate = new BlockchainAsyncTemplate();
    blockchainAsyncTemplate.blockchainBaseTemplate = blockchainBaseTemplate;
    blockchainAsyncTemplate.setContextProvider(ContextProvider.defaultProvider);
    return blockchainAsyncTemplate;
  }

  @Test
  public void testGetBlockchainStatus() {
    final BlockchainBaseTemplate base = mock(BlockchainBaseTemplate.class);
    ResultOrErrorFuture<BlockchainStatus> future =
        ResultOrErrorFutureFactory.supply(() -> new BlockchainStatus());
    when(base.getBlockchainStatusFunction()).thenReturn(() -> future);

    final BlockchainAsyncTemplate blockchainAsyncTemplate =
        supplyBlockchainAsyncTemplate(base);

    final ResultOrErrorFuture<BlockchainStatus> blockchainStatus =
        blockchainAsyncTemplate.getBlockchainStatus();
    assertTrue(blockchainStatus.get().hasResult());
    assertEquals(BLOCKCHAIN_BLOCKCHAINSTATUS_ASYNC,
        ((WithIdentity) blockchainAsyncTemplate.getBlockchainStatusFunction()).getIdentity());
  }

  @Test
  public void testListPeers() {
    final BlockchainBaseTemplate base = mock(BlockchainBaseTemplate.class);
    ResultOrErrorFuture<List<Peer>> future =
        ResultOrErrorFutureFactory.supply(() -> new ArrayList<Peer>());
    when(base.getListPeersFunction()).thenReturn(() -> future);

    final BlockchainAsyncTemplate blockchainAsyncTemplate =
        supplyBlockchainAsyncTemplate(base);

    final ResultOrErrorFuture<List<Peer>> peers = blockchainAsyncTemplate.listPeers();
    assertTrue(peers.get().hasResult());
    assertEquals(BLOCKCHAIN_LISTPEERS_ASYNC,
        ((WithIdentity) blockchainAsyncTemplate.getListPeersFunction()).getIdentity());
  }

  @Test
  public void testGetNodeStatus() {
    final BlockchainBaseTemplate base = mock(BlockchainBaseTemplate.class);
    ResultOrErrorFuture<NodeStatus> future =
        ResultOrErrorFutureFactory.supply(() -> new NodeStatus());
    when(base.getNodeStatusFunction()).thenReturn(() -> future);

    final BlockchainAsyncTemplate blockchainAsyncTemplate =
        supplyBlockchainAsyncTemplate(base);

    final ResultOrErrorFuture<NodeStatus> nodeStatus = blockchainAsyncTemplate.getNodeStatus();
    assertTrue(nodeStatus.get().hasResult());
    assertEquals(BLOCKCHAIN_NODESTATUS_ASYNC,
        ((WithIdentity) blockchainAsyncTemplate.getNodeStatusFunction()).getIdentity());
  }

}
