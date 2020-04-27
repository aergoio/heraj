/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hera.AbstractTestCase;
import hera.api.AccountOperation;
import hera.api.BlockOperation;
import hera.api.BlockchainOperation;
import hera.api.ContractOperation;
import hera.api.TransactionOperation;
import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.model.AccountTotalVote;
import hera.api.model.Block;
import hera.api.model.BlockHash;
import hera.api.model.BlockMetadata;
import hera.api.model.BlockchainStatus;
import hera.api.model.ChainIdHash;
import hera.api.model.ChainInfo;
import hera.api.model.ChainStats;
import hera.api.model.ContractAddress;
import hera.api.model.ContractInterface;
import hera.api.model.ContractInvocation;
import hera.api.model.ContractResult;
import hera.api.model.ContractTxHash;
import hera.api.model.ContractTxReceipt;
import hera.api.model.ElectedCandidate;
import hera.api.model.Event;
import hera.api.model.EventFilter;
import hera.api.model.NodeStatus;
import hera.api.model.Peer;
import hera.api.model.PeerMetric;
import hera.api.model.ServerInfo;
import hera.api.model.StakeInfo;
import hera.api.model.StreamObserver;
import hera.api.model.Subscription;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.api.model.TxReceipt;
import hera.client.AergoClient;
import java.util.Collections;
import java.util.List;
import org.junit.Test;
import org.mockito.ArgumentMatchers;

public class QueryApiImplTest extends AbstractTestCase {

  @Test
  public void testGetAccountState() {
    // given
    final AccountOperation mockOperation = mock(AccountOperation.class);
    when(mockOperation.getState(any(AccountAddress.class)))
        .thenReturn(AccountState.newBuilder().build());
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getAccountOperation()).thenReturn(mockOperation);
    final ClientProvider mockClientProvider = mock(ClientProvider.class);
    when(mockClientProvider.getClient()).thenReturn(mockClient);

    // then
    final QueryApi queryApi = new QueryApiImpl(mockClientProvider);
    final AccountState accountState = queryApi.getAccountState(anyAccountAddress);
    assertNotNull(accountState);
  }

  @Test
  public void testGetNameOwner() {
    // given
    final AccountOperation mockOperation = mock(AccountOperation.class);
    when(mockOperation.getNameOwner(anyString()))
        .thenReturn(AccountAddress.EMPTY);
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getAccountOperation()).thenReturn(mockOperation);
    final ClientProvider mockClientProvider = mock(ClientProvider.class);
    when(mockClientProvider.getClient()).thenReturn(mockClient);

    // then
    final QueryApi queryApi = new QueryApiImpl(mockClientProvider);
    final AccountAddress accountAddress = queryApi.getNameOwner(anyName);
    assertNotNull(accountAddress);
  }

  @Test
  public void testGetNameOwnerAtHeight() {
    // given
    final AccountOperation mockOperation = mock(AccountOperation.class);
    when(mockOperation.getNameOwner(anyString(), anyLong()))
        .thenReturn(AccountAddress.EMPTY);
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getAccountOperation()).thenReturn(mockOperation);
    final ClientProvider mockClientProvider = mock(ClientProvider.class);
    when(mockClientProvider.getClient()).thenReturn(mockClient);

    // then
    final QueryApi queryApi = new QueryApiImpl(mockClientProvider);
    final AccountAddress accountAddress = queryApi.getNameOwner(anyName, anyHeight);
    assertNotNull(accountAddress);
  }

  @Test
  public void testGetStakingInfo() {
    // given
    final AccountOperation mockOperation = mock(AccountOperation.class);
    when(mockOperation.getStakingInfo(any(AccountAddress.class)))
        .thenReturn(StakeInfo.newBuilder().build());
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getAccountOperation()).thenReturn(mockOperation);
    final ClientProvider mockClientProvider = mock(ClientProvider.class);
    when(mockClientProvider.getClient()).thenReturn(mockClient);

    // then
    final QueryApi queryApi = new QueryApiImpl(mockClientProvider);
    final StakeInfo stakeInfo = queryApi.getStakingInfo(anyAccountAddress);
    assertNotNull(stakeInfo);
  }

  @Test
  public void testListElectedBps() {
    // given
    final AccountOperation mockOperation = mock(AccountOperation.class);
    when(mockOperation.listElected(anyString(), anyInt()))
        .thenReturn(Collections.<ElectedCandidate>emptyList());
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getAccountOperation()).thenReturn(mockOperation);
    final ClientProvider mockClientProvider = mock(ClientProvider.class);
    when(mockClientProvider.getClient()).thenReturn(mockClient);

    // then
    final QueryApi queryApi = new QueryApiImpl(mockClientProvider);
    final List<ElectedCandidate> electedCandidates = queryApi.listElectedBps(anySize);
    assertNotNull(electedCandidates);
  }

  @Test
  public void testListElected() {
    // given
    final AccountOperation mockOperation = mock(AccountOperation.class);
    when(mockOperation.listElected(anyString(), anyInt()))
        .thenReturn(Collections.<ElectedCandidate>emptyList());
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getAccountOperation()).thenReturn(mockOperation);
    final ClientProvider mockClientProvider = mock(ClientProvider.class);
    when(mockClientProvider.getClient()).thenReturn(mockClient);

    // then
    final QueryApi queryApi = new QueryApiImpl(mockClientProvider);
    final List<ElectedCandidate> electedCandidates = queryApi.listElected(anyVoteId, anySize);
    assertNotNull(electedCandidates);
  }

  @Test
  public void testGetVotesOf() {
    // given
    final AccountOperation mockOperation = mock(AccountOperation.class);
    when(mockOperation.getVotesOf(any(AccountAddress.class)))
        .thenReturn(AccountTotalVote.newBuilder().build());
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getAccountOperation()).thenReturn(mockOperation);
    final ClientProvider mockClientProvider = mock(ClientProvider.class);
    when(mockClientProvider.getClient()).thenReturn(mockClient);

    // then
    final QueryApi queryApi = new QueryApiImpl(mockClientProvider);
    final AccountTotalVote accountTotalVote = queryApi.getVotesOf(anyAccountAddress);
    assertNotNull(accountTotalVote);
  }

  @Test
  public void testGetBestBlockHash() {
    // given
    final BlockchainOperation mockOperation = mock(BlockchainOperation.class);
    when(mockOperation.getBlockchainStatus())
        .thenReturn(BlockchainStatus.newBuilder().build());
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getBlockchainOperation()).thenReturn(mockOperation);
    final ClientProvider mockClientProvider = mock(ClientProvider.class);
    when(mockClientProvider.getClient()).thenReturn(mockClient);

    // then
    final QueryApi queryApi = new QueryApiImpl(mockClientProvider);
    final BlockHash blockHash = queryApi.getBestBlockHash();
    assertNotNull(blockHash);
  }

  @Test
  public void testGetBestBlockHeight() {
    // given
    final BlockchainOperation mockOperation = mock(BlockchainOperation.class);
    when(mockOperation.getBlockchainStatus())
        .thenReturn(BlockchainStatus.newBuilder().build());
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getBlockchainOperation()).thenReturn(mockOperation);
    final ClientProvider mockClientProvider = mock(ClientProvider.class);
    when(mockClientProvider.getClient()).thenReturn(mockClient);

    // then
    final QueryApi queryApi = new QueryApiImpl(mockClientProvider);
    final long height = queryApi.getBestBlockHeight();
    assertTrue(0 <= height);
  }

  @Test
  public void testGetChainIdHash() {
    // given
    final BlockchainOperation mockOperation = mock(BlockchainOperation.class);
    when(mockOperation.getBlockchainStatus())
        .thenReturn(BlockchainStatus.newBuilder().build());
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getBlockchainOperation()).thenReturn(mockOperation);
    final ClientProvider mockClientProvider = mock(ClientProvider.class);
    when(mockClientProvider.getClient()).thenReturn(mockClient);

    // then
    final QueryApi queryApi = new QueryApiImpl(mockClientProvider);
    final ChainIdHash chainIdHash = queryApi.getChainIdHash();
    assertNotNull(chainIdHash);
  }

  @Test
  public void testGetBlockchainStatus() {
    // given
    final BlockchainOperation mockOperation = mock(BlockchainOperation.class);
    when(mockOperation.getBlockchainStatus())
        .thenReturn(BlockchainStatus.newBuilder().build());
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getBlockchainOperation()).thenReturn(mockOperation);
    final ClientProvider mockClientProvider = mock(ClientProvider.class);
    when(mockClientProvider.getClient()).thenReturn(mockClient);

    // then
    final QueryApi queryApi = new QueryApiImpl(mockClientProvider);
    final BlockchainStatus blockchainStatus = queryApi.getBlockchainStatus();
    assertNotNull(blockchainStatus);
  }

  @Test
  public void testGetChainInfo() {
    // given
    final BlockchainOperation mockOperation = mock(BlockchainOperation.class);
    when(mockOperation.getChainInfo())
        .thenReturn(ChainInfo.newBuilder().build());
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getBlockchainOperation()).thenReturn(mockOperation);
    final ClientProvider mockClientProvider = mock(ClientProvider.class);
    when(mockClientProvider.getClient()).thenReturn(mockClient);

    // then
    final QueryApi queryApi = new QueryApiImpl(mockClientProvider);
    final ChainInfo chainInfo = queryApi.getChainInfo();
    assertNotNull(chainInfo);
  }

  @Test
  public void testGetChainStats() {
    // given
    final BlockchainOperation mockOperation = mock(BlockchainOperation.class);
    when(mockOperation.getChainStats())
        .thenReturn(ChainStats.newBuilder().build());
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getBlockchainOperation()).thenReturn(mockOperation);
    final ClientProvider mockClientProvider = mock(ClientProvider.class);
    when(mockClientProvider.getClient()).thenReturn(mockClient);

    // then
    final QueryApi queryApi = new QueryApiImpl(mockClientProvider);
    final ChainStats chainStats = queryApi.getChainStats();
    assertNotNull(chainStats);
  }

  @Test
  public void testListPeers() {
    // given
    final BlockchainOperation mockOperation = mock(BlockchainOperation.class);
    when(mockOperation.listPeers())
        .thenReturn(Collections.<Peer>emptyList());
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getBlockchainOperation()).thenReturn(mockOperation);
    final ClientProvider mockClientProvider = mock(ClientProvider.class);
    when(mockClientProvider.getClient()).thenReturn(mockClient);

    // then
    final QueryApi queryApi = new QueryApiImpl(mockClientProvider);
    final List<Peer> peers = queryApi.listPeers();
    assertNotNull(peers);
  }

  @Test
  public void testListPeersWithFlag() {
    // given
    final BlockchainOperation mockOperation = mock(BlockchainOperation.class);
    when(mockOperation.listPeers(anyBoolean(), anyBoolean()))
        .thenReturn(Collections.<Peer>emptyList());
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getBlockchainOperation()).thenReturn(mockOperation);
    final ClientProvider mockClientProvider = mock(ClientProvider.class);
    when(mockClientProvider.getClient()).thenReturn(mockClient);

    // then
    final QueryApi queryApi = new QueryApiImpl(mockClientProvider);
    final List<Peer> peers = queryApi.listPeers(false, false);
    assertNotNull(peers);
  }

  @Test
  public void testListPeerMetrics() {
    // given
    final BlockchainOperation mockOperation = mock(BlockchainOperation.class);
    when(mockOperation.listPeerMetrics())
        .thenReturn(Collections.<PeerMetric>emptyList());
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getBlockchainOperation()).thenReturn(mockOperation);
    final ClientProvider mockClientProvider = mock(ClientProvider.class);
    when(mockClientProvider.getClient()).thenReturn(mockClient);

    // then
    final QueryApi queryApi = new QueryApiImpl(mockClientProvider);
    final List<PeerMetric> peerMetrics = queryApi.listPeerMetrics();
    assertNotNull(peerMetrics);
  }

  @Test
  public void testGetServerInfo() {
    // given
    final BlockchainOperation mockOperation = mock(BlockchainOperation.class);
    when(mockOperation.getServerInfo(ArgumentMatchers.<String>anyList()))
        .thenReturn(ServerInfo.newBuilder().build());
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getBlockchainOperation()).thenReturn(mockOperation);
    final ClientProvider mockClientProvider = mock(ClientProvider.class);
    when(mockClientProvider.getClient()).thenReturn(mockClient);

    // then
    final QueryApi queryApi = new QueryApiImpl(mockClientProvider);
    final ServerInfo serverInfo = queryApi.getServerInfo(Collections.<String>emptyList());
    assertNotNull(serverInfo);
  }

  @Test
  public void testGetNodeStatus() {
    // given
    final BlockchainOperation mockOperation = mock(BlockchainOperation.class);
    when(mockOperation.getNodeStatus())
        .thenReturn(NodeStatus.newBuilder().build());
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getBlockchainOperation()).thenReturn(mockOperation);
    final ClientProvider mockClientProvider = mock(ClientProvider.class);
    when(mockClientProvider.getClient()).thenReturn(mockClient);

    // then
    final QueryApi queryApi = new QueryApiImpl(mockClientProvider);
    final NodeStatus nodeStatus = queryApi.getNodeStatus();
    assertNotNull(nodeStatus);
  }

  @Test
  public void testBlockMetadataByHash() {
    // given
    final BlockOperation mockOperation = mock(BlockOperation.class);
    when(mockOperation.getBlockMetadata(any(BlockHash.class)))
        .thenReturn(BlockMetadata.newBuilder().build());
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getBlockOperation()).thenReturn(mockOperation);
    final ClientProvider mockClientProvider = mock(ClientProvider.class);
    when(mockClientProvider.getClient()).thenReturn(mockClient);

    // then
    final QueryApi queryApi = new QueryApiImpl(mockClientProvider);
    final BlockMetadata blockMetadata = queryApi.getBlockMetadata(anyBlockHash);
    assertNotNull(blockMetadata);
  }

  @Test
  public void testBlockMetadataByHeight() {
    // given
    final BlockOperation mockOperation = mock(BlockOperation.class);
    when(mockOperation.getBlockMetadata(anyLong()))
        .thenReturn(BlockMetadata.newBuilder().build());
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getBlockOperation()).thenReturn(mockOperation);
    final ClientProvider mockClientProvider = mock(ClientProvider.class);
    when(mockClientProvider.getClient()).thenReturn(mockClient);

    // then
    final QueryApi queryApi = new QueryApiImpl(mockClientProvider);
    final BlockMetadata blockMetadata = queryApi.getBlockMetadata(anyHeight);
    assertNotNull(blockMetadata);
  }

  @Test
  public void testListBlockMetadataByHash() {
    // given
    final BlockOperation mockOperation = mock(BlockOperation.class);
    when(mockOperation.listBlockMetadatas(any(BlockHash.class), anyInt()))
        .thenReturn(Collections.<BlockMetadata>emptyList());
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getBlockOperation()).thenReturn(mockOperation);
    final ClientProvider mockClientProvider = mock(ClientProvider.class);
    when(mockClientProvider.getClient()).thenReturn(mockClient);

    // then
    final QueryApi queryApi = new QueryApiImpl(mockClientProvider);
    final List<BlockMetadata> blockMetadatas = queryApi.listBlockMetadatas(anyBlockHash, anySize);
    assertNotNull(blockMetadatas);
  }

  @Test
  public void testListBlockMetadataByHeight() {
    // given
    final BlockOperation mockOperation = mock(BlockOperation.class);
    when(mockOperation.listBlockMetadatas(anyLong(), anyInt()))
        .thenReturn(Collections.<BlockMetadata>emptyList());
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getBlockOperation()).thenReturn(mockOperation);
    final ClientProvider mockClientProvider = mock(ClientProvider.class);
    when(mockClientProvider.getClient()).thenReturn(mockClient);

    // then
    final QueryApi queryApi = new QueryApiImpl(mockClientProvider);
    final List<BlockMetadata> blockMetadatas = queryApi.listBlockMetadatas(anyHeight, anySize);
    assertNotNull(blockMetadatas);
  }

  @Test
  public void testBlockByHash() {
    // given
    final BlockOperation mockOperation = mock(BlockOperation.class);
    when(mockOperation.getBlock(any(BlockHash.class)))
        .thenReturn(Block.newBuilder().build());
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getBlockOperation()).thenReturn(mockOperation);
    final ClientProvider mockClientProvider = mock(ClientProvider.class);
    when(mockClientProvider.getClient()).thenReturn(mockClient);

    // then
    final QueryApi queryApi = new QueryApiImpl(mockClientProvider);
    final Block block = queryApi.getBlock(anyBlockHash);
    assertNotNull(block);
  }

  @Test
  public void testBlockByHeight() {
    // given
    final BlockOperation mockOperation = mock(BlockOperation.class);
    when(mockOperation.getBlock(anyLong()))
        .thenReturn(Block.newBuilder().build());
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getBlockOperation()).thenReturn(mockOperation);
    final ClientProvider mockClientProvider = mock(ClientProvider.class);
    when(mockClientProvider.getClient()).thenReturn(mockClient);

    // then
    final QueryApi queryApi = new QueryApiImpl(mockClientProvider);
    final Block block = queryApi.getBlock(anyHeight);
    assertNotNull(block);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testSubscribeBlockMetadata() {
    // given
    final BlockOperation mockOperation = mock(BlockOperation.class);
    when(mockOperation.subscribeBlockMetadata(any(StreamObserver.class)))
        .thenReturn(mock(Subscription.class));
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getBlockOperation()).thenReturn(mockOperation);
    final ClientProvider mockClientProvider = mock(ClientProvider.class);
    when(mockClientProvider.getClient()).thenReturn(mockClient);

    // then
    final QueryApi queryApi = new QueryApiImpl(mockClientProvider);
    final StreamObserver<BlockMetadata> observer = new StreamObserver<BlockMetadata>() {
      @Override
      public void onNext(BlockMetadata value) {

      }

      @Override
      public void onError(Throwable t) {

      }

      @Override
      public void onCompleted() {

      }
    };
    assertNotNull(queryApi.subscribeNewBlockMetadata(observer));
    assertNotNull(queryApi.subscribeBlockMetadata(observer));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testSubscribeBlock() {
    // given
    final BlockOperation mockOperation = mock(BlockOperation.class);
    when(mockOperation.subscribeBlock(any(StreamObserver.class)))
        .thenReturn(mock(Subscription.class));
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getBlockOperation()).thenReturn(mockOperation);
    final ClientProvider mockClientProvider = mock(ClientProvider.class);
    when(mockClientProvider.getClient()).thenReturn(mockClient);

    // then
    final QueryApi queryApi = new QueryApiImpl(mockClientProvider);
    final StreamObserver<Block> observer = new StreamObserver<Block>() {
      @Override
      public void onNext(Block value) {

      }

      @Override
      public void onError(Throwable t) {

      }

      @Override
      public void onCompleted() {

      }
    };
    assertNotNull(queryApi.subscribeNewBlock(observer));
    assertNotNull(queryApi.subscribeBlock(observer));
  }

  @Test
  public void testGetTransaction() {
    // given
    final TransactionOperation mockOperation = mock(TransactionOperation.class);
    when(mockOperation.getTransaction(any(TxHash.class)))
        .thenReturn(anyTransaction);
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getTransactionOperation()).thenReturn(mockOperation);
    final ClientProvider mockClientProvider = mock(ClientProvider.class);
    when(mockClientProvider.getClient()).thenReturn(mockClient);

    // then
    final QueryApi queryApi = new QueryApiImpl(mockClientProvider);
    final Transaction transaction = queryApi.getTransaction(anyTxHash);
    assertNotNull(transaction);
  }

  @Test
  public void testGetTxReceipt() {
    // given
    final TransactionOperation mockOperation = mock(TransactionOperation.class);
    when(mockOperation.getTxReceipt(any(TxHash.class)))
        .thenReturn(TxReceipt.newBuilder().build());
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getTransactionOperation()).thenReturn(mockOperation);
    final ClientProvider mockClientProvider = mock(ClientProvider.class);
    when(mockClientProvider.getClient()).thenReturn(mockClient);

    // then
    final QueryApi queryApi = new QueryApiImpl(mockClientProvider);
    final TxReceipt txReceipt = queryApi.getTxReceipt(anyTxHash);
    assertNotNull(txReceipt);
  }

  @Test
  public void testGetContractTxReceipt() {
    // given
    final ContractOperation mockOperation = mock(ContractOperation.class);
    when(mockOperation.getContractTxReceipt(any(ContractTxHash.class)))
        .thenReturn(ContractTxReceipt.newBuilder().build());
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getContractOperation()).thenReturn(mockOperation);
    final ClientProvider mockClientProvider = mock(ClientProvider.class);
    when(mockClientProvider.getClient()).thenReturn(mockClient);

    // then
    final QueryApi queryApi = new QueryApiImpl(mockClientProvider);
    final ContractTxReceipt contractTxReceipt = queryApi.getContractTxReceipt(anyContractTxHash);
    assertNotNull(contractTxReceipt);
  }

  @Test
  public void testGetContractInterface() {
    // given
    final ContractOperation mockOperation = mock(ContractOperation.class);
    when(mockOperation.getContractInterface(any(ContractAddress.class)))
        .thenReturn(ContractInterface.newBuilder().build());
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getContractOperation()).thenReturn(mockOperation);
    final ClientProvider mockClientProvider = mock(ClientProvider.class);
    when(mockClientProvider.getClient()).thenReturn(mockClient);

    // then
    final QueryApi queryApi = new QueryApiImpl(mockClientProvider);
    final ContractInterface contractInterface = queryApi.getContractInterface(anyContractAddress);
    assertNotNull(contractInterface);
  }

  @Test
  public void testQuery() {
    // given
    final ContractOperation mockOperation = mock(ContractOperation.class);
    when(mockOperation.query(any(ContractInvocation.class)))
        .thenReturn(ContractResult.EMPTY);
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getContractOperation()).thenReturn(mockOperation);
    final ClientProvider mockClientProvider = mock(ClientProvider.class);
    when(mockClientProvider.getClient()).thenReturn(mockClient);

    // then
    final QueryApi queryApi = new QueryApiImpl(mockClientProvider);
    final ContractResult contractResult = queryApi.query(anyInvocation);
    assertNotNull(contractResult);
  }

  @Test
  public void testListEvents() {
    // given
    final ContractOperation mockOperation = mock(ContractOperation.class);
    when(mockOperation.listEvents(any(EventFilter.class)))
        .thenReturn(Collections.<Event>emptyList());
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getContractOperation()).thenReturn(mockOperation);
    final ClientProvider mockClientProvider = mock(ClientProvider.class);
    when(mockClientProvider.getClient()).thenReturn(mockClient);

    // then
    final QueryApi queryApi = new QueryApiImpl(mockClientProvider);
    final List<Event> events = queryApi.listEvents(anyEventFilter);
    assertNotNull(events);
  }

  @Test
  public void testSubscribeEvent() {
    // given
    final ContractOperation mockOperation = mock(ContractOperation.class);
    when(mockOperation.subscribeEvent(any(EventFilter.class), any(StreamObserver.class)))
        .thenReturn(mock(Subscription.class));
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getContractOperation()).thenReturn(mockOperation);
    final ClientProvider mockClientProvider = mock(ClientProvider.class);
    when(mockClientProvider.getClient()).thenReturn(mockClient);

    // then
    final QueryApi queryApi = new QueryApiImpl(mockClientProvider);
    final Subscription<Event> subscription = queryApi
        .subscribeEvent(anyEventFilter, new StreamObserver<Event>() {
          @Override
          public void onNext(Event value) {

          }

          @Override
          public void onError(Throwable t) {

          }

          @Override
          public void onCompleted() {

          }
        });
    assertNotNull(subscription);
  }

}
