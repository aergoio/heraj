/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyLong;
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
import hera.api.model.AccountState;
import hera.api.model.Block;
import hera.api.model.BlockHash;
import hera.api.model.BlockchainStatus;
import hera.api.model.BytesValue;
import hera.api.model.ContractAddress;
import hera.api.model.ContractInterface;
import hera.api.model.ContractTxHash;
import hera.api.model.ContractTxReceipt;
import hera.api.model.NodeStatus;
import hera.api.model.ServerManagedAccount;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.client.AergoClient;
import java.util.ArrayList;
import org.junit.Test;
import org.mockito.Mockito;

public class AbstractWalletTest extends AbstractTestCase {
  private final AccountAddress accountAddress =
      AccountAddress.of(() -> "AmLo9CGR3xFZPVKZ5moSVRNW1kyscY9rVkCvgrpwNJjRUPUWadC5");

  @Test
  public void testGetAccount() {
    final AbstractWallet wallet = mock(AbstractWallet.class, Mockito.CALLS_REAL_METHODS);
    wallet.account = mock(Account.class);
    assertNotNull(wallet.getAccount());
  }

  @Test
  public void testGetAccountOnNoAccount() {
    final AbstractWallet wallet = mock(AbstractWallet.class, Mockito.CALLS_REAL_METHODS);
    try {
      wallet.getAccount();
      fail();
    } catch (Exception e) {
      // good we expected this
    }
  }

  @Test
  public void testGetAccountState() {
    final AbstractWallet wallet = mock(AbstractWallet.class, Mockito.CALLS_REAL_METHODS);
    final AergoClient mockClient = mock(AergoClient.class);
    final AccountOperation mockOperation = mock(AccountOperation.class);
    when(mockOperation.getState(any(Account.class))).thenReturn(mock(AccountState.class));
    when(mockClient.getAccountOperation()).thenReturn(mockOperation);
    when(wallet.getAergoClient()).thenReturn(mockClient);
    wallet.account = mock(Account.class);

    assertNotNull(wallet.getAccountState());
  }

  @Test
  public void testGetAccountStateWithAccount() {
    final AbstractWallet wallet = mock(AbstractWallet.class, Mockito.CALLS_REAL_METHODS);
    final AergoClient mockClient = mock(AergoClient.class);
    final AccountOperation mockOperation = mock(AccountOperation.class);
    when(mockOperation.getState(any(Account.class))).thenReturn(mock(AccountState.class));
    when(mockClient.getAccountOperation()).thenReturn(mockOperation);
    when(wallet.getAergoClient()).thenReturn(mockClient);

    assertNotNull(wallet.getAccountState(mock(Account.class)));
  }

  @Test
  public void testGetAccountStateWithAccountAddress() {
    final AbstractWallet wallet = mock(AbstractWallet.class, Mockito.CALLS_REAL_METHODS);
    final AergoClient mockClient = mock(AergoClient.class);
    final AccountOperation mockOperation = mock(AccountOperation.class);
    when(mockOperation.getState(any(AccountAddress.class)))
        .thenReturn(mock(AccountState.class));
    when(mockClient.getAccountOperation()).thenReturn(mockOperation);
    when(wallet.getAergoClient()).thenReturn(mockClient);

    assertNotNull(wallet.getAccountState(mock(AccountAddress.class)));
  }

  @Test
  public void testListServerKeyStoreAccounts() {
    final AbstractWallet wallet = mock(AbstractWallet.class, Mockito.CALLS_REAL_METHODS);
    final AergoClient mockClient = mock(AergoClient.class);
    final KeyStoreOperation mockOperation = mock(KeyStoreOperation.class);
    when(mockOperation.list()).thenReturn(new ArrayList<>());
    when(mockClient.getKeyStoreOperation()).thenReturn(mockOperation);
    when(wallet.getAergoClient()).thenReturn(mockClient);

    assertNotNull(wallet.listServerKeyStoreAccounts());
  }

  @Test
  public void testGetBestBlockHash() {
    final AbstractWallet wallet = mock(AbstractWallet.class, Mockito.CALLS_REAL_METHODS);
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
    final AbstractWallet wallet = mock(AbstractWallet.class, Mockito.CALLS_REAL_METHODS);
    final AergoClient mockClient = mock(AergoClient.class);
    final BlockchainOperation mockOperation = mock(BlockchainOperation.class);
    when(mockOperation.getBlockchainStatus()).thenReturn(
        new BlockchainStatus(1, BlockHash.of(BytesValue.EMPTY)));
    when(mockClient.getBlockchainOperation()).thenReturn(mockOperation);
    when(wallet.getAergoClient()).thenReturn(mockClient);

    assertNotNull(wallet.getBestBlockHeight());
  }

  @Test
  public void testListNodePeers() {
    final AbstractWallet wallet = mock(AbstractWallet.class, Mockito.CALLS_REAL_METHODS);
    final AergoClient mockClient = mock(AergoClient.class);
    final BlockchainOperation mockOperation = mock(BlockchainOperation.class);
    when(mockOperation.listPeers()).thenReturn(new ArrayList<>());
    when(mockClient.getBlockchainOperation()).thenReturn(mockOperation);
    when(wallet.getAergoClient()).thenReturn(mockClient);

    assertNotNull(wallet.listNodePeers());
  }

  @Test
  public void testGetNodeStatus() {
    final AbstractWallet wallet = mock(AbstractWallet.class, Mockito.CALLS_REAL_METHODS);
    final AergoClient mockClient = mock(AergoClient.class);
    final BlockchainOperation mockOperation = mock(BlockchainOperation.class);
    when(mockOperation.getNodeStatus()).thenReturn(new NodeStatus(new ArrayList<>()));
    when(mockClient.getBlockchainOperation()).thenReturn(mockOperation);
    when(wallet.getAergoClient()).thenReturn(mockClient);

    assertNotNull(wallet.getNodeStatus());
  }

  @Test
  public void testGetBlockByHash() {
    final AbstractWallet wallet = mock(AbstractWallet.class, Mockito.CALLS_REAL_METHODS);
    final AergoClient mockClient = mock(AergoClient.class);
    final BlockOperation mockOperation = mock(BlockOperation.class);
    when(mockOperation.getBlock(any(BlockHash.class))).thenReturn(mock(Block.class));
    when(mockClient.getBlockOperation()).thenReturn(mockOperation);
    when(wallet.getAergoClient()).thenReturn(mockClient);

    assertNotNull(wallet.getBlock(BlockHash.of(BytesValue.EMPTY)));
  }

  @Test
  public void testGetBlockByHeight() {
    final AbstractWallet wallet = mock(AbstractWallet.class, Mockito.CALLS_REAL_METHODS);
    final AergoClient mockClient = mock(AergoClient.class);
    final BlockOperation mockOperation = mock(BlockOperation.class);
    when(mockOperation.getBlock(any(Long.class))).thenReturn(mock(Block.class));
    when(mockClient.getBlockOperation()).thenReturn(mockOperation);
    when(wallet.getAergoClient()).thenReturn(mockClient);

    assertNotNull(wallet.getBlock(1L));
  }

  @Test
  public void testListBlockHeadersByHash() {
    final AbstractWallet wallet = mock(AbstractWallet.class, Mockito.CALLS_REAL_METHODS);
    final AergoClient mockClient = mock(AergoClient.class);
    final BlockOperation mockOperation = mock(BlockOperation.class);
    when(mockOperation.listBlockHeaders(any(BlockHash.class), anyInt()))
        .thenReturn(new ArrayList<>());
    when(mockClient.getBlockOperation()).thenReturn(mockOperation);
    when(wallet.getAergoClient()).thenReturn(mockClient);

    assertNotNull(wallet.listBlockHeaders(BlockHash.of(BytesValue.EMPTY), 1));
  }

  @Test
  public void testListBlockHeadersByHeight() {
    final AbstractWallet wallet = mock(AbstractWallet.class, Mockito.CALLS_REAL_METHODS);
    final AergoClient mockClient = mock(AergoClient.class);
    final BlockOperation mockOperation = mock(BlockOperation.class);
    when(mockOperation.listBlockHeaders(anyLong(), anyInt())).thenReturn(new ArrayList<>());
    when(mockClient.getBlockOperation()).thenReturn(mockOperation);
    when(wallet.getAergoClient()).thenReturn(mockClient);

    assertNotNull(wallet.listBlockHeaders(1L, 1));
  }

  @Test
  public void testGetTransaction() {
    final AbstractWallet wallet = mock(AbstractWallet.class, Mockito.CALLS_REAL_METHODS);
    final AergoClient mockClient = mock(AergoClient.class);
    final TransactionOperation mockOperation = mock(TransactionOperation.class);
    when(mockOperation.getTransaction(any())).thenReturn(mock(Transaction.class));
    when(mockClient.getTransactionOperation()).thenReturn(mockOperation);
    when(wallet.getAergoClient()).thenReturn(mockClient);

    assertNotNull(wallet.getTransaction(TxHash.of(BytesValue.EMPTY)));
  }

  @Test
  public void testGetReceipt() {
    final AbstractWallet wallet = mock(AbstractWallet.class, Mockito.CALLS_REAL_METHODS);
    final AergoClient mockClient = mock(AergoClient.class);
    final ContractOperation mockOperation = mock(ContractOperation.class);
    when(mockOperation.getReceipt(any())).thenReturn(mock(ContractTxReceipt.class));
    when(mockClient.getContractOperation()).thenReturn(mockOperation);
    when(wallet.getAergoClient()).thenReturn(mockClient);

    assertNotNull(wallet.getReceipt(ContractTxHash.of(BytesValue.EMPTY)));
  }

  @Test
  public void testGetContractInterface() {
    final AbstractWallet wallet = mock(AbstractWallet.class, Mockito.CALLS_REAL_METHODS);
    final AergoClient mockClient = mock(AergoClient.class);
    final ContractOperation mockOperation = mock(ContractOperation.class);
    when(mockOperation.getContractInterface(any())).thenReturn(mock(ContractInterface.class));
    when(mockClient.getContractOperation()).thenReturn(mockOperation);
    when(wallet.getAergoClient()).thenReturn(mockClient);

    assertNotNull(wallet.getContractInterface(ContractAddress.of(BytesValue.EMPTY)));
  }

  @Test
  public void testGetAddress() {
    final AbstractWallet wallet = mock(AbstractWallet.class, Mockito.CALLS_REAL_METHODS);
    wallet.account = ServerManagedAccount.of(accountAddress);

    assertNotNull(wallet.getAddress());
  }

  @Test
  public void testSetNonce() {
    final AbstractWallet wallet = mock(AbstractWallet.class, Mockito.CALLS_REAL_METHODS);
    wallet.account = ServerManagedAccount.of(accountAddress);

    final long nonce = 3;
    wallet.setNonce(nonce);
    assertEquals(nonce, wallet.getRecentlyUsedNonce());
  }

  @Test
  public void testGetRecentlyUsedNonce() {
    final AbstractWallet wallet = mock(AbstractWallet.class, Mockito.CALLS_REAL_METHODS);
    wallet.account = ServerManagedAccount.of(accountAddress);

    assertNotNull(wallet.getRecentlyUsedNonce());
  }

}