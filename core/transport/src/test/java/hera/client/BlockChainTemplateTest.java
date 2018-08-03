/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hera.AbstractTestCase;
import hera.api.model.BlockchainStatus;
import hera.api.model.NodeStatus;
import hera.api.model.PeerAddress;
import hera.transport.ModelConverter;
import java.util.List;
import org.junit.BeforeClass;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import types.AergoRPCServiceGrpc.AergoRPCServiceBlockingStub;
import types.Node;
import types.Rpc;

@PrepareForTest({AergoRPCServiceBlockingStub.class, Rpc.BlockchainStatus.class, Rpc.PeerList.class,
    Node.PeerAddress.class, Rpc.NodeStatus.class})
public class BlockChainTemplateTest extends AbstractTestCase {

  protected static final ModelConverter<BlockchainStatus, Rpc.BlockchainStatus> blockchainConverter
      = mock(ModelConverter.class);

  protected static final ModelConverter<PeerAddress, Node.PeerAddress> peerAddressConverter = mock(
      ModelConverter.class);

  protected static final ModelConverter<NodeStatus, Rpc.NodeStatus> nodeStatusConverter = mock(
      ModelConverter.class);

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
        .thenReturn(mock(Rpc.NodeStatus.class));
    when(nodeStatusConverter.convertToDomainModel(any(Rpc.NodeStatus.class)))
        .thenReturn(mock(NodeStatus.class));
  }

  @Test
  public void testGetBlockchainStatus() {
    final AergoRPCServiceBlockingStub aergoService = mock(AergoRPCServiceBlockingStub.class);
    when(aergoService.blockchain(any())).thenReturn(mock(Rpc.BlockchainStatus.class));

    final BlockChainTemplate blockChainTemplate = new BlockChainTemplate(aergoService,
        blockchainConverter, peerAddressConverter, nodeStatusConverter);

    final BlockchainStatus status = blockChainTemplate.getBlockchainStatus();
    assertNotNull(status);
  }

  @Test
  public void testListPeers() {
    final AergoRPCServiceBlockingStub aergoService = mock(AergoRPCServiceBlockingStub.class);
    when(aergoService.getPeers(any())).thenReturn(mock(Rpc.PeerList.class));

    final BlockChainTemplate blockChainTemplate = new BlockChainTemplate(aergoService,
        blockchainConverter, peerAddressConverter, nodeStatusConverter);

    final List<PeerAddress> peers = blockChainTemplate.listPeers();
    assertNotNull(peers);
  }

  @Test
  public void testGetNodeStatus() {
    final AergoRPCServiceBlockingStub aergoService = mock(AergoRPCServiceBlockingStub.class);
    when(aergoService.nodeState(any())).thenReturn(mock(Rpc.NodeStatus.class));

    final BlockChainTemplate blockChainTemplate = new BlockChainTemplate(aergoService,
        blockchainConverter, peerAddressConverter, nodeStatusConverter);

    final NodeStatus nodestatus = blockChainTemplate.getNodeStatus();
    assertNotNull(nodestatus);
  }

}
