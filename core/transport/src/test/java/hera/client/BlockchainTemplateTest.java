/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static java.util.Collections.emptyList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hera.AbstractTestCase;
import hera.Context;
import hera.ContextStorage;
import hera.EmptyContext;
import hera.Invocation;
import hera.Requester;
import hera.api.model.BlockchainStatus;
import hera.api.model.BytesValue;
import hera.api.model.ChainIdHash;
import hera.api.model.ChainInfo;
import hera.api.model.ChainStats;
import hera.api.model.NodeStatus;
import hera.api.model.Peer;
import hera.api.model.PeerMetric;
import hera.api.model.ServerInfo;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.mockito.ArgumentMatchers;

public class BlockchainTemplateTest extends AbstractTestCase {

  protected final ContextStorage<Context> contextStorage = new UnmodifiableContextStorage(
      EmptyContext.getInstance());

  @Test
  public void testGetChainIdHash() throws Exception {
    // given
    final BlockchainTemplate blockchainTemplate = new BlockchainTemplate(contextStorage);
    final Requester mockRequester = mock(Requester.class);
    final ChainIdHash expected = ChainIdHash.of(BytesValue.EMPTY);
    final BlockchainStatus blockchainStatus = BlockchainStatus.newBuilder()
        .chainIdHash(expected)
        .build();
    when(mockRequester.request(ArgumentMatchers.<Invocation<BlockchainStatus>>any()))
        .thenReturn(blockchainStatus);
    blockchainTemplate.requester = mockRequester;

    // then
    final ChainIdHash actual = blockchainTemplate.getChainIdHash();
    assertEquals(expected, actual);
  }

  @Test
  public void testGetBlockchainStatus() throws Exception {
    // given
    final BlockchainTemplate blockchainTemplate = new BlockchainTemplate(contextStorage);
    final Requester mockRequester = mock(Requester.class);
    final BlockchainStatus expected = BlockchainStatus.newBuilder().build();
    when(mockRequester.request(ArgumentMatchers.<Invocation<BlockchainStatus>>any()))
        .thenReturn(expected);
    blockchainTemplate.requester = mockRequester;

    // then
    final BlockchainStatus actual = blockchainTemplate.getBlockchainStatus();
    assertEquals(expected, actual);
  }

  @Test
  public void testGetChainInfo() throws Exception {
    // given
    final BlockchainTemplate blockchainTemplate = new BlockchainTemplate(contextStorage);
    final Requester mockRequester = mock(Requester.class);
    final ChainInfo expected = ChainInfo.newBuilder().build();
    when(mockRequester.request(ArgumentMatchers.<Invocation<ChainInfo>>any()))
        .thenReturn(expected);
    blockchainTemplate.requester = mockRequester;

    // then
    final ChainInfo actual = blockchainTemplate.getChainInfo();
    assertEquals(expected, actual);
  }

  @Test
  public void testGetChainStats() throws Exception {
    // given
    final BlockchainTemplate blockchainTemplate = new BlockchainTemplate(contextStorage);
    final Requester mockRequester = mock(Requester.class);
    final ChainStats expected = ChainStats.newBuilder().build();
    when(mockRequester.request(ArgumentMatchers.<Invocation<ChainStats>>any()))
        .thenReturn(expected);
    blockchainTemplate.requester = mockRequester;

    // then
    final ChainStats actual = blockchainTemplate.getChainStats();
    assertEquals(expected, actual);
  }

  @Test
  public void testListPeers() throws Exception {
    // given
    final BlockchainTemplate blockchainTemplate = new BlockchainTemplate(contextStorage);
    final Requester mockRequester = mock(Requester.class);
    final List<Peer> expected = emptyList();
    when(mockRequester.request(ArgumentMatchers.<Invocation<List>>any()))
        .thenReturn(expected);
    blockchainTemplate.requester = mockRequester;

    // then
    final List<Peer> actual = blockchainTemplate.listPeers();
    assertEquals(expected, actual);
  }

  @Test
  public void testListPeerMetrics() throws Exception {
    // given
    final BlockchainTemplate blockchainTemplate = new BlockchainTemplate(contextStorage);
    final Requester mockRequester = mock(Requester.class);
    final List<PeerMetric> expected = emptyList();
    when(mockRequester.request(ArgumentMatchers.<Invocation<List>>any()))
        .thenReturn(expected);
    blockchainTemplate.requester = mockRequester;

    // then
    final List<PeerMetric> actual = blockchainTemplate.listPeerMetrics();
    assertEquals(expected, actual);
  }

  @Test
  public void testGetServerInfo() throws Exception {
    // given
    final BlockchainTemplate blockchainTemplate = new BlockchainTemplate(contextStorage);
    final Requester mockRequester = mock(Requester.class);
    final ServerInfo expected = ServerInfo.newBuilder().build();
    when(mockRequester.request(ArgumentMatchers.<Invocation<ServerInfo>>any()))
        .thenReturn(expected);
    blockchainTemplate.requester = mockRequester;

    // then
    final ServerInfo actual = blockchainTemplate.getServerInfo(Collections.<String>emptyList());
    assertEquals(expected, actual);
  }

  @Test
  public void testGetNodeStatus() throws Exception {
    // given
    final BlockchainTemplate blockchainTemplate = new BlockchainTemplate(contextStorage);
    final Requester mockRequester = mock(Requester.class);
    final NodeStatus expected = NodeStatus.newBuilder().build();
    when(mockRequester.request(ArgumentMatchers.<Invocation<NodeStatus>>any()))
        .thenReturn(expected);
    blockchainTemplate.requester = mockRequester;

    // then
    final NodeStatus actual = blockchainTemplate.getNodeStatus();
    assertEquals(expected, actual);
  }

}

