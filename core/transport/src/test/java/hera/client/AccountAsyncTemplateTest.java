/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.TransportConstants.ACCOUNT_CREATE_NAME_ASYNC;
import static hera.TransportConstants.ACCOUNT_GETNAMEOWNER_ASYNC;
import static hera.TransportConstants.ACCOUNT_GETSTATE_ASYNC;
import static hera.TransportConstants.ACCOUNT_SIGN_ASYNC;
import static hera.TransportConstants.ACCOUNT_UPDATE_NAME_ASYNC;
import static hera.TransportConstants.ACCOUNT_VERIFY_ASYNC;
import static hera.api.model.BytesValue.of;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import hera.AbstractTestCase;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountFactory;
import hera.api.model.AccountState;
import hera.api.model.BytesValue;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.Transaction;
import hera.api.model.TxHash;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.api.tupleorerror.ResultOrErrorFutureFactory;
import hera.api.tupleorerror.WithIdentity;
import hera.key.AergoKeyGenerator;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

@PrepareForTest({AccountBaseTemplate.class})
public class AccountAsyncTemplateTest extends AbstractTestCase {

  protected static final EncryptedPrivateKey ENCRYPTED_PRIVATE_KEY =
      new EncryptedPrivateKey(of(new byte[] {EncryptedPrivateKey.VERSION}));

  protected static final AccountAddress ACCOUNT_ADDRESS =
      new AccountAddress(of(new byte[] {AccountAddress.VERSION}));

  protected static final String PASSWORD = randomUUID().toString();

  protected AccountAsyncTemplate supplyAccountAsyncTemplate(
      final AccountBaseTemplate accountBaseTemplate) {
    final AccountAsyncTemplate accountAsyncTemplate = new AccountAsyncTemplate();
    accountAsyncTemplate.accountBaseTemplate = accountBaseTemplate;
    accountAsyncTemplate.setContextProvider(() -> context);
    return accountAsyncTemplate;
  }

  @Override
  public void setUp() {
    super.setUp();
  }

  @Test
  public void testGetState() {
    final AccountBaseTemplate base = mock(AccountBaseTemplate.class);
    final AccountState mockState = mock(AccountState.class);
    final ResultOrErrorFuture<AccountState> future =
        ResultOrErrorFutureFactory.supply(() -> mockState);
    when(base.getStateFunction()).thenReturn((a) -> future);

    final AccountAsyncTemplate accountAsyncTemplate = supplyAccountAsyncTemplate(base);

    final ResultOrErrorFuture<AccountState> accountStateFuture =
        accountAsyncTemplate.getState(ACCOUNT_ADDRESS);
    assertTrue(accountStateFuture.get().hasResult());
    assertEquals(ACCOUNT_GETSTATE_ASYNC,
        ((WithIdentity) accountAsyncTemplate.getStateFunction()).getIdentity());
  }

  @Test
  public void testCreateName() {
    final AccountBaseTemplate base = mock(AccountBaseTemplate.class);
    final TxHash mockHash = mock(TxHash.class);
    final ResultOrErrorFuture<TxHash> future =
        ResultOrErrorFutureFactory.supply(() -> mockHash);
    when(base.getCreateNameFunction()).thenReturn((a, i, n) -> future);

    final AccountAsyncTemplate accountAsyncTemplate = supplyAccountAsyncTemplate(base);

    final Account account = new AccountFactory().create(new AergoKeyGenerator().create());
    final ResultOrErrorFuture<TxHash> nameTxHash =
        accountAsyncTemplate.createName(account, randomUUID().toString(),
            account.incrementAndGetNonce());
    assertTrue(nameTxHash.get().hasResult());
    assertEquals(ACCOUNT_CREATE_NAME_ASYNC,
        ((WithIdentity) accountAsyncTemplate.getCreateNameFunction()).getIdentity());
  }

  @Test
  public void testUpdateName() {
    final AccountBaseTemplate base = mock(AccountBaseTemplate.class);
    final TxHash mockHash = mock(TxHash.class);
    final ResultOrErrorFuture<TxHash> future =
        ResultOrErrorFutureFactory.supply(() -> mockHash);
    when(base.getUpdateNameFunction()).thenReturn((a, i, t, n) -> future);

    final AccountAsyncTemplate accountAsyncTemplate = supplyAccountAsyncTemplate(base);

    final Account owner = new AccountFactory().create(new AergoKeyGenerator().create());
    final Account newOwner = new AccountFactory().create(new AergoKeyGenerator().create());
    final ResultOrErrorFuture<TxHash> updateTxHash =
        accountAsyncTemplate.updateName(owner, randomUUID().toString(),
            newOwner.getAddress(), owner.incrementAndGetNonce());
    assertTrue(updateTxHash.get().hasResult());
    assertEquals(ACCOUNT_UPDATE_NAME_ASYNC,
        ((WithIdentity) accountAsyncTemplate.getUpdateNameFunction()).getIdentity());
  }

  @Test
  public void testGetNameOwner() {
    final AccountBaseTemplate base = mock(AccountBaseTemplate.class);
    final ResultOrErrorFuture<AccountAddress> future =
        ResultOrErrorFutureFactory.supply(() -> new AccountAddress(BytesValue.EMPTY));
    when(base.getGetNameOwnerFunction()).thenReturn(s -> future);

    final AccountAsyncTemplate accountAsyncTemplate = supplyAccountAsyncTemplate(base);

    final ResultOrErrorFuture<AccountAddress> owner =
        accountAsyncTemplate.getNameOwner(randomUUID().toString());
    assertTrue(owner.get().hasResult());
    assertEquals(ACCOUNT_GETNAMEOWNER_ASYNC,
        ((WithIdentity) accountAsyncTemplate.getNameOwnerFunction()).getIdentity());
  }

  @Test
  public void testSign() {
    final AccountBaseTemplate base = mock(AccountBaseTemplate.class);
    final Transaction mockSignedTransaction = mock(Transaction.class);
    final ResultOrErrorFuture<Transaction> future =
        ResultOrErrorFutureFactory.supply(() -> mockSignedTransaction);
    when(base.getSignFunction()).thenReturn((a, t) -> future);

    final AccountAsyncTemplate accountAsyncTemplate = supplyAccountAsyncTemplate(base);

    final Account account = mock(Account.class);
    final Transaction transaction = mock(Transaction.class);
    final ResultOrErrorFuture<Transaction> signedTransactionFuture =
        accountAsyncTemplate.sign(account, transaction);
    assertTrue(signedTransactionFuture.get().hasResult());
    assertEquals(ACCOUNT_SIGN_ASYNC,
        ((WithIdentity) accountAsyncTemplate.getSignFunction()).getIdentity());
  }

  @Test
  public void testVerify() {
    final AccountBaseTemplate base = mock(AccountBaseTemplate.class);
    final ResultOrErrorFuture<Boolean> future =
        ResultOrErrorFutureFactory.supply(() -> true);
    when(base.getVerifyFunction()).thenReturn((a, t) -> future);

    final AccountAsyncTemplate accountAsyncTemplate = supplyAccountAsyncTemplate(base);

    final Account account = mock(Account.class);
    final Transaction transaction = mock(Transaction.class);
    final ResultOrErrorFuture<Boolean> verifyResultFuture =
        accountAsyncTemplate.verify(account, transaction);
    assertTrue(verifyResultFuture.get().hasResult());
    assertEquals(ACCOUNT_VERIFY_ASYNC,
        ((WithIdentity) accountAsyncTemplate.getVerifyFunction()).getIdentity());
  }

}
