/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.TransportConstants.ACCOUNT_GETSTATE_EITHER;
import static hera.TransportConstants.ACCOUNT_SIGN_EITHER;
import static hera.TransportConstants.ACCOUNT_VERIFY_EITHER;
import static hera.api.model.BytesValue.of;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import hera.AbstractTestCase;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.AccountState;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.api.tupleorerror.ResultOrError;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.api.tupleorerror.ResultOrErrorFutureFactory;
import hera.api.tupleorerror.WithIdentity;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

@PrepareForTest({AccountBaseTemplate.class})
public class AccountEitherTemplateTest extends AbstractTestCase {

  protected static final EncryptedPrivateKey ENCRYPTED_PRIVATE_KEY =
      new EncryptedPrivateKey(of(new byte[] {EncryptedPrivateKey.VERSION}));

  protected static final AccountAddress ACCOUNT_ADDRESS =
      new AccountAddress(of(new byte[] {AccountAddress.VERSION}));

  protected static final String PASSWORD = randomUUID().toString();

  protected AccountEitherTemplate supplyAccountEitherTemplate(
      final AccountBaseTemplate accountBaseTemplate) {
    final AccountEitherTemplate accountEitherTemplate = new AccountEitherTemplate();
    accountEitherTemplate.accountBaseTemplate = accountBaseTemplate;
    accountEitherTemplate.setContextProvider(() -> context);
    return accountEitherTemplate;
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

    final AccountEitherTemplate accountEitherTemplate = supplyAccountEitherTemplate(base);

    final ResultOrError<AccountState> accountState =
        accountEitherTemplate.getState(ACCOUNT_ADDRESS);
    assertTrue(accountState.hasResult());
    assertEquals(ACCOUNT_GETSTATE_EITHER,
        ((WithIdentity) accountEitherTemplate.getStateFunction()).getIdentity());
  }

  @Test
  public void testSign() {
    final AccountBaseTemplate base = mock(AccountBaseTemplate.class);
    final Signature mockSignature = new Signature();
    final ResultOrErrorFuture<Signature> future =
        ResultOrErrorFutureFactory.supply(() -> mockSignature);
    when(base.getSignFunction()).thenReturn((a, t) -> future);

    final AccountEitherTemplate accountEitherTemplate = supplyAccountEitherTemplate(base);

    final Account account = mock(Account.class);
    final Transaction transaction = mock(Transaction.class);
    final ResultOrError<Signature> accountStateFuture =
        accountEitherTemplate.sign(account, transaction);
    assertTrue(accountStateFuture.hasResult());
    assertEquals(ACCOUNT_SIGN_EITHER,
        ((WithIdentity) accountEitherTemplate.getSignFunction()).getIdentity());
  }

  @Test
  public void testVerify() {
    final AccountBaseTemplate base = mock(AccountBaseTemplate.class);
    final ResultOrErrorFuture<Boolean> future =
        ResultOrErrorFutureFactory.supply(() -> true);
    when(base.getVerifyFunction()).thenReturn((a, t) -> future);

    final AccountEitherTemplate accountEitherTemplate = supplyAccountEitherTemplate(base);

    final Account account = mock(Account.class);
    final Transaction transaction = mock(Transaction.class);
    final ResultOrError<Boolean> accountStateFuture =
        accountEitherTemplate.verify(account, transaction);
    assertTrue(accountStateFuture.hasResult());
    assertEquals(ACCOUNT_VERIFY_EITHER,
        ((WithIdentity) accountEitherTemplate.getVerifyFunction()).getIdentity());
  }

}
