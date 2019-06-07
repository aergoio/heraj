/*
 * @copyright defined in LICENSE.txt
 */

package hera.client.internal;

import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import com.google.common.util.concurrent.ListenableFuture;
import hera.AbstractTestCase;
import hera.Context;
import hera.ContextProvider;
import hera.api.function.Function1;
import hera.api.model.Account;
import hera.api.model.AccountFactory;
import hera.api.model.AccountTotalVote;
import hera.api.model.BlockchainStatus;
import hera.api.model.BytesValue;
import hera.api.model.ChainInfo;
import hera.api.model.ChainStats;
import hera.api.model.ElectedCandidate;
import hera.api.model.NodeStatus;
import hera.api.model.Peer;
import hera.api.model.PeerMetric;
import hera.api.model.ServerInfo;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;
import types.AergoRPCServiceGrpc.AergoRPCServiceFutureStub;
import types.Metric;
import types.Rpc;

@PrepareForTest({AergoRPCServiceFutureStub.class})
public class BlockchainBaseTemplateTest extends AbstractTestCase {

  @Override
  public void setUp() {
    super.setUp();
  }

  protected BlockchainBaseTemplate supplyBlockchainBaseTemplate(
      final AergoRPCServiceFutureStub aergoService) {
    final BlockchainBaseTemplate blockchainBaseTemplate = new BlockchainBaseTemplate();
    blockchainBaseTemplate.aergoService = aergoService;
    blockchainBaseTemplate.contextProvider = new ContextProvider() {
      @Override
      public Context get() {
        return context;
      }
    };
    return blockchainBaseTemplate;
  }

  @Test
  public void testGetBlockchainStatus() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture<Rpc.BlockchainStatus> mockListenableFuture =
        service.submit(new Callable<Rpc.BlockchainStatus>() {
          @Override
          public Rpc.BlockchainStatus call() throws Exception {
            return Rpc.BlockchainStatus.newBuilder().build();
          }
        });
    when(aergoService.blockchain(any(Rpc.Empty.class))).thenReturn(mockListenableFuture);

    final BlockchainBaseTemplate blockchainBaseTemplate =
        supplyBlockchainBaseTemplate(aergoService);

    final FinishableFuture<BlockchainStatus> blockchainStatus =
        blockchainBaseTemplate.getBlockchainStatusFunction().apply();
    assertNotNull(blockchainStatus.get());
  }

  @Test
  public void testGetChainInfo() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture<Rpc.ChainInfo> mockListenableFuture =
        service.submit(new Callable<Rpc.ChainInfo>() {
          @Override
          public Rpc.ChainInfo call() throws Exception {
            return Rpc.ChainInfo.newBuilder().build();
          }
        });
    when(aergoService.getChainInfo(any(Rpc.Empty.class))).thenReturn(mockListenableFuture);

    final BlockchainBaseTemplate blockchainBaseTemplate =
        supplyBlockchainBaseTemplate(aergoService);

    final FinishableFuture<ChainInfo> chainInfo =
        blockchainBaseTemplate.getChainInfoFunction().apply();
    assertNotNull(chainInfo.get());
  }

  @Test
  public void testGetChainStats() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture<Rpc.ChainStats> mockListenableFuture =
        service.submit(new Callable<Rpc.ChainStats>() {
          @Override
          public Rpc.ChainStats call() throws Exception {
            return Rpc.ChainStats.newBuilder().build();
          }
        });
    when(aergoService.chainStat(any(Rpc.Empty.class))).thenReturn(mockListenableFuture);

    final BlockchainBaseTemplate blockchainBaseTemplate =
        supplyBlockchainBaseTemplate(aergoService);

    final FinishableFuture<ChainStats> chainStats =
        blockchainBaseTemplate.getChainStatsFunction().apply();
    assertNotNull(chainStats.get());
  }

  @Test
  public void testListPeers() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture<Rpc.PeerList> mockListenableFuture =
        service.submit(new Callable<Rpc.PeerList>() {
          @Override
          public Rpc.PeerList call() throws Exception {
            return Rpc.PeerList.newBuilder().build();
          }
        });
    when(aergoService.getPeers(any(Rpc.PeersParams.class))).thenReturn(mockListenableFuture);

    final BlockchainBaseTemplate blockchainBaseTemplate =
        supplyBlockchainBaseTemplate(aergoService);

    final FinishableFuture<List<Peer>> peers =
        blockchainBaseTemplate.getListPeersFunction().apply(false, false);
    assertNotNull(peers.get());
  }

  @Test
  public void testListPeerMetrics() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture<Metric.Metrics> mockListenableFuture =
        service.submit(new Callable<Metric.Metrics>() {
          @Override
          public Metric.Metrics call() throws Exception {
            return Metric.Metrics.newBuilder().build();
          }
        });
    when(aergoService.metric(any(Metric.MetricsRequest.class))).thenReturn(mockListenableFuture);

    final BlockchainBaseTemplate blockchainBaseTemplate =
        supplyBlockchainBaseTemplate(aergoService);

    final FinishableFuture<List<PeerMetric>> peers =
        blockchainBaseTemplate.getListPeersMetricsFunction().apply();
    assertNotNull(peers.get());
  }

  @Test
  public void testGetServerInfo() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture<Rpc.ServerInfo> mockListenableFuture =
        service.submit(new Callable<Rpc.ServerInfo>() {
          @Override
          public Rpc.ServerInfo call() throws Exception {
            return Rpc.ServerInfo.newBuilder().build();
          }
        });
    when(aergoService.getServerInfo(any(Rpc.KeyParams.class))).thenReturn(mockListenableFuture);

    final BlockchainBaseTemplate blockchainBaseTemplate =
        supplyBlockchainBaseTemplate(aergoService);

    final FinishableFuture<ServerInfo> nodeStatus =
        blockchainBaseTemplate.getServerInfoFunction().apply(new ArrayList<String>());
    assertNotNull(nodeStatus.get());
  }

  @Test
  public void testGetNodeStatus() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture<Rpc.SingleBytes> mockListenableFuture =
        service.submit(new Callable<Rpc.SingleBytes>() {
          @Override
          public Rpc.SingleBytes call() throws Exception {
            return Rpc.SingleBytes.newBuilder().build();
          }
        });
    when(aergoService.nodeState(any(Rpc.NodeReq.class))).thenReturn(mockListenableFuture);

    final BlockchainBaseTemplate blockchainBaseTemplate =
        supplyBlockchainBaseTemplate(aergoService);

    final FinishableFuture<NodeStatus> nodeStatus =
        blockchainBaseTemplate.getNodeStatusFunction().apply();
    assertNotNull(nodeStatus.get());
  }

  @Test
  public void testVote() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    final FinishableFuture<TxHash> future = new FinishableFuture<TxHash>();
    future.success(new TxHash(BytesValue.of(randomUUID().toString().getBytes())));
    TransactionBaseTemplate mockTransactionBaseTemplate = mock(TransactionBaseTemplate.class);
    when(mockTransactionBaseTemplate.getCommitFunction())
        .thenReturn(new Function1<Transaction, FinishableFuture<TxHash>>() {
          @Override
          public FinishableFuture<TxHash> apply(Transaction t) {
            return future;
          }
        });

    final BlockchainBaseTemplate blockchainBaseTemplate =
        supplyBlockchainBaseTemplate(aergoService);
    blockchainBaseTemplate.transactionBaseTemplate = mockTransactionBaseTemplate;

    final AergoKey key = new AergoKeyGenerator().create();
    final Account account = new AccountFactory().create(key);
    final FinishableFuture<TxHash> nameTxHash =
        blockchainBaseTemplate.getVoteFunction().apply(account,
            randomUUID().toString(), asList(new String[] {randomUUID().toString()}),
            account.incrementAndGetNonce());
    assertNotNull(nameTxHash.get());
  }

  @Test
  public void testListElected() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture<Rpc.VoteList> mockListenableFuture =
        service.submit(new Callable<Rpc.VoteList>() {
          @Override
          public Rpc.VoteList call() throws Exception {
            return Rpc.VoteList.newBuilder().build();
          }
        });
    when(aergoService.getVotes(any(Rpc.VoteParams.class))).thenReturn(mockListenableFuture);

    final BlockchainBaseTemplate blockchainBaseTemplate =
        supplyBlockchainBaseTemplate(aergoService);

    final FinishableFuture<List<ElectedCandidate>> electedBlockProducers =
        blockchainBaseTemplate.getListElectedFunction().apply(randomUUID().toString(), 23);
    assertNotNull(electedBlockProducers.get());
  }

  @Test
  public void testGetVotesOf() {
    final AergoRPCServiceFutureStub aergoService = mock(AergoRPCServiceFutureStub.class);
    ListenableFuture<Rpc.AccountVoteInfo> mockListenableFuture =
        service.submit(new Callable<Rpc.AccountVoteInfo>() {
          @Override
          public Rpc.AccountVoteInfo call() throws Exception {
            return Rpc.AccountVoteInfo.newBuilder().build();
          }
        });
    when(aergoService.getAccountVotes(any(Rpc.AccountAddress.class)))
        .thenReturn(mockListenableFuture);

    final BlockchainBaseTemplate blockchainBaseTemplate =
        supplyBlockchainBaseTemplate(aergoService);

    final FinishableFuture<AccountTotalVote> votes =
        blockchainBaseTemplate.getVotesOfFunction().apply(accountAddress);
    assertNotNull(votes.get());
  }

}
