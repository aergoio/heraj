/*
 * * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import static hera.api.model.BytesValue.of;
import static hera.util.EncodingUtils.encodeBase58WithCheck;
import static hera.util.VersionUtils.envelop;
import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.CALLS_REAL_METHODS;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.withSettings;

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
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.api.model.internal.Time;
import hera.api.model.internal.TryCountAndInterval;
import hera.client.AergoClient;
import hera.key.AergoKeyGenerator;
import hera.keystore.KeyStore;
import java.util.ArrayList;
import java.util.List;
import org.junit.Before;
import org.junit.Test;

public class AbstractWalletTest extends AbstractTestCase {

  private final AccountAddress accountAddress =
      AccountAddress.of("AmLo9CGR3xFZPVKZ5moSVRNW1kyscY9rVkCvgrpwNJjRUPUWadC5");

  private final EncryptedPrivateKey encryptedPrivatekey = EncryptedPrivateKey
      .of("47RHxbUL3DhA1TMHksEPdVrhumcjdXLAB3Hkv61mqkC9M1Wncai5b91q7hpKydfFHKyyVvgKt");

  private final ContractAddress contractAddress =
      ContractAddress.of("AmLo9CGR3xFZPVKZ5moSVRNW1kyscY9rVkCvgrpwNJjRUPUWadC5");


  protected TransactionTrier trier;

  protected AbstractWallet supplyWallet(final AergoClient aergoClient) {
    final TryCountAndInterval interval = new TryCountAndInterval(1, Time.of(1000L));
    final AbstractWallet wallet = mock(AbstractWallet.class,
        withSettings().useConstructor(aergoClient, interval).defaultAnswer(CALLS_REAL_METHODS));
    return wallet;
  }

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
    final AergoClient mockClient = mock(AergoClient.class);
    final AccountOperation mockOperation = mock(AccountOperation.class);
    when(mockOperation.getState(any(AccountAddress.class))).thenReturn(mock(AccountState.class));
    when(mockClient.getAccountOperation()).thenReturn(mockOperation);

    final AbstractWallet wallet = supplyWallet(mockClient);
    wallet.account = new AccountFactory().create(new AergoKeyGenerator().create());

    assertNotNull(wallet.getAccountState());
  }

  @Test
  public void testSavekey() {
    final AbstractWallet wallet = supplyWallet(mock(AergoClient.class));
    wallet.keyStore = mock(KeyStore.class);
    wallet.saveKey(new AergoKeyGenerator().create(), randomUUID().toString());
  }

  @Test
  public void testExportKey() {
    final AbstractWallet wallet = supplyWallet(mock(AergoClient.class));
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
    final AbstractWallet wallet = supplyWallet(mock(AergoClient.class));
    final KeyStore keyStore = mock(KeyStore.class);
    when(keyStore.listIdentities()).thenReturn(new ArrayList<Identity>());
    wallet.keyStore = keyStore;

    final List<Identity> exported = wallet.listKeyStoreIdentities();
    assertNotNull(exported);
  }

  @Test
  public void testUnlockOnSuccess() {
    final AergoClient mockClient = mock(AergoClient.class);
    final AccountOperation mockOperation = mock(AccountOperation.class);
    when(mockOperation.getState(any(Account.class))).thenReturn(mock(AccountState.class));
    when(mockClient.getAccountOperation()).thenReturn(mockOperation);

    final KeyStore keyStore = mock(KeyStore.class);
    when(keyStore.unlock(any(Authentication.class))).thenReturn(mock(Account.class));

    final AbstractWallet wallet = supplyWallet(mockClient);
    wallet.keyStore = keyStore;

    final Authentication authentication =
        Authentication.of(accountAddress, randomUUID().toString());
    final boolean unlockResult = wallet.unlock(authentication);
    assertTrue(unlockResult);
  }

  @Test
  public void testUnlock() {
    final AbstractWallet wallet = supplyWallet(mock(AergoClient.class));
    final KeyStore keyStore = mock(KeyStore.class);
    when(keyStore.unlock(any(Authentication.class))).thenReturn(null);
    wallet.keyStore = keyStore;

    final Authentication authentication =
        Authentication.of(accountAddress, randomUUID().toString());
    final boolean unlockResult = wallet.unlock(authentication);
    assertFalse(unlockResult);
  }

  @Test
  public void testLock() {
    final AbstractWallet wallet = supplyWallet(mock(AergoClient.class));
    final KeyStore mockKeyStore = mock(KeyStore.class);
    when(mockKeyStore.lock(any(Authentication.class))).thenReturn(true);
    wallet.keyStore = mockKeyStore;

    final Authentication authentication =
        Authentication.of(accountAddress, randomUUID().toString());
    final boolean lockResult = wallet.lock(authentication);
    assertTrue(lockResult);
  }

  @Test
  public void testStoreKeyStoreOnSuccess() {
    final AbstractWallet wallet = supplyWallet(mock(AergoClient.class));
    wallet.keyStore = mock(KeyStore.class);

    wallet.storeKeyStore(randomUUID().toString(), randomUUID().toString());
  }

  @Test
  public void testLoadAccount() {
    final AbstractWallet wallet = supplyWallet(mock(AergoClient.class));
    final KeyStore keyStore = mock(KeyStore.class);
    when(keyStore.unlock(any(Authentication.class))).thenReturn(mock(Account.class));
    wallet.keyStore = keyStore;

    final Authentication authentication =
        Authentication.of(accountAddress, randomUUID().toString());
    final Account unlockedAccount = wallet.loadAccount(authentication);
    assertNotNull(unlockedAccount);
  }

  @Test
  public void testLoadAccountOnFail() {
    final AbstractWallet wallet = supplyWallet(mock(AergoClient.class));
    final KeyStore keyStore = mock(KeyStore.class);
    when(keyStore.unlock(any(Authentication.class))).thenReturn(null);
    wallet.keyStore = keyStore;

    final Authentication authentication =
        Authentication.of(accountAddress, randomUUID().toString());
    final Account unlockedAccount = wallet.loadAccount(authentication);
    assertNull(unlockedAccount);
  }

  @Test
  public void testSign() {
    final AergoClient mockClient = mock(AergoClient.class);
    final AccountOperation mockOperation = mock(AccountOperation.class);
    when(mockOperation.sign(any(Account.class), any(RawTransaction.class)))
        .thenReturn(mock(Transaction.class));
    when(mockClient.getAccountOperation()).thenReturn(mockOperation);

    final AbstractWallet wallet = supplyWallet(mockClient);
    wallet.account = new AccountFactory().create(accountAddress);

    final Transaction signed = wallet.sign(mock(RawTransaction.class));
    assertNotNull(signed);
  }

  @Test
  public void testVerify() {
    final AergoClient mockClient = mock(AergoClient.class);
    final AccountOperation mockOperation = mock(AccountOperation.class);
    when(mockOperation.verify(any(Account.class), any(Transaction.class))).thenReturn(true);
    when(mockClient.getAccountOperation()).thenReturn(mockOperation);

    final AbstractWallet wallet = supplyWallet(mockClient);
    wallet.account = new AccountFactory().create(accountAddress);

    final boolean verifyResult = wallet.verify(mock(Transaction.class));
    assertTrue(verifyResult);
  }

  @Test
  public void testCreateName() {
    final AergoClient mockClient = mock(AergoClient.class);
    final AccountOperation mockOperation = mock(AccountOperation.class);
    when(mockOperation.createName(any(Account.class), anyString(), anyLong()))
        .thenReturn(mock(TxHash.class));
    when(mockClient.getAccountOperation()).thenReturn(mockOperation);

    final AbstractWallet wallet = supplyWallet(mockClient);
    wallet.account = new AccountFactory().create(accountAddress);
    wallet.trier = trier;

    final TxHash defineHash = wallet.createName(randomUUID().toString());
    assertNotNull(defineHash);
  }

  @Test
  public void testUpdateName() {
    final AergoClient mockClient = mock(AergoClient.class);
    final AccountOperation mockOperation = mock(AccountOperation.class);
    when(mockOperation.updateName(any(Account.class), anyString(), any(AccountAddress.class),
        anyLong())).thenReturn(mock(TxHash.class));
    when(mockClient.getAccountOperation()).thenReturn(mockOperation);

    final AbstractWallet wallet = supplyWallet(mockClient);
    wallet.account = new AccountFactory().create(accountAddress);
    wallet.trier = trier;

    final TxHash updateHash = wallet.updateName(randomUUID().toString(), accountAddress);
    assertNotNull(updateHash);
  }

  @Test
  public void testStake() {
    final AergoClient mockClient = mock(AergoClient.class);
    final AccountOperation mockOperation = mock(AccountOperation.class);
    when(mockOperation.stake(any(Account.class), any(Aer.class), anyLong()))
        .thenReturn(mock(TxHash.class));
    when(mockClient.getAccountOperation()).thenReturn(mockOperation);

    final AbstractWallet wallet = supplyWallet(mockClient);
    wallet.account = new AccountFactory().create(accountAddress);
    wallet.trier = trier;

    final TxHash stakingHash = wallet.stake(Aer.AERGO_ONE);
    assertNotNull(stakingHash);
  }

  @Test
  public void testUnstake() {
    final AergoClient mockClient = mock(AergoClient.class);
    final AccountOperation mockOperation = mock(AccountOperation.class);
    when(mockOperation.unstake(any(Account.class), any(Aer.class), anyLong()))
        .thenReturn(mock(TxHash.class));
    when(mockClient.getAccountOperation()).thenReturn(mockOperation);

    final AbstractWallet wallet = supplyWallet(mockClient);
    wallet.account = new AccountFactory().create(accountAddress);
    wallet.trier = trier;

    final TxHash stakingHash = wallet.unstake(Aer.AERGO_ONE);
    assertNotNull(stakingHash);
  }

  @SuppressWarnings("unchecked")
  @Test
  public void testVote() {
    final AergoClient mockClient = mock(AergoClient.class);
    final BlockchainOperation mockOperation = mock(BlockchainOperation.class);
    when(mockOperation.vote(any(Account.class), any(String.class), any(List.class), anyLong()))
        .thenReturn(mock(TxHash.class));
    when(mockClient.getBlockchainOperation()).thenReturn(mockOperation);

    final AbstractWallet wallet = supplyWallet(mockClient);
    wallet.account = new AccountFactory().create(accountAddress);
    wallet.trier = trier;

    final TxHash votingHash =
        wallet.vote(randomUUID().toString(), asList(new String[] {randomUUID().toString()}));
    assertNotNull(votingHash);
  }

  @Test
  public void testSendWithName() {
    final AergoClient mockClient = mock(AergoClient.class);
    final TransactionOperation mockTransactionOperation = mock(TransactionOperation.class);
    when(mockTransactionOperation.commit(any(Transaction.class)))
        .thenReturn(TxHash.of(BytesValue.EMPTY));
    when(mockClient.getTransactionOperation()).thenReturn(mockTransactionOperation);
    final AccountOperation mockAccountOperation = mock(AccountOperation.class);
    when(mockAccountOperation.sign(any(Account.class), any(RawTransaction.class)))
        .thenReturn(mock(Transaction.class));
    when(mockClient.getAccountOperation()).thenReturn(mockAccountOperation);

    final AbstractWallet wallet = supplyWallet(mockClient);
    wallet.trier = trier;
    wallet.account = new AccountFactory().create(accountAddress);

    assertNotNull(
        wallet.send(randomUUID().toString(), Aer.of("1000", Unit.AER), Fee.getDefaultFee()));
  }

  @Test
  public void testSendWithAddress() {
    final AergoClient mockClient = mock(AergoClient.class);
    final TransactionOperation mockTransactionOperation = mock(TransactionOperation.class);
    when(mockTransactionOperation.commit(any(Transaction.class)))
        .thenReturn(TxHash.of(BytesValue.EMPTY));
    when(mockClient.getTransactionOperation()).thenReturn(mockTransactionOperation);
    final AccountOperation mockAccountOperation = mock(AccountOperation.class);
    when(mockAccountOperation.sign(any(Account.class), any(RawTransaction.class)))
        .thenReturn(mock(Transaction.class));
    when(mockClient.getAccountOperation()).thenReturn(mockAccountOperation);

    final AbstractWallet wallet = supplyWallet(mockClient);
    wallet.account = new AccountFactory().create(accountAddress);
    wallet.trier = trier;

    assertNotNull(wallet.send(accountAddress, Aer.of("1000", Unit.AER), Fee.getDefaultFee()));
  }

  @Test
  public void testDeploy() {
    final AergoClient mockClient = mock(AergoClient.class);
    final ContractOperation mockOperation = mock(ContractOperation.class);
    when(mockOperation.deploy(any(Account.class), any(ContractDefinition.class), anyLong(),
        any(Fee.class)))
            .thenReturn(ContractTxHash.of(BytesValue.EMPTY));
    when(mockClient.getContractOperation()).thenReturn(mockOperation);

    final AbstractWallet wallet = supplyWallet(mockClient);
    wallet.account = new AccountFactory().create(accountAddress);
    wallet.trier = trier;

    final ContractDefinition definition = new ContractDefinition(encodeBase58WithCheck(
        of(envelop(randomUUID().toString().getBytes(), ContractDefinition.PAYLOAD_VERSION))));
    assertNotNull(wallet.deploy(definition, Fee.getDefaultFee()));
  }

  @Test
  public void testExecute() {
    final AergoClient mockClient = mock(AergoClient.class);
    final ContractOperation mockOperation = mock(ContractOperation.class);
    when(mockOperation.execute(any(Account.class), any(ContractInvocation.class), anyLong(),
        any(Fee.class))).thenReturn(ContractTxHash.of(BytesValue.EMPTY));
    when(mockClient.getContractOperation()).thenReturn(mockOperation);

    final AbstractWallet wallet = supplyWallet(mockClient);
    wallet.account = new AccountFactory().create(accountAddress);
    wallet.trier = trier;

    assertNotNull(wallet.execute(new ContractInvocation(contractAddress, new ContractFunction("")),
        Fee.getDefaultFee()));
  }

  @Test
  public void testGetAccount() {
    final AbstractWallet wallet = supplyWallet(mock(AergoClient.class));
    wallet.account = mock(Account.class);
    assertNotNull(wallet.getAccount());
  }

  @Test
  public void testGetAccountOnNoAccount() {
    final AbstractWallet wallet = supplyWallet(mock(AergoClient.class));
    try {
      wallet.getAccount();
      fail();
    } catch (Exception e) {
      // good we expected this
    }
  }

  @Test
  public void testGetRecentlyUsedNonce() {
    final AbstractWallet wallet = supplyWallet(mock(AergoClient.class));
    wallet.account = new AccountFactory().create(accountAddress);

    assertNotNull(wallet.getRecentlyUsedNonce());
  }

  @Test
  public void testIncrementAndGetNonce() {
    final AbstractWallet wallet = supplyWallet(mock(AergoClient.class));
    wallet.account = new AccountFactory().create(accountAddress);

    final long preNonce = wallet.getRecentlyUsedNonce();
    assertEquals(preNonce + 1, wallet.incrementAndGetNonce());
    assertEquals(preNonce + 1, wallet.getRecentlyUsedNonce());
  }

}
