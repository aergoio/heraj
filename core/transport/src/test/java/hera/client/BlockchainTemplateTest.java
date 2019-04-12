/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.TransportConstants.BLOCKCHAIN_BLOCKCHAINSTATUS;
import static hera.TransportConstants.BLOCKCHAIN_CHAININFO;
import static hera.TransportConstants.BLOCKCHAIN_LIST_ELECTED;
import static hera.TransportConstants.BLOCKCHAIN_LIST_PEERS;
import static hera.TransportConstants.BLOCKCHAIN_NODESTATUS;
import static hera.TransportConstants.BLOCKCHAIN_PEERMETRICS;
import static hera.TransportConstants.BLOCKCHAIN_SERVERINFO;
import static hera.TransportConstants.BLOCKCHAIN_VOTE;
import static hera.TransportConstants.BLOCKCHAIN_VOTESOF;
import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hera.AbstractTestCase;
import hera.ContextProvider;
import hera.api.function.Function0;
import hera.api.function.Function1;
import hera.api.function.Function2;
import hera.api.function.Function4;
import hera.api.function.WithIdentity;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountFactory;
import hera.api.model.AccountTotalVote;
import hera.api.model.BlockHash;
import hera.api.model.BlockchainStatus;
import hera.api.model.BytesValue;
import hera.api.model.ChainIdHash;
import hera.api.model.ChainInfo;
import hera.api.model.ElectedCandidate;
import hera.api.model.ModuleStatus;
import hera.api.model.NodeStatus;
import hera.api.model.Peer;
import hera.api.model.PeerMetric;
import hera.api.model.ServerInfo;
import hera.api.model.TxHash;
import hera.key.AergoKeyGenerator;
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
  public void testGetChainIdHash() {
    final BlockchainBaseTemplate base = mock(BlockchainBaseTemplate.class);
    final FinishableFuture<BlockchainStatus> future = new FinishableFuture<BlockchainStatus>();
    future.success(new BlockchainStatus(10L, BlockHash.of(BytesValue.EMPTY),
        randomUUID().toString(), chainIdHash));
    when(base.getBlockchainStatusFunction())
        .thenReturn(new Function0<FinishableFuture<BlockchainStatus>>() {
          @Override
          public FinishableFuture<BlockchainStatus> apply() {
            return future;
          }
        });

    final BlockchainTemplate blockchainTemplate = supplyBlockchainTemplate(base);

    final ChainIdHash chainIdHash = blockchainTemplate.getChainIdHash();
    assertNotNull(chainIdHash);
  }

  @Test
  public void testGetBlockchainStatus() {
    final BlockchainBaseTemplate base = mock(BlockchainBaseTemplate.class);
    final FinishableFuture<BlockchainStatus> future = new FinishableFuture<BlockchainStatus>();
    future.success(new BlockchainStatus(10L, BlockHash.of(BytesValue.EMPTY),
        randomUUID().toString(), chainIdHash));
    when(base.getBlockchainStatusFunction())
        .thenReturn(new Function0<FinishableFuture<BlockchainStatus>>() {
          @Override
          public FinishableFuture<BlockchainStatus> apply() {
            return future;
          }
        });

    final BlockchainTemplate blockchainTemplate = supplyBlockchainTemplate(base);

    final BlockchainStatus blockchainStatus =
        blockchainTemplate.getBlockchainStatus();
    assertNotNull(blockchainStatus);
    assertEquals(BLOCKCHAIN_BLOCKCHAINSTATUS,
        ((WithIdentity) blockchainTemplate.getBlockchainStatusFunction()).getIdentity());
  }

  @Test
  public void testGetChainInfo() {
    final BlockchainBaseTemplate base = mock(BlockchainBaseTemplate.class);
    final FinishableFuture<ChainInfo> future = new FinishableFuture<ChainInfo>();
    future.success(mock(ChainInfo.class));
    when(base.getChainInfoFunction())
        .thenReturn(new Function0<FinishableFuture<ChainInfo>>() {
          @Override
          public FinishableFuture<ChainInfo> apply() {
            return future;
          }
        });

    final BlockchainTemplate blockchainTemplate = supplyBlockchainTemplate(base);

    final ChainInfo chainInfo = blockchainTemplate.getChainInfo();
    assertNotNull(chainInfo);
    assertEquals(BLOCKCHAIN_CHAININFO,
        ((WithIdentity) blockchainTemplate.getChainInfoFunction()).getIdentity());
  }

  @Test
  public void testListPeers() {
    final BlockchainBaseTemplate base = mock(BlockchainBaseTemplate.class);
    final FinishableFuture<List<Peer>> future = new FinishableFuture<List<Peer>>();
    future.success(new ArrayList<Peer>());
    when(base.getListPeersFunction()).thenReturn(new Function2<Boolean, Boolean,
        FinishableFuture<List<Peer>>>() {
      @Override
      public FinishableFuture<List<Peer>> apply(Boolean t1, Boolean t2) {
        return future;
      }
    });

    final BlockchainTemplate blockchainTemplate = supplyBlockchainTemplate(base);

    final List<Peer> peers = blockchainTemplate.listPeers(true, true);
    assertNotNull(peers);
    assertEquals(BLOCKCHAIN_LIST_PEERS,
        ((WithIdentity) blockchainTemplate.getListPeersFunction()).getIdentity());
  }

  @Test
  public void testListPeerMetrics() {
    final BlockchainBaseTemplate base = mock(BlockchainBaseTemplate.class);
    final FinishableFuture<List<PeerMetric>> future = new FinishableFuture<List<PeerMetric>>();
    future.success(new ArrayList<PeerMetric>());
    when(base.getListPeersMetricsFunction())
        .thenReturn(new Function0<FinishableFuture<List<PeerMetric>>>() {
          @Override
          public FinishableFuture<List<PeerMetric>> apply() {
            return future;
          }
        });

    final BlockchainTemplate blockchainTemplate = supplyBlockchainTemplate(base);

    final List<PeerMetric> peerMetrics = blockchainTemplate.listPeerMetrics();
    assertNotNull(peerMetrics);
    assertEquals(BLOCKCHAIN_PEERMETRICS,
        ((WithIdentity) blockchainTemplate.getListPeerMetricsFunction()).getIdentity());
  }

  @Test
  public void testGetServerInfo() {
    final BlockchainBaseTemplate base = mock(BlockchainBaseTemplate.class);
    final FinishableFuture<ServerInfo> future = new FinishableFuture<ServerInfo>();
    future.success(mock(ServerInfo.class));
    when(base.getServerInfoFunction())
        .thenReturn(new Function1<List<String>, FinishableFuture<ServerInfo>>() {
          @Override
          public FinishableFuture<ServerInfo> apply(List<String> t) {
            return future;
          }
        });

    final BlockchainTemplate blockchainTemplate = supplyBlockchainTemplate(base);

    final ServerInfo nodeStatus = blockchainTemplate.getServerInfo(new ArrayList<String>());
    assertNotNull(nodeStatus);
    assertEquals(BLOCKCHAIN_SERVERINFO,
        ((WithIdentity) blockchainTemplate.getServerInfoFunction()).getIdentity());
  }

  @Test
  public void testGetNodeStatus() {
    final BlockchainBaseTemplate base = mock(BlockchainBaseTemplate.class);
    final FinishableFuture<NodeStatus> future = new FinishableFuture<NodeStatus>();
    future.success(new NodeStatus(new ArrayList<ModuleStatus>()));
    when(base.getNodeStatusFunction()).thenReturn(new Function0<FinishableFuture<NodeStatus>>() {
      @Override
      public FinishableFuture<NodeStatus> apply() {
        return future;
      }
    });

    final BlockchainTemplate blockchainTemplate = supplyBlockchainTemplate(base);

    final NodeStatus nodeStatus = blockchainTemplate.getNodeStatus();
    assertNotNull(nodeStatus);
    assertEquals(BLOCKCHAIN_NODESTATUS,
        ((WithIdentity) blockchainTemplate.getNodeStatusFunction()).getIdentity());
  }

  @Test
  public void testVote() {
    final BlockchainBaseTemplate base = mock(BlockchainBaseTemplate.class);
    final FinishableFuture<TxHash> future = new FinishableFuture<TxHash>();
    final TxHash mockHash = mock(TxHash.class);
    future.success(mockHash);
    when(base.getVoteFunction()).thenReturn(new Function4<Account, String, List<String>,
        Long, FinishableFuture<TxHash>>() {

      @Override
      public FinishableFuture<TxHash> apply(Account t1, String t2, List<String> t3, Long t4) {
        return future;
      }
    });

    final BlockchainTemplate blockchainTemplate = supplyBlockchainTemplate(base);

    final Account account = new AccountFactory().create(new AergoKeyGenerator().create());
    final FinishableFuture<TxHash> voteHash = blockchainTemplate.getVoteFunction().apply(account,
        randomUUID().toString(), asList(new String[] {randomUUID().toString()}),
        account.incrementAndGetNonce());
    assertNotNull(voteHash);
    assertEquals(BLOCKCHAIN_VOTE,
        ((WithIdentity) blockchainTemplate.getVoteFunction()).getIdentity());
  }

  @Test
  public void testListElected() {
    final BlockchainBaseTemplate base = mock(BlockchainBaseTemplate.class);
    final FinishableFuture<List<ElectedCandidate>> future =
        new FinishableFuture<List<ElectedCandidate>>();
    final List<ElectedCandidate> mockBps = new ArrayList<ElectedCandidate>();
    future.success(mockBps);
    when(base.getListElectedFunction()).thenReturn(new Function2<String, Integer,
        FinishableFuture<List<ElectedCandidate>>>() {

      @Override
      public FinishableFuture<List<ElectedCandidate>> apply(String t1, Integer t2) {
        return future;
      }
    });

    final BlockchainTemplate blockchainTemplate = supplyBlockchainTemplate(base);

    final FinishableFuture<List<ElectedCandidate>> bplist =
        blockchainTemplate.getListElectedFunction().apply(randomUUID().toString(),
            randomUUID().toString().hashCode());
    assertNotNull(bplist);
    assertEquals(BLOCKCHAIN_LIST_ELECTED,
        ((WithIdentity) blockchainTemplate.getListElectedFunction()).getIdentity());
  }

  @Test
  public void testListVotesOf() {
    final BlockchainBaseTemplate base = mock(BlockchainBaseTemplate.class);
    final FinishableFuture<AccountTotalVote> future = new FinishableFuture<AccountTotalVote>();
    final AccountTotalVote mockTotalVote = mock(AccountTotalVote.class);
    future.success(mockTotalVote);
    when(base.getVotesOfFunction()).thenReturn(new Function1<AccountAddress,
        FinishableFuture<AccountTotalVote>>() {

      @Override
      public FinishableFuture<AccountTotalVote> apply(AccountAddress t) {
        return future;
      }
    });

    final BlockchainTemplate blockchainTemplate = supplyBlockchainTemplate(base);

    final Account account = new AccountFactory().create(new AergoKeyGenerator().create());
    final FinishableFuture<AccountTotalVote> votingInfos =
        blockchainTemplate.getVotesOfFunction().apply(account.getAddress());
    assertNotNull(votingInfos);
    assertEquals(BLOCKCHAIN_VOTESOF,
        ((WithIdentity) blockchainTemplate.getVotesOfFunction()).getIdentity());
  }

}
