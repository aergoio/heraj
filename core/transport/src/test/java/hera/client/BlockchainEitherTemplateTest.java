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
import hera.api.model.BlockchainStatus;
import hera.api.model.NodeStatus;
import hera.api.model.Peer;
import hera.api.tupleorerror.ResultOrError;
import hera.api.tupleorerror.ResultOrErrorFuture;
import java.util.List;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.Node;
import types.Rpc;

@SuppressWarnings("unchecked")
@PrepareForTest({AergoRPCServiceFutureStub.class, Rpc.BlockchainStatus.class, Rpc.PeerList.class,
    Node.PeerAddress.class, Rpc.SingleBytes.class})
public class BlockchainEitherTemplateTest extends AbstractTestCase {

  @Override
  public void setUp() {
    super.setUp();
  }

  @Test
  public void testGetBlockchainStatus() {
    ResultOrErrorFuture<BlockchainStatus> futureMock = mock(ResultOrErrorFuture.class);
    when(futureMock.get(anyLong(), any())).thenReturn(mock(ResultOrError.class));
    BlockchainAsyncTemplate asyncOperationMock = mock(BlockchainAsyncTemplate.class);
    when(asyncOperationMock.getBlockchainStatus()).thenReturn(futureMock);

    final BlockchainEitherTemplate blockchainTemplate = new BlockchainEitherTemplate();
    blockchainTemplate.setContext(context);
    blockchainTemplate.blockchainAsyncOperation = asyncOperationMock;

    final ResultOrError<BlockchainStatus> blockchainStatus =
        blockchainTemplate.getBlockchainStatus();
    assertNotNull(blockchainStatus);
  }

  @Test
  public void testListPeers() {
    ResultOrErrorFuture<List<Peer>> futureMock = mock(ResultOrErrorFuture.class);
    when(futureMock.get(anyLong(), any())).thenReturn(mock(ResultOrError.class));
    BlockchainAsyncTemplate asyncOperationMock = mock(BlockchainAsyncTemplate.class);
    when(asyncOperationMock.listPeers()).thenReturn(futureMock);

    final BlockchainEitherTemplate blockchainTemplate = new BlockchainEitherTemplate();
    blockchainTemplate.setContext(context);
    blockchainTemplate.blockchainAsyncOperation = asyncOperationMock;

    final ResultOrError<List<Peer>> peerAddresses = blockchainTemplate.listPeers();
    assertNotNull(peerAddresses);
  }

  @Test
  public void testGetNodeStatus() {
    ResultOrErrorFuture<NodeStatus> futureMock = mock(ResultOrErrorFuture.class);
    when(futureMock.get(anyLong(), any())).thenReturn(mock(ResultOrError.class));
    BlockchainAsyncTemplate asyncOperationMock = mock(BlockchainAsyncTemplate.class);
    when(asyncOperationMock.getNodeStatus()).thenReturn(futureMock);

    final BlockchainEitherTemplate blockchainTemplate = new BlockchainEitherTemplate();
    blockchainTemplate.setContext(context);
    blockchainTemplate.blockchainAsyncOperation = asyncOperationMock;

    final ResultOrError<NodeStatus> nodeStatus = blockchainTemplate.getNodeStatus();
    assertNotNull(nodeStatus);
  }

}
