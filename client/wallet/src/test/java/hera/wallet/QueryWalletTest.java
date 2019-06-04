/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

import hera.AbstractTestCase;
import hera.api.AccountOperation;
import hera.api.BlockOperation;
import hera.api.BlockchainOperation;
import hera.api.ContractOperation;
import hera.api.KeyStoreOperation;
import hera.api.TransactionOperation;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountFactory;
import hera.api.model.AccountState;
import hera.api.model.AccountTotalVote;
import hera.api.model.Block;
import hera.api.model.BlockHash;
import hera.api.model.BlockMetadata;
import hera.api.model.BlockchainStatus;
import hera.api.model.BytesValue;
import hera.api.model.ChainInfo;
import hera.api.model.ContractAddress;
import hera.api.model.ContractInterface;
import hera.api.model.ContractInvocation;
import hera.api.model.ContractResult;
import hera.api.model.ContractTxHash;
import hera.api.model.ContractTxReceipt;
import hera.api.model.ElectedCandidate;
import hera.api.model.Event;
import hera.api.model.EventFilter;
import hera.api.model.ModuleStatus;
import hera.api.model.NodeStatus;
import hera.api.model.Peer;
import hera.api.model.PeerMetric;
import hera.api.model.ServerInfo;
import hera.api.model.StakeInfo;
import hera.api.model.StreamObserver;
import hera.api.model.Subscription;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.client.AergoClient;
import hera.key.AergoKeyGenerator;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class QueryWalletTest extends AbstractTestCase {

  private final ContractAddress contractAddress =
      ContractAddress.of("AmLo9CGR3xFZPVKZ5moSVRNW1kyscY9rVkCvgrpwNJjRUPUWadC5");

  @Test
  public void testGetAccountState() {
    final AergoClient mockClient = mock(AergoClient.class);
    final AccountOperation mockOperation = mock(AccountOperation.class);
    when(mockOperation.getState(any(AccountAddress.class))).thenReturn(mock(AccountState.class));
    when(mockClient.getAccountOperation()).thenReturn(mockOperation);

    final QueryWallet wallet = mock(QueryWallet.class,
        withSettings().useConstructor(mockClient).defaultAnswer(CALLS_REAL_METHODS));

    final Account account = new AccountFactory().create(new AergoKeyGenerator().create());
    assertNotNull(wallet.getAccountState(account));
  }

  @Test
  public void testGetNameOwner() {
    final AergoClient mockClient = mock(AergoClient.class);
    final AccountOperation mockOperation = mock(AccountOperation.class);
    when(mockOperation.getNameOwner(anyString())).thenReturn(mock(AccountAddress.class));
    when(mockClient.getAccountOperation()).thenReturn(mockOperation);

    final QueryWallet wallet = mock(QueryWallet.class,
        withSettings().useConstructor(mockClient).defaultAnswer(CALLS_REAL_METHODS));

    assertNotNull(wallet.getNameOwner((randomUUID().toString())));
  }

  @Test
  public void testGetStakingInfo() {
    final AergoClient mockClient = mock(AergoClient.class);
    final AccountOperation mockOperation = mock(AccountOperation.class);
    when(mockOperation.getStakingInfo(any(AccountAddress.class)))
        .thenReturn(mock(StakeInfo.class));
    when(mockClient.getAccountOperation()).thenReturn(mockOperation);

    final QueryWallet wallet = mock(QueryWallet.class,
        withSettings().useConstructor(mockClient).defaultAnswer(CALLS_REAL_METHODS));

    final Account account = new AccountFactory().create(new AergoKeyGenerator().create());
    assertNotNull(wallet.getStakingInfo(account));
  }

  @Test
  public void testListElected() {
    final AergoClient mockClient = mock(AergoClient.class);
    final BlockchainOperation mockOperation = mock(BlockchainOperation.class);
    when(mockOperation.listElected(anyString(), anyInt()))
        .thenReturn(new ArrayList<ElectedCandidate>());
    when(mockClient.getBlockchainOperation()).thenReturn(mockOperation);

    final QueryWallet wallet = mock(QueryWallet.class,
        withSettings().useConstructor(mockClient).defaultAnswer(CALLS_REAL_METHODS));

    assertNotNull(wallet.listElected(randomUUID().toString(), randomUUID().toString().hashCode()));
  }

  @Test
  public void testGetVotesOf() {
    final AergoClient mockClient = mock(AergoClient.class);
    final BlockchainOperation mockOperation = mock(BlockchainOperation.class);
    when(mockOperation.getVotesOf(any(AccountAddress.class)))
        .thenReturn(mock(AccountTotalVote.class));
    when(mockClient.getBlockchainOperation()).thenReturn(mockOperation);

    final QueryWallet wallet = mock(QueryWallet.class,
        withSettings().useConstructor(mockClient).defaultAnswer(CALLS_REAL_METHODS));

    final Account account = new AccountFactory().create(new AergoKeyGenerator().create());
    assertNotNull(wallet.getVotesOf(account));
  }

  @Test
  public void testListServerKeyStoreAccounts() {
    final AergoClient mockClient = mock(AergoClient.class);
    final KeyStoreOperation mockOperation = mock(KeyStoreOperation.class);
    when(mockOperation.list()).thenReturn(new ArrayList<AccountAddress>());
    when(mockClient.getKeyStoreOperation()).thenReturn(mockOperation);

    final QueryWallet wallet = mock(QueryWallet.class,
        withSettings().useConstructor(mockClient).defaultAnswer(CALLS_REAL_METHODS));

    assertNotNull(wallet.listServerKeyStoreAccounts());
  }

  @Test
  public void testGetBestBlockHash() {
    final AergoClient mockClient = mock(AergoClient.class);
    final BlockchainOperation mockOperation = mock(BlockchainOperation.class);
    when(mockOperation.getBlockchainStatus()).thenReturn(
        new BlockchainStatus(1, BlockHash.of(BytesValue.EMPTY), randomUUID().toString(),
            chainIdHash));
    when(mockClient.getBlockchainOperation()).thenReturn(mockOperation);

    final QueryWallet wallet = mock(QueryWallet.class,
        withSettings().useConstructor(mockClient).defaultAnswer(CALLS_REAL_METHODS));

    assertNotNull(wallet.getBestBlockHash());
  }

  @Test
  public void testGetBestBlockHeight() {
    final AergoClient mockClient = mock(AergoClient.class);
    final BlockchainOperation mockOperation = mock(BlockchainOperation.class);
    when(mockOperation.getBlockchainStatus()).thenReturn(
        new BlockchainStatus(1, BlockHash.of(BytesValue.EMPTY), randomUUID().toString(),
            chainIdHash));
    when(mockClient.getBlockchainOperation()).thenReturn(mockOperation);

    final QueryWallet wallet = mock(QueryWallet.class,
        withSettings().useConstructor(mockClient).defaultAnswer(CALLS_REAL_METHODS));

    assertNotNull(wallet.getBestBlockHeight());
  }

  @Test
  public void testGetChainIdHash() {
    final AergoClient mockClient = mock(AergoClient.class);
    final BlockchainOperation mockOperation = mock(BlockchainOperation.class);
    when(mockOperation.getBlockchainStatus()).thenReturn(
        new BlockchainStatus(1, BlockHash.of(BytesValue.EMPTY), randomUUID().toString(),
            chainIdHash));
    when(mockClient.getBlockchainOperation()).thenReturn(mockOperation);

    final QueryWallet wallet = mock(QueryWallet.class,
        withSettings().useConstructor(mockClient).defaultAnswer(CALLS_REAL_METHODS));

    assertNotNull(wallet.getChainIdHash());
  }

  @Test
  public void testGetBlockchainStatus() {
    final AergoClient mockClient = mock(AergoClient.class);
    final BlockchainOperation mockOperation = mock(BlockchainOperation.class);
    when(mockOperation.getBlockchainStatus()).thenReturn(
        new BlockchainStatus(1, BlockHash.of(BytesValue.EMPTY), randomUUID().toString(),
            chainIdHash));
    when(mockClient.getBlockchainOperation()).thenReturn(mockOperation);

    final QueryWallet wallet = mock(QueryWallet.class,
        withSettings().useConstructor(mockClient).defaultAnswer(CALLS_REAL_METHODS));

    assertNotNull(wallet.getBlockchainStatus());
  }

  @Test
  public void testGetChainInfo() {
    final AergoClient mockClient = mock(AergoClient.class);
    final BlockchainOperation mockOperation = mock(BlockchainOperation.class);
    when(mockOperation.getChainInfo()).thenReturn(mock(ChainInfo.class));
    when(mockClient.getBlockchainOperation()).thenReturn(mockOperation);

    final QueryWallet wallet = mock(QueryWallet.class,
        withSettings().useConstructor(mockClient).defaultAnswer(CALLS_REAL_METHODS));

    assertNotNull(wallet.getChainInfo());
  }

  @Test
  public void testListNodePeers() {
    final AergoClient mockClient = mock(AergoClient.class);
    final BlockchainOperation mockOperation = mock(BlockchainOperation.class);
    when(mockOperation.listPeers()).thenReturn(new ArrayList<Peer>());
    when(mockClient.getBlockchainOperation()).thenReturn(mockOperation);

    final QueryWallet wallet = mock(QueryWallet.class,
        withSettings().useConstructor(mockClient).defaultAnswer(CALLS_REAL_METHODS));

    assertNotNull(wallet.listNodePeers());
  }

  @Test
  public void testListPeerMetrics() {
    final AergoClient mockClient = mock(AergoClient.class);
    final BlockchainOperation mockOperation = mock(BlockchainOperation.class);
    when(mockOperation.listPeerMetrics()).thenReturn(new ArrayList<PeerMetric>());
    when(mockClient.getBlockchainOperation()).thenReturn(mockOperation);

    final QueryWallet wallet = mock(QueryWallet.class,
        withSettings().useConstructor(mockClient).defaultAnswer(CALLS_REAL_METHODS));

    assertNotNull(wallet.listPeerMetrics());
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testGetServerInfo() {
    final AergoClient mockClient = mock(AergoClient.class);
    final BlockchainOperation mockOperation = mock(BlockchainOperation.class);
    when(mockOperation.getServerInfo(any(List.class))).thenReturn(mock(ServerInfo.class));
    when(mockClient.getBlockchainOperation()).thenReturn(mockOperation);

    final QueryWallet wallet = mock(QueryWallet.class,
        withSettings().useConstructor(mockClient).defaultAnswer(CALLS_REAL_METHODS));

    assertNotNull(wallet.getServerInfo(new ArrayList<String>()));
  }

  @Test
  public void testGetNodeStatus() {
    final AergoClient mockClient = mock(AergoClient.class);
    final BlockchainOperation mockOperation = mock(BlockchainOperation.class);
    when(mockOperation.getNodeStatus()).thenReturn(new NodeStatus(new ArrayList<ModuleStatus>()));
    when(mockClient.getBlockchainOperation()).thenReturn(mockOperation);

    final QueryWallet wallet = mock(QueryWallet.class,
        withSettings().useConstructor(mockClient).defaultAnswer(CALLS_REAL_METHODS));

    assertNotNull(wallet.getNodeStatus());
  }

  @Test
  public void testGetBlockMetadataByHash() {
    final AergoClient mockClient = mock(AergoClient.class);
    final BlockOperation mockOperation = mock(BlockOperation.class);
    when(mockOperation.getBlockMetadata(any(BlockHash.class)))
        .thenReturn(mock(BlockMetadata.class));
    when(mockClient.getBlockOperation()).thenReturn(mockOperation);

    final QueryWallet wallet = mock(QueryWallet.class,
        withSettings().useConstructor(mockClient).defaultAnswer(CALLS_REAL_METHODS));

    assertNotNull(wallet.getBlockMetadata(BlockHash.of(BytesValue.EMPTY)));
  }

  @Test
  public void testGetBlockMetadataByHeight() {
    final AergoClient mockClient = mock(AergoClient.class);
    final BlockOperation mockOperation = mock(BlockOperation.class);
    when(mockOperation.getBlockMetadata(any(Long.class))).thenReturn(mock(BlockMetadata.class));
    when(mockClient.getBlockOperation()).thenReturn(mockOperation);

    final QueryWallet wallet = mock(QueryWallet.class,
        withSettings().useConstructor(mockClient).defaultAnswer(CALLS_REAL_METHODS));

    assertNotNull(wallet.getBlockMetadata(1L));
  }

  @Test
  public void testListBlockMetadatasByHash() {
    final AergoClient mockClient = mock(AergoClient.class);
    final BlockOperation mockOperation = mock(BlockOperation.class);
    when(mockOperation.listBlockMetadatas(any(BlockHash.class), anyInt()))
        .thenReturn(new ArrayList<BlockMetadata>());
    when(mockClient.getBlockOperation()).thenReturn(mockOperation);

    final QueryWallet wallet = mock(QueryWallet.class,
        withSettings().useConstructor(mockClient).defaultAnswer(CALLS_REAL_METHODS));

    assertNotNull(wallet.listBlockMetadatas(BlockHash.of(BytesValue.EMPTY), 1));
  }

  @Test
  public void testListBlockMetadatasByHeight() {
    final AergoClient mockClient = mock(AergoClient.class);
    final BlockOperation mockOperation = mock(BlockOperation.class);
    when(mockOperation.listBlockMetadatas(anyLong(), anyInt()))
        .thenReturn(new ArrayList<BlockMetadata>());
    when(mockClient.getBlockOperation()).thenReturn(mockOperation);

    final QueryWallet wallet = mock(QueryWallet.class,
        withSettings().useConstructor(mockClient).defaultAnswer(CALLS_REAL_METHODS));

    assertNotNull(wallet.listBlockMetadatas(1L, 1));
  }

  @Test
  public void testGetBlockByHash() {
    final AergoClient mockClient = mock(AergoClient.class);
    final BlockOperation mockOperation = mock(BlockOperation.class);
    when(mockOperation.getBlock(any(BlockHash.class))).thenReturn(mock(Block.class));
    when(mockClient.getBlockOperation()).thenReturn(mockOperation);

    final QueryWallet wallet = mock(QueryWallet.class,
        withSettings().useConstructor(mockClient).defaultAnswer(CALLS_REAL_METHODS));

    assertNotNull(wallet.getBlock(BlockHash.of(BytesValue.EMPTY)));
  }

  @Test
  public void testGetBlockByHeight() {
    final AergoClient mockClient = mock(AergoClient.class);
    final BlockOperation mockOperation = mock(BlockOperation.class);
    when(mockOperation.getBlock(any(Long.class))).thenReturn(mock(Block.class));
    when(mockClient.getBlockOperation()).thenReturn(mockOperation);

    final QueryWallet wallet = mock(QueryWallet.class,
        withSettings().useConstructor(mockClient).defaultAnswer(CALLS_REAL_METHODS));

    assertNotNull(wallet.getBlock(1L));
  }

  @Test
  public void testGetTransaction() {
    final AergoClient mockClient = mock(AergoClient.class);
    final TransactionOperation mockOperation = mock(TransactionOperation.class);
    when(mockOperation.getTransaction(any(TxHash.class))).thenReturn(mock(Transaction.class));
    when(mockClient.getTransactionOperation()).thenReturn(mockOperation);

    final QueryWallet wallet = mock(QueryWallet.class,
        withSettings().useConstructor(mockClient).defaultAnswer(CALLS_REAL_METHODS));

    assertNotNull(wallet.getTransaction(TxHash.of(BytesValue.EMPTY)));
  }

  @Test
  public void testGetReceipt() {
    final AergoClient mockClient = mock(AergoClient.class);
    final ContractOperation mockOperation = mock(ContractOperation.class);
    when(mockOperation.getReceipt(any(ContractTxHash.class)))
        .thenReturn(mock(ContractTxReceipt.class));
    when(mockClient.getContractOperation()).thenReturn(mockOperation);

    final QueryWallet wallet = mock(QueryWallet.class,
        withSettings().useConstructor(mockClient).defaultAnswer(CALLS_REAL_METHODS));

    assertNotNull(wallet.getReceipt(ContractTxHash.of(BytesValue.EMPTY)));
  }

  @Test
  public void testGetContractInterface() {
    final AergoClient mockClient = mock(AergoClient.class);
    final ContractOperation mockOperation = mock(ContractOperation.class);
    when(mockOperation.getContractInterface(any(ContractAddress.class)))
        .thenReturn(mock(ContractInterface.class));
    when(mockClient.getContractOperation()).thenReturn(mockOperation);

    final QueryWallet wallet = mock(QueryWallet.class,
        withSettings().useConstructor(mockClient).defaultAnswer(CALLS_REAL_METHODS));

    assertNotNull(wallet.getContractInterface(ContractAddress.of(BytesValue.EMPTY)));
  }

  @Test
  public void testQuery() {
    final AergoClient mockClient = mock(AergoClient.class);
    final ContractOperation mockOperation = mock(ContractOperation.class);
    when(mockOperation.query(any(ContractInvocation.class))).thenReturn(mock(ContractResult.class));
    when(mockClient.getContractOperation()).thenReturn(mockOperation);

    final QueryWallet wallet = mock(QueryWallet.class,
        withSettings().useConstructor(mockClient).defaultAnswer(CALLS_REAL_METHODS));

    assertNotNull(wallet.query(mock(ContractInvocation.class)));
  }

  @Test
  public void testListEvents() {
    final AergoClient mockClient = mock(AergoClient.class);
    final ContractOperation mockOperation = mock(ContractOperation.class);
    when(mockOperation.listEvents(any(EventFilter.class))).thenReturn(new ArrayList<Event>());
    when(mockClient.getContractOperation()).thenReturn(mockOperation);

    final QueryWallet wallet = mock(QueryWallet.class,
        withSettings().useConstructor(mockClient).defaultAnswer(CALLS_REAL_METHODS));

    final EventFilter eventFilter = EventFilter.newBuilder(contractAddress).build();
    assertNotNull(wallet.listEvents(eventFilter));
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testSubscribeEvent() {
    final AergoClient mockClient = mock(AergoClient.class);
    final ContractOperation mockOperation = mock(ContractOperation.class);
    when(mockOperation.subscribeEvent(any(EventFilter.class), any(StreamObserver.class)))
        .thenReturn(mock(Subscription.class));
    when(mockClient.getContractOperation()).thenReturn(mockOperation);

    final QueryWallet wallet = mock(QueryWallet.class,
        withSettings().useConstructor(mockClient).defaultAnswer(CALLS_REAL_METHODS));

    final EventFilter eventFilter = EventFilter.newBuilder(contractAddress).build();
    final StreamObserver<Event> mockObserver = mock(StreamObserver.class);
    assertNotNull(wallet.subscribeEvent(eventFilter, mockObserver));
  }

}
