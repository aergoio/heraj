/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.TransportConstants.BLOCKCHAIN_BLOCKCHAINSTATUS;
import static hera.TransportConstants.BLOCKCHAIN_CHAININFO;
import static hera.TransportConstants.BLOCKCHAIN_LIST_ELECTED_BPS;
import static hera.TransportConstants.BLOCKCHAIN_LIST_PEERS;
import static hera.TransportConstants.BLOCKCHAIN_LIST_VOTESOF;
import static hera.TransportConstants.BLOCKCHAIN_NODESTATUS;
import static hera.TransportConstants.BLOCKCHAIN_PEERMETRICS;
import static hera.TransportConstants.BLOCKCHAIN_VOTE;
import static hera.api.model.BytesValue.of;
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
import hera.api.function.Function3;
import hera.api.function.WithIdentity;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountFactory;
import hera.api.model.BlockHash;
import hera.api.model.BlockProducer;
import hera.api.model.BlockchainStatus;
import hera.api.model.BytesValue;
import hera.api.model.ChainInfo;
import hera.api.model.ModuleStatus;
import hera.api.model.NodeStatus;
import hera.api.model.Peer;
import hera.api.model.PeerId;
import hera.api.model.PeerMetric;
import hera.api.model.TxHash;
import hera.api.model.VotingInfo;
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
  public void testGetBlockchainStatus() {
    final BlockchainBaseTemplate base = mock(BlockchainBaseTemplate.class);
    final FinishableFuture<BlockchainStatus> future = new FinishableFuture<BlockchainStatus>();
    future.success(new BlockchainStatus(10L, BlockHash.of(BytesValue.EMPTY)));
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
    when(base.getVoteFunction()).thenReturn(new Function3<Account, PeerId,
        Long, FinishableFuture<TxHash>>() {

      @Override
      public FinishableFuture<TxHash> apply(Account t1, PeerId t2, Long t3) {
        return future;
      }
    });

    final BlockchainTemplate blockchainTemplate = supplyBlockchainTemplate(base);

    final Account account = new AccountFactory().create(new AergoKeyGenerator().create());
    final FinishableFuture<TxHash> voteHash = blockchainTemplate.getVoteFunction().apply(account,
        new PeerId(of(randomUUID().toString().getBytes())), account.incrementAndGetNonce());
    assertNotNull(voteHash);
    assertEquals(BLOCKCHAIN_VOTE,
        ((WithIdentity) blockchainTemplate.getVoteFunction()).getIdentity());
  }

  @Test
  public void testListElectedBlockProducers() {
    final BlockchainBaseTemplate base = mock(BlockchainBaseTemplate.class);
    final FinishableFuture<List<BlockProducer>> future =
        new FinishableFuture<List<BlockProducer>>();
    final List<BlockProducer> mockBps = new ArrayList<BlockProducer>();
    future.success(mockBps);
    when(base.getListElectedBlockProducersFunction()).thenReturn(new Function1<Long,
        FinishableFuture<List<BlockProducer>>>() {

      @Override
      public FinishableFuture<List<BlockProducer>> apply(Long t) {
        return future;
      }
    });

    final BlockchainTemplate blockchainTemplate = supplyBlockchainTemplate(base);

    final FinishableFuture<List<BlockProducer>> bplist =
        blockchainTemplate.getListElectedBlockProducersFunction().apply(1L);
    assertNotNull(bplist);
    assertEquals(BLOCKCHAIN_LIST_ELECTED_BPS,
        ((WithIdentity) blockchainTemplate.getListElectedBlockProducersFunction()).getIdentity());
  }

  @Test
  public void testListVotesOf() {
    final BlockchainBaseTemplate base = mock(BlockchainBaseTemplate.class);
    final FinishableFuture<List<VotingInfo>> future =
        new FinishableFuture<List<VotingInfo>>();
    final List<VotingInfo> mockVotingInfos = new ArrayList<VotingInfo>();
    future.success(mockVotingInfos);
    when(base.getListVotesOfFunction()).thenReturn(new Function1<AccountAddress,
        FinishableFuture<List<VotingInfo>>>() {

      @Override
      public FinishableFuture<List<VotingInfo>> apply(AccountAddress t) {
        return future;
      }
    });

    final BlockchainTemplate blockchainTemplate = supplyBlockchainTemplate(base);

    final Account account = new AccountFactory().create(new AergoKeyGenerator().create());
    final FinishableFuture<List<VotingInfo>> votingInfos =
        blockchainTemplate.getListVotesOfFunction().apply(account.getAddress());
    assertNotNull(votingInfos);
    assertEquals(BLOCKCHAIN_LIST_VOTESOF,
        ((WithIdentity) blockchainTemplate.getListVotesOfFunction()).getIdentity());
  }

}
