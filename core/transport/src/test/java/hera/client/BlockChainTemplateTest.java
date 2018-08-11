/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hera.AbstractTestCase;
import hera.api.BlockChainAsyncOperation;
import hera.api.model.BlockchainStatus;
import hera.api.model.NodeStatus;
import hera.api.model.PeerAddress;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.Node;
import types.Rpc;

@PrepareForTest({AergoRPCServiceFutureStub.class, Rpc.BlockchainStatus.class, Rpc.PeerList.class,
    Node.PeerAddress.class, Rpc.NodeStatus.class})
public class BlockChainTemplateTest extends AbstractTestCase {

  @Test
  public void testGetBlockchainStatus() throws Exception {
    CompletableFuture<BlockchainStatus> futureMock = mock(CompletableFuture.class);
    when(futureMock.get(anyLong(), any())).thenReturn(mock(BlockchainStatus.class));
    BlockChainAsyncOperation asyncOperationMock = mock(BlockChainAsyncOperation.class);
    when(asyncOperationMock.getBlockchainStatus()).thenReturn(futureMock);

    final BlockChainTemplate blockChainTemplate = new BlockChainTemplate(asyncOperationMock);

    final BlockchainStatus blockchainStatus = blockChainTemplate.getBlockchainStatus();
    assertNotNull(blockchainStatus);
  }

  @Test
  public void testListPeers() throws Exception {
    CompletableFuture<List<PeerAddress>> futureMock = mock(CompletableFuture.class);
    when(futureMock.get(anyLong(), any())).thenReturn(mock(List.class));
    BlockChainAsyncOperation asyncOperationMock = mock(BlockChainAsyncOperation.class);
    when(asyncOperationMock.listPeers()).thenReturn(futureMock);

    final BlockChainTemplate blockChainTemplate = new BlockChainTemplate(asyncOperationMock);

    final List<PeerAddress> peerAddresses = blockChainTemplate.listPeers();
    assertNotNull(peerAddresses);
  }

  @Test
  public void testGetNodeStatus() throws Exception {
    CompletableFuture<NodeStatus> futureMock = mock(CompletableFuture.class);
    when(futureMock.get(anyLong(), any())).thenReturn(mock(NodeStatus.class));
    BlockChainAsyncOperation asyncOperationMock = mock(BlockChainAsyncOperation.class);
    when(asyncOperationMock.getNodeStatus()).thenReturn(futureMock);

    final BlockChainTemplate blockChainTemplate = new BlockChainTemplate(asyncOperationMock);

    final NodeStatus nodeStatus = blockChainTemplate.getNodeStatus();
    assertNotNull(nodeStatus);
  }

}
