/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hera.AbstractTestCase;
import hera.api.model.BlockchainStatus;
import hera.api.model.NodeStatus;
import hera.api.model.Peer;
import hera.api.tupleorerror.ResultOrError;
import hera.api.tupleorerror.ResultOrErrorFuture;
import java.util.List;
import java.util.concurrent.TimeoutException;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.Node;
import types.Rpc;

@SuppressWarnings("unchecked")
@PrepareForTest({AergoRPCServiceFutureStub.class, Rpc.BlockchainStatus.class, Rpc.PeerList.class,
    Node.PeerAddress.class, Rpc.SingleBytes.class})
public class BlockChainEitherTemplateTest extends AbstractTestCase {

  @Test
  public void testGetBlockchainStatus() throws Exception {
    ResultOrErrorFuture<BlockchainStatus> futureMock = mock(ResultOrErrorFuture.class);
    when(futureMock.get(anyLong(), any())).thenReturn(mock(ResultOrError.class));
    BlockChainAsyncTemplate asyncOperationMock = mock(BlockChainAsyncTemplate.class);
    when(asyncOperationMock.getBlockchainStatus()).thenReturn(futureMock);

    final BlockChainEitherTemplate blockChainTemplate = new BlockChainEitherTemplate();
    blockChainTemplate.blockChainAsyncOperation = asyncOperationMock;

    final ResultOrError<BlockchainStatus> blockchainStatus =
        blockChainTemplate.getBlockchainStatus();
    assertNotNull(blockchainStatus);
  }

  @Test
  public void testGetBlockchainStatusWithTimeout() throws Exception {
    ResultOrErrorFuture<BlockchainStatus> futureMock = mock(ResultOrErrorFuture.class);
    when(futureMock.get(anyLong(), any())).thenThrow(TimeoutException.class);
    BlockChainAsyncTemplate asyncOperationMock = mock(BlockChainAsyncTemplate.class);
    when(asyncOperationMock.getBlockchainStatus()).thenReturn(futureMock);

    final BlockChainEitherTemplate blockChainTemplate = new BlockChainEitherTemplate();
    blockChainTemplate.blockChainAsyncOperation = asyncOperationMock;

    final ResultOrError<BlockchainStatus> blockchainStatus =
        blockChainTemplate.getBlockchainStatus();
    assertTrue(blockchainStatus.hasError());
  }

  @Test
  public void testListPeers() throws Exception {
    ResultOrErrorFuture<List<Peer>> futureMock = mock(ResultOrErrorFuture.class);
    when(futureMock.get(anyLong(), any())).thenReturn(mock(ResultOrError.class));
    BlockChainAsyncTemplate asyncOperationMock = mock(BlockChainAsyncTemplate.class);
    when(asyncOperationMock.listPeers()).thenReturn(futureMock);

    final BlockChainEitherTemplate blockChainTemplate = new BlockChainEitherTemplate();
    blockChainTemplate.blockChainAsyncOperation = asyncOperationMock;

    final ResultOrError<List<Peer>> peerAddresses = blockChainTemplate.listPeers();
    assertNotNull(peerAddresses);
  }

  @Test
  public void testListPeersWithTimeout() throws Exception {
    ResultOrErrorFuture<List<Peer>> futureMock = mock(ResultOrErrorFuture.class);
    when(futureMock.get(anyLong(), any())).thenThrow(TimeoutException.class);
    BlockChainAsyncTemplate asyncOperationMock = mock(BlockChainAsyncTemplate.class);
    when(asyncOperationMock.listPeers()).thenReturn(futureMock);

    final BlockChainEitherTemplate blockChainTemplate = new BlockChainEitherTemplate();
    blockChainTemplate.blockChainAsyncOperation = asyncOperationMock;

    final ResultOrError<List<Peer>> peerAddresses = blockChainTemplate.listPeers();
    assertTrue(peerAddresses.hasError());
  }

  @Test
  public void testGetNodeStatus() throws Exception {
    ResultOrErrorFuture<NodeStatus> futureMock = mock(ResultOrErrorFuture.class);
    when(futureMock.get(anyLong(), any())).thenReturn(mock(ResultOrError.class));
    BlockChainAsyncTemplate asyncOperationMock = mock(BlockChainAsyncTemplate.class);
    when(asyncOperationMock.getNodeStatus()).thenReturn(futureMock);

    final BlockChainEitherTemplate blockChainTemplate = new BlockChainEitherTemplate();
    blockChainTemplate.blockChainAsyncOperation = asyncOperationMock;

    final ResultOrError<NodeStatus> nodeStatus = blockChainTemplate.getNodeStatus();
    assertNotNull(nodeStatus);
  }

  @Test
  public void testGetNodeStatusWithError() throws Exception {
    ResultOrErrorFuture<NodeStatus> futureMock = mock(ResultOrErrorFuture.class);
    when(futureMock.get(anyLong(), any())).thenThrow(TimeoutException.class);
    BlockChainAsyncTemplate asyncOperationMock = mock(BlockChainAsyncTemplate.class);
    when(asyncOperationMock.getNodeStatus()).thenReturn(futureMock);

    final BlockChainEitherTemplate blockChainTemplate = new BlockChainEitherTemplate();
    blockChainTemplate.blockChainAsyncOperation = asyncOperationMock;

    final ResultOrError<NodeStatus> nodeStatus = blockChainTemplate.getNodeStatus();
    assertTrue(nodeStatus.hasError());
  }

}
