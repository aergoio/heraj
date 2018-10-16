/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hera.AbstractTestCase;
import hera.api.model.BlockchainStatus;
import hera.api.model.NodeStatus;
import hera.api.model.PeerAddress;
import hera.api.tupleorerror.ResultOrError;
import java.util.List;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import types.AergoRPCServiceGrpc.AergoRPCServiceStub;
import types.Node;
import types.Rpc;

@SuppressWarnings("unchecked")
@PrepareForTest({AergoRPCServiceStub.class, Rpc.BlockchainStatus.class, Rpc.PeerList.class,
    Node.PeerAddress.class, Rpc.SingleBytes.class})
public class BlockChainTemplateTest extends AbstractTestCase {

  @Test
  public void testGetBlockchainStatus() throws Exception {
    ResultOrError<BlockchainStatus> eitherMock = mock(ResultOrError.class);
    when(eitherMock.getResult()).thenReturn(mock(BlockchainStatus.class));
    BlockChainEitherTemplate eitherOperationMock = mock(BlockChainEitherTemplate.class);
    when(eitherOperationMock.getBlockchainStatus()).thenReturn(eitherMock);

    final BlockChainTemplate blockChainTemplate = new BlockChainTemplate();
    blockChainTemplate.blockChainEitherOperation = eitherOperationMock;

    final BlockchainStatus blockchainStatus = blockChainTemplate.getBlockchainStatus();
    assertNotNull(blockchainStatus);
  }

  @Test
  public void testListPeers() throws Exception {
    ResultOrError<List<PeerAddress>> eitherMock = mock(ResultOrError.class);
    when(eitherMock.getResult()).thenReturn(mock(List.class));
    BlockChainEitherTemplate eitherOperationMock = mock(BlockChainEitherTemplate.class);
    when(eitherOperationMock.listPeers()).thenReturn(eitherMock);

    final BlockChainTemplate blockChainTemplate = new BlockChainTemplate();
    blockChainTemplate.blockChainEitherOperation = eitherOperationMock;

    final List<PeerAddress> peerAddresses = blockChainTemplate.listPeers();
    assertNotNull(peerAddresses);
  }

  @Test
  public void testGetNodeStatus() throws Exception {
    ResultOrError<NodeStatus> eitherMock = mock(ResultOrError.class);
    when(eitherMock.getResult()).thenReturn(mock(NodeStatus.class));
    BlockChainEitherTemplate eitherOperationMock = mock(BlockChainEitherTemplate.class);
    when(eitherOperationMock.getNodeStatus()).thenReturn(eitherMock);

    final BlockChainTemplate blockChainTemplate = new BlockChainTemplate();
    blockChainTemplate.blockChainEitherOperation = eitherOperationMock;

    final NodeStatus nodeStatus = blockChainTemplate.getNodeStatus();
    assertNotNull(nodeStatus);
  }

}
