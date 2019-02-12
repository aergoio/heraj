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
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

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
import hera.api.model.Block;
import hera.api.model.BlockHash;
import hera.api.model.BlockHeader;
import hera.api.model.BlockProducer;
import hera.api.model.BlockchainStatus;
import hera.api.model.BytesValue;
import hera.api.model.ChainInfo;
import hera.api.model.ContractAddress;
import hera.api.model.ContractFunction;
import hera.api.model.ContractInterface;
import hera.api.model.ContractInvocation;
import hera.api.model.ContractResult;
import hera.api.model.ContractTxHash;
import hera.api.model.ContractTxReceipt;
import hera.api.model.ModuleStatus;
import hera.api.model.NodeStatus;
import hera.api.model.Peer;
import hera.api.model.PeerMetric;
import hera.api.model.StakingInfo;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.api.model.VotingInfo;
import hera.client.AergoClient;
import hera.key.AergoKeyGenerator;
import java.util.ArrayList;
import org.junit.Test;
import org.mockito.Mockito;

public class QueryWalletTest extends AbstractTestCase {

  private final ContractAddress contractAddress =
      ContractAddress.of("AmLo9CGR3xFZPVKZ5moSVRNW1kyscY9rVkCvgrpwNJjRUPUWadC5");

  @Test
  public void testGetAccountState() {
    final QueryWallet wallet = mock(QueryWallet.class, Mockito.CALLS_REAL_METHODS);
    final AergoClient mockClient = mock(AergoClient.class);
    final AccountOperation mockOperation = mock(AccountOperation.class);
    when(mockOperation.getState(any(Account.class))).thenReturn(mock(AccountState.class));
    when(mockClient.getAccountOperation()).thenReturn(mockOperation);
    when(wallet.getAergoClient()).thenReturn(mockClient);

    assertNotNull(wallet.getAccountState(mock(Account.class)));
  }

  @Test
  public void testGetNameOwner() {
    final QueryWallet wallet = mock(QueryWallet.class, Mockito.CALLS_REAL_METHODS);
    final AergoClient mockClient = mock(AergoClient.class);
    final AccountOperation mockOperation = mock(AccountOperation.class);
    when(mockOperation.getNameOwner(anyString())).thenReturn(mock(AccountAddress.class));
    when(mockClient.getAccountOperation()).thenReturn(mockOperation);
    when(wallet.getAergoClient()).thenReturn(mockClient);

    assertNotNull(wallet.getNameOwner((randomUUID().toString())));
  }

  @Test
  public void testGetStakingInfo() {
    final QueryWallet wallet = mock(QueryWallet.class, Mockito.CALLS_REAL_METHODS);
    final AergoClient mockClient = mock(AergoClient.class);
    final AccountOperation mockOperation = mock(AccountOperation.class);
    when(mockOperation.getStakingInfo(any(AccountAddress.class)))
        .thenReturn(mock(StakingInfo.class));
    when(mockClient.getAccountOperation()).thenReturn(mockOperation);
    when(wallet.getAergoClient()).thenReturn(mockClient);

    final Account account = new AccountFactory().create(new AergoKeyGenerator().create());
    assertNotNull(wallet.getStakingInfo(account));
  }

  @Test
  public void testListElectedBlockProducers() {
    final QueryWallet wallet = mock(QueryWallet.class, Mockito.CALLS_REAL_METHODS);
    final AergoClient mockClient = mock(AergoClient.class);
    final BlockchainOperation mockOperation = mock(BlockchainOperation.class);
    when(mockOperation.listElectedBlockProducers(anyLong()))
        .thenReturn(new ArrayList<BlockProducer>());
    when(mockClient.getBlockchainOperation()).thenReturn(mockOperation);
    when(wallet.getAergoClient()).thenReturn(mockClient);

    assertNotNull(wallet.listElectedBlockProducers());
  }

  @Test
  public void testListVotesOf() {
    final QueryWallet wallet = mock(QueryWallet.class, Mockito.CALLS_REAL_METHODS);
    final AergoClient mockClient = mock(AergoClient.class);
    final BlockchainOperation mockOperation = mock(BlockchainOperation.class);
    when(mockOperation.listVotesOf(any(AccountAddress.class)))
        .thenReturn(new ArrayList<VotingInfo>());
    when(mockClient.getBlockchainOperation()).thenReturn(mockOperation);
    when(wallet.getAergoClient()).thenReturn(mockClient);

    final Account account = new AccountFactory().create(new AergoKeyGenerator().create());
    assertNotNull(wallet.listVotesOf(account));
  }

  @Test
  public void testListServerKeyStoreAccounts() {
    final QueryWallet wallet = mock(QueryWallet.class, Mockito.CALLS_REAL_METHODS);
    final AergoClient mockClient = mock(AergoClient.class);
    final KeyStoreOperation mockOperation = mock(KeyStoreOperation.class);
    when(mockOperation.list()).thenReturn(new ArrayList<AccountAddress>());
    when(mockClient.getKeyStoreOperation()).thenReturn(mockOperation);
    when(wallet.getAergoClient()).thenReturn(mockClient);

    assertNotNull(wallet.listServerKeyStoreAccounts());
  }

  @Test
  public void testGetBestBlockHash() {
    final QueryWallet wallet = mock(QueryWallet.class, Mockito.CALLS_REAL_METHODS);
    final AergoClient mockClient = mock(AergoClient.class);
    final BlockchainOperation mockOperation = mock(BlockchainOperation.class);
    when(mockOperation.getBlockchainStatus()).thenReturn(
        new BlockchainStatus(1, BlockHash.of(BytesValue.EMPTY)));
    when(mockClient.getBlockchainOperation()).thenReturn(mockOperation);
    when(wallet.getAergoClient()).thenReturn(mockClient);

    assertNotNull(wallet.getBestBlockHash());
  }

  @Test
  public void testGetBestBlockHeight() {
    final QueryWallet wallet = mock(QueryWallet.class, Mockito.CALLS_REAL_METHODS);
    final AergoClient mockClient = mock(AergoClient.class);
    final BlockchainOperation mockOperation = mock(BlockchainOperation.class);
    when(mockOperation.getBlockchainStatus()).thenReturn(
        new BlockchainStatus(1, BlockHash.of(BytesValue.EMPTY)));
    when(mockClient.getBlockchainOperation()).thenReturn(mockOperation);
    when(wallet.getAergoClient()).thenReturn(mockClient);

    assertNotNull(wallet.getBestBlockHeight());
  }

  @Test
  public void testGetChainInfo() {
    final QueryWallet wallet = mock(QueryWallet.class, Mockito.CALLS_REAL_METHODS);
    final AergoClient mockClient = mock(AergoClient.class);
    final BlockchainOperation mockOperation = mock(BlockchainOperation.class);
    when(mockOperation.getChainInfo()).thenReturn(mock(ChainInfo.class));
    when(mockClient.getBlockchainOperation()).thenReturn(mockOperation);
    when(wallet.getAergoClient()).thenReturn(mockClient);

    assertNotNull(wallet.getChainInfo());
  }

  @Test
  public void testListNodePeers() {
    final QueryWallet wallet = mock(QueryWallet.class, Mockito.CALLS_REAL_METHODS);
    final AergoClient mockClient = mock(AergoClient.class);
    final BlockchainOperation mockOperation = mock(BlockchainOperation.class);
    when(mockOperation.listPeers()).thenReturn(new ArrayList<Peer>());
    when(mockClient.getBlockchainOperation()).thenReturn(mockOperation);
    when(wallet.getAergoClient()).thenReturn(mockClient);

    assertNotNull(wallet.listNodePeers());
  }

  @Test
  public void testListPeerMetrics() {
    final QueryWallet wallet = mock(QueryWallet.class, Mockito.CALLS_REAL_METHODS);
    final AergoClient mockClient = mock(AergoClient.class);
    final BlockchainOperation mockOperation = mock(BlockchainOperation.class);
    when(mockOperation.listPeerMetrics()).thenReturn(new ArrayList<PeerMetric>());
    when(mockClient.getBlockchainOperation()).thenReturn(mockOperation);
    when(wallet.getAergoClient()).thenReturn(mockClient);

    assertNotNull(wallet.listPeerMetrics());
  }

  @Test
  public void testGetNodeStatus() {
    final QueryWallet wallet = mock(QueryWallet.class, Mockito.CALLS_REAL_METHODS);
    final AergoClient mockClient = mock(AergoClient.class);
    final BlockchainOperation mockOperation = mock(BlockchainOperation.class);
    when(mockOperation.getNodeStatus()).thenReturn(new NodeStatus(new ArrayList<ModuleStatus>()));
    when(mockClient.getBlockchainOperation()).thenReturn(mockOperation);
    when(wallet.getAergoClient()).thenReturn(mockClient);

    assertNotNull(wallet.getNodeStatus());
  }

  @Test
  public void testGetBlockHeaderByHash() {
    final QueryWallet wallet = mock(QueryWallet.class, Mockito.CALLS_REAL_METHODS);
    final AergoClient mockClient = mock(AergoClient.class);
    final BlockOperation mockOperation = mock(BlockOperation.class);
    when(mockOperation.getBlockHeader(any(BlockHash.class))).thenReturn(mock(Block.class));
    when(mockClient.getBlockOperation()).thenReturn(mockOperation);
    when(wallet.getAergoClient()).thenReturn(mockClient);

    assertNotNull(wallet.getBlockHeader(BlockHash.of(BytesValue.EMPTY)));
  }

  @Test
  public void testGetBlockHeaderByHeight() {
    final QueryWallet wallet = mock(QueryWallet.class, Mockito.CALLS_REAL_METHODS);
    final AergoClient mockClient = mock(AergoClient.class);
    final BlockOperation mockOperation = mock(BlockOperation.class);
    when(mockOperation.getBlockHeader(any(Long.class))).thenReturn(mock(Block.class));
    when(mockClient.getBlockOperation()).thenReturn(mockOperation);
    when(wallet.getAergoClient()).thenReturn(mockClient);

    assertNotNull(wallet.getBlockHeader(1L));
  }

  @Test
  public void testListBlockHeadersByHash() {
    final QueryWallet wallet = mock(QueryWallet.class, Mockito.CALLS_REAL_METHODS);
    final AergoClient mockClient = mock(AergoClient.class);
    final BlockOperation mockOperation = mock(BlockOperation.class);
    when(mockOperation.listBlockHeaders(any(BlockHash.class), anyInt()))
        .thenReturn(new ArrayList<BlockHeader>());
    when(mockClient.getBlockOperation()).thenReturn(mockOperation);
    when(wallet.getAergoClient()).thenReturn(mockClient);

    assertNotNull(wallet.listBlockHeaders(BlockHash.of(BytesValue.EMPTY), 1));
  }

  @Test
  public void testListBlockHeadersByHeight() {
    final QueryWallet wallet = mock(QueryWallet.class, Mockito.CALLS_REAL_METHODS);
    final AergoClient mockClient = mock(AergoClient.class);
    final BlockOperation mockOperation = mock(BlockOperation.class);
    when(mockOperation.listBlockHeaders(anyLong(), anyInt()))
        .thenReturn(new ArrayList<BlockHeader>());
    when(mockClient.getBlockOperation()).thenReturn(mockOperation);
    when(wallet.getAergoClient()).thenReturn(mockClient);

    assertNotNull(wallet.listBlockHeaders(1L, 1));
  }

  @Test
  public void testGetBlockByHash() {
    final QueryWallet wallet = mock(QueryWallet.class, Mockito.CALLS_REAL_METHODS);
    final AergoClient mockClient = mock(AergoClient.class);
    final BlockOperation mockOperation = mock(BlockOperation.class);
    when(mockOperation.getBlock(any(BlockHash.class))).thenReturn(mock(Block.class));
    when(mockClient.getBlockOperation()).thenReturn(mockOperation);
    when(wallet.getAergoClient()).thenReturn(mockClient);

    assertNotNull(wallet.getBlock(BlockHash.of(BytesValue.EMPTY)));
  }

  @Test
  public void testGetBlockByHeight() {
    final QueryWallet wallet = mock(QueryWallet.class, Mockito.CALLS_REAL_METHODS);
    final AergoClient mockClient = mock(AergoClient.class);
    final BlockOperation mockOperation = mock(BlockOperation.class);
    when(mockOperation.getBlock(any(Long.class))).thenReturn(mock(Block.class));
    when(mockClient.getBlockOperation()).thenReturn(mockOperation);
    when(wallet.getAergoClient()).thenReturn(mockClient);

    assertNotNull(wallet.getBlock(1L));
  }

  @Test
  public void testGetTransaction() {
    final QueryWallet wallet = mock(QueryWallet.class, Mockito.CALLS_REAL_METHODS);
    final AergoClient mockClient = mock(AergoClient.class);
    final TransactionOperation mockOperation = mock(TransactionOperation.class);
    when(mockOperation.getTransaction(any(TxHash.class))).thenReturn(mock(Transaction.class));
    when(mockClient.getTransactionOperation()).thenReturn(mockOperation);
    when(wallet.getAergoClient()).thenReturn(mockClient);

    assertNotNull(wallet.getTransaction(TxHash.of(BytesValue.EMPTY)));
  }

  @Test
  public void testGetReceipt() {
    final QueryWallet wallet = mock(QueryWallet.class, Mockito.CALLS_REAL_METHODS);
    final AergoClient mockClient = mock(AergoClient.class);
    final ContractOperation mockOperation = mock(ContractOperation.class);
    when(mockOperation.getReceipt(any(ContractTxHash.class)))
        .thenReturn(mock(ContractTxReceipt.class));
    when(mockClient.getContractOperation()).thenReturn(mockOperation);
    when(wallet.getAergoClient()).thenReturn(mockClient);

    assertNotNull(wallet.getReceipt(ContractTxHash.of(BytesValue.EMPTY)));
  }

  @Test
  public void testGetContractInterface() {
    final QueryWallet wallet = mock(QueryWallet.class, Mockito.CALLS_REAL_METHODS);
    final AergoClient mockClient = mock(AergoClient.class);
    final ContractOperation mockOperation = mock(ContractOperation.class);
    when(mockOperation.getContractInterface(any(ContractAddress.class)))
        .thenReturn(mock(ContractInterface.class));
    when(mockClient.getContractOperation()).thenReturn(mockOperation);
    when(wallet.getAergoClient()).thenReturn(mockClient);

    assertNotNull(wallet.getContractInterface(ContractAddress.of(BytesValue.EMPTY)));
  }

  @Test
  public void testQuery() {
    final QueryWallet wallet = mock(QueryWallet.class, Mockito.CALLS_REAL_METHODS);
    final AergoClient mockClient = mock(AergoClient.class);
    final ContractOperation mockOperation = mock(ContractOperation.class);
    when(mockOperation.query(any(ContractInvocation.class))).thenReturn(mock(ContractResult.class));
    when(mockClient.getContractOperation()).thenReturn(mockOperation);
    when(wallet.getAergoClient()).thenReturn(mockClient);

    assertNotNull(wallet.query(new ContractInvocation(contractAddress, new ContractFunction(""))));
  }

}
