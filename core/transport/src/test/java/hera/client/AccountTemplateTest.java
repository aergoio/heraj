/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.TransportConstants.ACCOUNT_CREATE_NAME;
import static hera.TransportConstants.ACCOUNT_GETNAMEOWNER;
import static hera.TransportConstants.ACCOUNT_GETSTATE;
import static hera.TransportConstants.ACCOUNT_SIGN;
import static hera.TransportConstants.ACCOUNT_UPDATE_NAME;
import static hera.TransportConstants.ACCOUNT_VERIFY;
import static hera.api.model.BytesValue.of;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import hera.AbstractTestCase;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountFactory;
import hera.api.model.AccountState;
import hera.api.model.BytesValue;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.api.tupleorerror.Function1;
import hera.api.tupleorerror.Function2;
import hera.api.tupleorerror.Function3;
import hera.api.tupleorerror.Function4;
import hera.api.tupleorerror.WithIdentity;
import hera.key.AergoKeyGenerator;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

@PrepareForTest({AccountBaseTemplate.class, Account.class, EncryptedPrivateKey.class})
public class AccountTemplateTest extends AbstractTestCase {

  protected static final EncryptedPrivateKey ENCRYPTED_PRIVATE_KEY =
      new EncryptedPrivateKey(of(new byte[] {EncryptedPrivateKey.VERSION}));

  protected static final AccountAddress ACCOUNT_ADDRESS =
      new AccountAddress(of(new byte[] {AccountAddress.VERSION}));

  protected static final String PASSWORD = randomUUID().toString();

  protected AccountTemplate supplyAccountTemplate(
      final AccountBaseTemplate accountBaseTemplate) {
    final AccountTemplate accountTemplate = new AccountTemplate();
    accountTemplate.accountBaseTemplate = accountBaseTemplate;
    accountTemplate.setContextProvider(() -> context);
    return accountTemplate;
  }

  @Override
  public void setUp() {
    super.setUp();
  }

  @Test
  public void testGetState() {
    final AccountBaseTemplate base = mock(AccountBaseTemplate.class);
    final AccountState mockState = mock(AccountState.class);
    final FinishableFuture<AccountState> future = new FinishableFuture<AccountState>();
    future.success(mockState);
    when(base.getStateFunction())
        .thenReturn(new Function1<AccountAddress, FinishableFuture<AccountState>>() {
          @Override
          public FinishableFuture<AccountState> apply(AccountAddress t) {
            return future;
          }
        });

    final AccountTemplate accountTemplate = supplyAccountTemplate(base);

    final AccountState accountState =
        accountTemplate.getState(ACCOUNT_ADDRESS);
    assertNotNull(accountState);
    assertEquals(ACCOUNT_GETSTATE,
        ((WithIdentity) accountTemplate.getStateFunction()).getIdentity());
  }

  @Test
  public void testCreateName() {
    final AccountBaseTemplate base = mock(AccountBaseTemplate.class);
    final TxHash mockHash = mock(TxHash.class);
    final FinishableFuture<TxHash> future = new FinishableFuture<TxHash>();
    future.success(mockHash);
    when(base.getCreateNameFunction())
        .thenReturn(new Function3<Account, String, Long, FinishableFuture<TxHash>>() {
          @Override
          public FinishableFuture<TxHash> apply(Account t1, String t2, Long t3) {
            return future;
          }
        });

    final AccountTemplate accountTemplate = supplyAccountTemplate(base);

    final Account account = new AccountFactory().create(new AergoKeyGenerator().create());
    final TxHash nameTxHash = accountTemplate.createName(account, randomUUID().toString(),
        account.incrementAndGetNonce());
    assertNotNull(nameTxHash);
    assertEquals(ACCOUNT_CREATE_NAME,
        ((WithIdentity) accountTemplate.getCreateNameFunction()).getIdentity());
  }

  @Test
  public void testUpdateName() {
    final AccountBaseTemplate base = mock(AccountBaseTemplate.class);
    final TxHash mockHash = mock(TxHash.class);
    final FinishableFuture<TxHash> future = new FinishableFuture<TxHash>();
    future.success(mockHash);
    when(base.getUpdateNameFunction()).thenReturn(
        new Function4<Account, String, AccountAddress, Long, FinishableFuture<TxHash>>() {
          @Override
          public FinishableFuture<TxHash> apply(Account t1, String t2, AccountAddress t3,
              Long t4) {
            return future;
          }
        });

    final AccountTemplate accountTemplate = supplyAccountTemplate(base);

    final Account owner = new AccountFactory().create(new AergoKeyGenerator().create());
    final Account newOwner = new AccountFactory().create(new AergoKeyGenerator().create());
    final TxHash updateTxHash = accountTemplate.updateName(owner, randomUUID().toString(),
        newOwner.getAddress(), owner.incrementAndGetNonce());
    assertNotNull(updateTxHash);
    assertEquals(ACCOUNT_UPDATE_NAME,
        ((WithIdentity) accountTemplate.getUpdateNameFunction()).getIdentity());
  }

  @Test
  public void testGetNameOwner() {
    final AccountBaseTemplate base = mock(AccountBaseTemplate.class);
    final FinishableFuture<AccountAddress> future = new FinishableFuture<AccountAddress>();
    future.success(new AccountAddress(BytesValue.EMPTY));
    when(base.getGetNameOwnerFunction())
        .thenReturn(new Function1<String, FinishableFuture<AccountAddress>>() {
          @Override
          public FinishableFuture<AccountAddress> apply(String t) {
            return future;
          }
        });

    final AccountTemplate accountTemplate = supplyAccountTemplate(base);

    final AccountAddress owner = accountTemplate.getNameOwner(randomUUID().toString());
    assertNotNull(owner);
    assertEquals(ACCOUNT_GETNAMEOWNER,
        ((WithIdentity) accountTemplate.getNameOwnerFunction()).getIdentity());
  }

  @Test
  public void testSign() {
    final AccountBaseTemplate base = mock(AccountBaseTemplate.class);
    final Transaction mockTransaction = mock(Transaction.class);
    final FinishableFuture<Transaction> future = new FinishableFuture<Transaction>();
    future.success(mockTransaction);
    when(base.getSignFunction())
        .thenReturn(new Function2<Account, RawTransaction, FinishableFuture<Transaction>>() {
          @Override
          public FinishableFuture<Transaction> apply(Account t1, RawTransaction t2) {
            return future;
          }
        });

    final AccountTemplate accountTemplate = supplyAccountTemplate(base);

    final Account account = mock(Account.class);
    final Transaction transaction = mock(Transaction.class);
    final Transaction signedTransaction = accountTemplate.sign(account, transaction);
    assertNotNull(signedTransaction);
    assertEquals(ACCOUNT_SIGN, ((WithIdentity) accountTemplate.getSignFunction()).getIdentity());
  }

  @Test
  public void testVerify() {
    final AccountBaseTemplate base = mock(AccountBaseTemplate.class);
    final FinishableFuture<Boolean> future = new FinishableFuture<Boolean>();
    future.success(true);
    when(base.getVerifyFunction())
        .thenReturn(new Function2<Account, Transaction, FinishableFuture<Boolean>>() {
          @Override
          public FinishableFuture<Boolean> apply(Account t1, Transaction t2) {
            return future;
          }
        });

    final AccountTemplate accountTemplate = supplyAccountTemplate(base);

    final Account account = mock(Account.class);
    final Transaction transaction = mock(Transaction.class);
    final boolean verifyResult =
        accountTemplate.verify(account, transaction);
    assertNotNull(verifyResult);
    assertEquals(ACCOUNT_VERIFY,
        ((WithIdentity) accountTemplate.getVerifyFunction()).getIdentity());
  }

}
