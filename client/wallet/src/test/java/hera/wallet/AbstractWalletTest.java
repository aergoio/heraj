/*
 * * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import static hera.api.model.BytesValue.of;
import static hera.util.EncodingUtils.encodeBase58WithCheck;
import static hera.util.VersionUtils.envelop;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hera.AbstractTestCase;
import hera.api.AccountOperation;
import hera.api.BlockchainOperation;
import hera.api.ContractOperation;
import hera.api.TransactionOperation;
import hera.api.function.Function0;
import hera.api.function.Function1;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountFactory;
import hera.api.model.AccountState;
import hera.api.model.Aer;
import hera.api.model.Aer.Unit;
import hera.api.model.Authentication;
import hera.api.model.BytesValue;
import hera.api.model.ContractAddress;
import hera.api.model.ContractDefinition;
import hera.api.model.ContractFunction;
import hera.api.model.ContractInvocation;
import hera.api.model.ContractTxHash;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.Fee;
import hera.api.model.Identity;
import hera.api.model.PeerId;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.client.AergoClient;
import hera.exception.InvalidAuthentiationException;
import hera.exception.WalletException;
import hera.key.AergoKeyGenerator;
import hera.keystore.KeyStore;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class AbstractWalletTest extends AbstractTestCase {

  private final AccountAddress accountAddress =
      AccountAddress.of("AmLo9CGR3xFZPVKZ5moSVRNW1kyscY9rVkCvgrpwNJjRUPUWadC5");

  private final EncryptedPrivateKey encryptedPrivatekey = EncryptedPrivateKey
      .of("47RHxbUL3DhA1TMHksEPdVrhumcjdXLAB3Hkv61mqkC9M1Wncai5b91q7hpKydfFHKyyVvgKt");

  private final ContractAddress contractAddress =
      ContractAddress.of("AmLo9CGR3xFZPVKZ5moSVRNW1kyscY9rVkCvgrpwNJjRUPUWadC5");

  protected TransactionTrier trier;

  @SuppressWarnings("unchecked")
  @Before
  public void setUp() {
    trier = mock(TransactionTrier.class);
    when(trier.request(any(Function1.class))).thenReturn(TxHash.of(BytesValue.EMPTY));
    when(trier.request(any(Function0.class), any(Function1.class)))
        .thenReturn(TxHash.of(BytesValue.EMPTY));
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

    assertNotNull(wallet.getCurrentAccountState());
  }

  @Test
  public void testSavekey() {
    AbstractWallet wallet =
        mock(AbstractWallet.class, Mockito.CALLS_REAL_METHODS);
    wallet.keyStore = mock(KeyStore.class);
    wallet.saveKey(new AergoKeyGenerator().create(), randomUUID().toString());
  }

  @Test
  public void testExportKey() {
    AbstractWallet wallet =
        mock(AbstractWallet.class, Mockito.CALLS_REAL_METHODS);
    final KeyStore keyStore = mock(KeyStore.class);
    when(keyStore.export(any(Authentication.class))).thenReturn(encryptedPrivatekey);
    wallet.keyStore = keyStore;

    final Authentication authentication =
        Authentication.of(accountAddress, randomUUID().toString());
    final String exported = wallet.exportKey(authentication);
    assertNotNull(exported);
  }

  @Test
  public void testListKeyStoreAddresses() {
    AbstractWallet wallet =
        mock(AbstractWallet.class, Mockito.CALLS_REAL_METHODS);
    final KeyStore keyStore = mock(KeyStore.class);
    when(keyStore.listIdentities()).thenReturn(new ArrayList<Identity>());
    wallet.keyStore = keyStore;

    final List<Identity> exported = wallet.listKeyStoreIdentities();
    assertNotNull(exported);
  }

  @Test
  public void testUnlockOnSuccess() {
    AbstractWallet wallet =
        mock(AbstractWallet.class, Mockito.CALLS_REAL_METHODS);

    final AergoClient mockClient = mock(AergoClient.class);
    final AccountOperation mockOperation = mock(AccountOperation.class);
    when(mockOperation.getState(any(Account.class))).thenReturn(mock(AccountState.class));
    when(mockClient.getAccountOperation()).thenReturn(mockOperation);
    when(wallet.getAergoClient()).thenReturn(mockClient);

    final KeyStore keyStore = mock(KeyStore.class);
    when(keyStore.unlock(any(Authentication.class))).thenReturn(mock(Account.class));
    wallet.keyStore = keyStore;

    final Authentication authentication =
        Authentication.of(accountAddress, randomUUID().toString());
    final boolean unlockResult = wallet.unlock(authentication);
    assertTrue(unlockResult);
  }

  @Test
  public void testUnlockOnFailure() {
    AbstractWallet wallet =
        mock(AbstractWallet.class, Mockito.CALLS_REAL_METHODS);
    final KeyStore keyStore = mock(KeyStore.class);
    when(keyStore.unlock(any(Authentication.class)))
        .thenThrow(new InvalidAuthentiationException("invalid"));
    wallet.keyStore = keyStore;

    final Authentication authentication =
        Authentication.of(accountAddress, randomUUID().toString());
    final boolean unlockResult = wallet.unlock(authentication);
    assertFalse(unlockResult);
  }

  @Test
  public void testLock() {
    AbstractWallet wallet =
        mock(AbstractWallet.class, Mockito.CALLS_REAL_METHODS);
    wallet.keyStore = mock(KeyStore.class);

    final Authentication authentication =
        Authentication.of(accountAddress, randomUUID().toString());
    final boolean lockResult = wallet.lock(authentication);
    assertTrue(lockResult);
  }

  @Test
  public void testStoreKeyStoreOnSuccess() {
    AbstractWallet wallet =
        mock(AbstractWallet.class, Mockito.CALLS_REAL_METHODS);
    wallet.keyStore = mock(KeyStore.class);

    final boolean storeResult =
        wallet.storeKeyStore(randomUUID().toString(), randomUUID().toString());
    assertTrue(storeResult);
  }

  @Test
  public void testStoreKeyStoreOnFail() {
    AbstractWallet wallet =
        mock(AbstractWallet.class, Mockito.CALLS_REAL_METHODS);
    KeyStore mockKeyStore = mock(KeyStore.class);
    doThrow(new WalletException("")).when(mockKeyStore).store(anyString(), anyString());
    wallet.keyStore = mockKeyStore;

    final boolean storeResult =
        wallet.storeKeyStore(randomUUID().toString(), randomUUID().toString());
    assertTrue(false == storeResult);
  }

  @Test
  public void testSign() {
    AbstractWallet wallet =
        mock(AbstractWallet.class, Mockito.CALLS_REAL_METHODS);
    final AergoClient mockClient = mock(AergoClient.class);
    final AccountOperation mockOperation = mock(AccountOperation.class);
    when(mockOperation.sign(any(Account.class), any(RawTransaction.class)))
        .thenReturn(mock(Transaction.class));
    when(mockClient.getAccountOperation()).thenReturn(mockOperation);
    when(wallet.getAergoClient()).thenReturn(mockClient);

    wallet.account = new AccountFactory().create(accountAddress);

    final Transaction signed = wallet.sign(mock(RawTransaction.class));
    assertNotNull(signed);
  }

  @Test
  public void testVerify() {
    AbstractWallet wallet =
        mock(AbstractWallet.class, Mockito.CALLS_REAL_METHODS);
    final AergoClient mockClient = mock(AergoClient.class);
    final AccountOperation mockOperation = mock(AccountOperation.class);
    when(mockOperation.verify(any(Account.class), any(Transaction.class))).thenReturn(true);
    when(mockClient.getAccountOperation()).thenReturn(mockOperation);
    when(wallet.getAergoClient()).thenReturn(mockClient);

    wallet.account = new AccountFactory().create(accountAddress);

    final boolean verifyResult = wallet.verify(mock(Transaction.class));
    assertTrue(verifyResult);
  }

  @Test
  public void testCreateName() {
    AbstractWallet wallet =
        mock(AbstractWallet.class, Mockito.CALLS_REAL_METHODS);
    final AergoClient mockClient = mock(AergoClient.class);
    final AccountOperation mockOperation = mock(AccountOperation.class);
    when(mockOperation.createName(any(Account.class), anyString(), anyLong()))
        .thenReturn(mock(TxHash.class));
    when(mockClient.getAccountOperation()).thenReturn(mockOperation);
    when(wallet.getAergoClient()).thenReturn(mockClient);

    wallet.account = new AccountFactory().create(accountAddress);
    wallet.trier = trier;

    final TxHash defineHash = wallet.createName(randomUUID().toString());
    assertNotNull(defineHash);
  }

  @Test
  public void testUpdateName() {
    AbstractWallet wallet =
        mock(AbstractWallet.class, Mockito.CALLS_REAL_METHODS);
    final AergoClient mockClient = mock(AergoClient.class);
    final AccountOperation mockOperation = mock(AccountOperation.class);
    when(mockOperation.updateName(any(Account.class), anyString(), any(AccountAddress.class),
        anyLong())).thenReturn(mock(TxHash.class));
    when(mockClient.getAccountOperation()).thenReturn(mockOperation);
    when(wallet.getAergoClient()).thenReturn(mockClient);

    wallet.account = new AccountFactory().create(accountAddress);
    wallet.trier = trier;

    final TxHash updateHash = wallet.updateName(randomUUID().toString(), accountAddress);
    assertNotNull(updateHash);
  }

  @Test
  public void testStake() {
    AbstractWallet wallet =
        mock(AbstractWallet.class, Mockito.CALLS_REAL_METHODS);
    final AergoClient mockClient = mock(AergoClient.class);
    final AccountOperation mockOperation = mock(AccountOperation.class);
    when(mockOperation.stake(any(Account.class), any(Aer.class), anyLong()))
        .thenReturn(mock(TxHash.class));
    when(mockClient.getAccountOperation()).thenReturn(mockOperation);
    when(wallet.getAergoClient()).thenReturn(mockClient);

    wallet.account = new AccountFactory().create(accountAddress);
    wallet.trier = trier;

    final TxHash stakingHash = wallet.stake(Aer.AERGO_ONE);
    assertNotNull(stakingHash);
  }

  @Test
  public void testUnstake() {
    AbstractWallet wallet =
        mock(AbstractWallet.class, Mockito.CALLS_REAL_METHODS);
    final AergoClient mockClient = mock(AergoClient.class);
    final AccountOperation mockOperation = mock(AccountOperation.class);
    when(mockOperation.unstake(any(Account.class), any(Aer.class), anyLong()))
        .thenReturn(mock(TxHash.class));
    when(mockClient.getAccountOperation()).thenReturn(mockOperation);
    when(wallet.getAergoClient()).thenReturn(mockClient);

    wallet.account = new AccountFactory().create(accountAddress);
    wallet.trier = trier;

    final TxHash stakingHash = wallet.unstake(Aer.AERGO_ONE);
    assertNotNull(stakingHash);
  }

  @Test
  public void testVote() {
    AbstractWallet wallet =
        mock(AbstractWallet.class, Mockito.CALLS_REAL_METHODS);
    final AergoClient mockClient = mock(AergoClient.class);
    final BlockchainOperation mockOperation = mock(BlockchainOperation.class);
    when(mockOperation.vote(any(Account.class), any(PeerId.class), anyLong()))
        .thenReturn(mock(TxHash.class));
    when(mockClient.getBlockchainOperation()).thenReturn(mockOperation);
    when(wallet.getAergoClient()).thenReturn(mockClient);

    wallet.account = new AccountFactory().create(accountAddress);
    wallet.trier = trier;

    final TxHash votingHash = wallet.vote(new PeerId(of(randomUUID().toString().getBytes())));
    assertNotNull(votingHash);
  }

  @Test
  public void testSendWithName() {
    final AbstractWallet wallet = mock(AbstractWallet.class, Mockito.CALLS_REAL_METHODS);
    wallet.account = new AccountFactory().create(accountAddress);

    final AergoClient mockClient = mock(AergoClient.class);
    final TransactionOperation mockTransactionOperation = mock(TransactionOperation.class);
    when(mockTransactionOperation.commit(any(Transaction.class)))
        .thenReturn(TxHash.of(BytesValue.EMPTY));
    when(mockClient.getTransactionOperation()).thenReturn(mockTransactionOperation);
    final AccountOperation mockAccountOperation = mock(AccountOperation.class);
    when(mockAccountOperation.sign(any(Account.class), any(RawTransaction.class)))
        .thenReturn(mock(Transaction.class));
    when(mockClient.getAccountOperation()).thenReturn(mockAccountOperation);
    when(wallet.getAergoClient()).thenReturn(mockClient);

    wallet.trier = trier;

    assertNotNull(
        wallet.send(randomUUID().toString(), Aer.of("1000", Unit.AER), Fee.getDefaultFee()));
  }

  @Test
  public void testSendWithAddress() {
    final AbstractWallet wallet = mock(AbstractWallet.class, Mockito.CALLS_REAL_METHODS);
    wallet.account = new AccountFactory().create(accountAddress);

    final AergoClient mockClient = mock(AergoClient.class);
    final TransactionOperation mockTransactionOperation = mock(TransactionOperation.class);
    when(mockTransactionOperation.commit(any(Transaction.class)))
        .thenReturn(TxHash.of(BytesValue.EMPTY));
    when(mockClient.getTransactionOperation()).thenReturn(mockTransactionOperation);
    final AccountOperation mockAccountOperation = mock(AccountOperation.class);
    when(mockAccountOperation.sign(any(Account.class), any(RawTransaction.class)))
        .thenReturn(mock(Transaction.class));
    when(mockClient.getAccountOperation()).thenReturn(mockAccountOperation);
    when(wallet.getAergoClient()).thenReturn(mockClient);

    wallet.trier = trier;

    assertNotNull(wallet.send(accountAddress, Aer.of("1000", Unit.AER), Fee.getDefaultFee()));
  }

  @Test
  public void testDeploy() {
    final AbstractWallet wallet = mock(AbstractWallet.class, Mockito.CALLS_REAL_METHODS);
    wallet.account = new AccountFactory().create(accountAddress);
    final AergoClient mockClient = mock(AergoClient.class);
    final ContractOperation mockOperation = mock(ContractOperation.class);
    when(mockOperation.deploy(any(Account.class), any(ContractDefinition.class), anyLong(),
        any(Fee.class)))
            .thenReturn(ContractTxHash.of(BytesValue.EMPTY));
    when(mockClient.getContractOperation()).thenReturn(mockOperation);
    when(wallet.getAergoClient()).thenReturn(mockClient);

    wallet.trier = trier;

    final ContractDefinition definition = new ContractDefinition(encodeBase58WithCheck(
        of(envelop(randomUUID().toString().getBytes(), ContractDefinition.PAYLOAD_VERSION))));
    assertNotNull(wallet.deploy(definition, Fee.getDefaultFee()));
  }

  @Test
  public void testExecute() {
    final AbstractWallet wallet = mock(AbstractWallet.class, Mockito.CALLS_REAL_METHODS);
    wallet.account = new AccountFactory().create(accountAddress);
    final AergoClient mockClient = mock(AergoClient.class);
    final ContractOperation mockOperation = mock(ContractOperation.class);
    when(mockOperation.execute(any(Account.class), any(ContractInvocation.class), anyLong(),
        any(Fee.class))).thenReturn(ContractTxHash.of(BytesValue.EMPTY));
    when(mockClient.getContractOperation()).thenReturn(mockOperation);
    when(wallet.getAergoClient()).thenReturn(mockClient);

    wallet.trier = trier;

    assertNotNull(wallet.execute(new ContractInvocation(contractAddress, new ContractFunction("")),
        Fee.getDefaultFee()));
  }

  @Test
  public void testGetAccount() {
    final AbstractWallet wallet = mock(AbstractWallet.class, Mockito.CALLS_REAL_METHODS);
    wallet.account = mock(Account.class);
    assertNotNull(wallet.getCurrentAccount());
  }

  @Test
  public void testGetAccountOnNoAccount() {
    final AbstractWallet wallet = mock(AbstractWallet.class, Mockito.CALLS_REAL_METHODS);
    try {
      wallet.getCurrentAccount();
      fail();
    } catch (Exception e) {
      // good we expected this
    }
  }

  @Test
  public void testGetRecentlyUsedNonce() {
    final AbstractWallet wallet = mock(AbstractWallet.class, Mockito.CALLS_REAL_METHODS);
    wallet.account = new AccountFactory().create(accountAddress);

    assertNotNull(wallet.getRecentlyUsedNonce());
  }

  @Test
  public void testIncrementAndGetNonce() {
    final AbstractWallet wallet = mock(AbstractWallet.class, Mockito.CALLS_REAL_METHODS);
    wallet.account = new AccountFactory().create(accountAddress);

    final long preNonce = wallet.getRecentlyUsedNonce();
    assertEquals(preNonce + 1, wallet.incrementAndGetNonce());
    assertEquals(preNonce + 1, wallet.getRecentlyUsedNonce());
  }

}
