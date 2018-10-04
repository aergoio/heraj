/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.util.concurrent.ListenableFuture;
import hera.AbstractTestCase;
import hera.api.model.BlockchainStatus;
import hera.api.model.NodeStatus;
import hera.api.model.PeerAddress;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.transport.ModelConverter;
import java.util.List;
import org.junit.BeforeClass;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.Node;
import types.Rpc;

@SuppressWarnings({"rawtypes", "unchecked"})
@PrepareForTest({AergoRPCServiceFutureStub.class, Rpc.BlockchainStatus.class, Rpc.PeerList.class,
    Node.PeerAddress.class, Rpc.SingleBytes.class})
public class BlockChainAsyncTemplateTest extends AbstractTestCase {

  protected static final ModelConverter<BlockchainStatus, Rpc.BlockchainStatus> blockchainConverter =
      mock(ModelConverter.class);

  protected static final ModelConverter<PeerAddress, Node.PeerAddress> peerAddressConverter =
      mock(ModelConverter.class);

  protected static final ModelConverter<NodeStatus, Rpc.SingleBytes> nodeStatusConverter =
      mock(ModelConverter.class);

  @BeforeClass
  public static void setUpBeforeClass() {
    when(blockchainConverter.convertToRpcModel(any(BlockchainStatus.class)))
        .thenReturn(mock(Rpc.BlockchainStatus.class));
    when(blockchainConverter.convertToDomainModel(any(Rpc.BlockchainStatus.class)))
        .thenReturn(mock(BlockchainStatus.class));
    when(peerAddressConverter.convertToRpcModel(any(PeerAddress.class)))
        .thenReturn(mock(types.Node.PeerAddress.class));
    when(peerAddressConverter.convertToDomainModel(any(Node.PeerAddress.class)))
        .thenReturn(mock(PeerAddress.class));
    when(nodeStatusConverter.convertToRpcModel(any(NodeStatus.class)))
        .thenReturn(mock(Rpc.SingleBytes.class));
    when(nodeStatusConverter.convertToDomainModel(any(Rpc.SingleBytes.class)))
        .thenReturn(mock(NodeStatus.class));
  }

  @Test
  public void testGetBlockchainStatus() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture = mock(ListenableFuture.class);
    when(aergoService.blockchain(any())).thenReturn(mockListenableFuture);

    final BlockChainAsyncTemplate blockChainAsyncTemplate = new BlockChainAsyncTemplate(
        aergoService, blockchainConverter, peerAddressConverter, nodeStatusConverter);

    final ResultOrErrorFuture<BlockchainStatus> blockchainStatus =
        blockChainAsyncTemplate.getBlockchainStatus();
    assertNotNull(blockchainStatus);
  }

  @Test
  public void testListPeers() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture = mock(ListenableFuture.class);
    when(aergoService.getPeers(any())).thenReturn(mockListenableFuture);

    final BlockChainAsyncTemplate blockChainAsyncTemplate = new BlockChainAsyncTemplate(
        aergoService, blockchainConverter, peerAddressConverter, nodeStatusConverter);

    final ResultOrErrorFuture<List<PeerAddress>> peers = blockChainAsyncTemplate.listPeers();
    assertNotNull(peers);
  }

  @Test
  public void testGetNodeStatus() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture mockListenableFuture = mock(ListenableFuture.class);
    when(aergoService.nodeState(any())).thenReturn(mockListenableFuture);

    final BlockChainAsyncTemplate blockChainAsyncTemplate = new BlockChainAsyncTemplate(
        aergoService, blockchainConverter, peerAddressConverter, nodeStatusConverter);

    final ResultOrErrorFuture<NodeStatus> nodeStatus = blockChainAsyncTemplate.getNodeStatus();
    assertNotNull(nodeStatus);
  }

}
