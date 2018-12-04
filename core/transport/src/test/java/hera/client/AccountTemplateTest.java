/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.TransportConstants.ACCOUNT_GETSTATE;
import static hera.TransportConstants.ACCOUNT_SIGN;
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
import hera.api.model.AccountState;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.Signature;
import hera.api.model.Transaction;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.api.tupleorerror.ResultOrErrorFutureFactory;
import hera.api.tupleorerror.WithIdentity;
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
    final ResultOrErrorFuture<AccountState> future =
        ResultOrErrorFutureFactory.supply(() -> mockState);
    when(base.getStateFunction()).thenReturn((a) -> future);

    final AccountTemplate accountTemplate = supplyAccountTemplate(base);

    final AccountState accountState =
        accountTemplate.getState(ACCOUNT_ADDRESS);
    assertNotNull(accountState);
    assertEquals(ACCOUNT_GETSTATE,
        ((WithIdentity) accountTemplate.getStateFunction()).getIdentity());
  }

  @Test
  public void testSign() {
    final AccountBaseTemplate base = mock(AccountBaseTemplate.class);
    final Signature mockSignature = new Signature();
    final ResultOrErrorFuture<Signature> future =
        ResultOrErrorFutureFactory.supply(() -> mockSignature);
    when(base.getSignFunction()).thenReturn((a, t) -> future);

    final AccountTemplate accountTemplate = supplyAccountTemplate(base);

    final Account account = mock(Account.class);
    final Transaction transaction = mock(Transaction.class);
    final Signature accountStateFuture =
        accountTemplate.sign(account, transaction);
    assertNotNull(accountStateFuture);
    assertEquals(ACCOUNT_SIGN, ((WithIdentity) accountTemplate.getSignFunction()).getIdentity());
  }

  @Test
  public void testVerify() {
    final AccountBaseTemplate base = mock(AccountBaseTemplate.class);
    final ResultOrErrorFuture<Boolean> future =
        ResultOrErrorFutureFactory.supply(() -> true);
    when(base.getVerifyFunction()).thenReturn((a, t) -> future);

    final AccountTemplate accountTemplate = supplyAccountTemplate(base);

    final Account account = mock(Account.class);
    final Transaction transaction = mock(Transaction.class);
    final Boolean accountStateFuture =
        accountTemplate.verify(account, transaction);
    assertNotNull(accountStateFuture);
    assertEquals(ACCOUNT_VERIFY,
        ((WithIdentity) accountTemplate.getVerifyFunction()).getIdentity());
  }

}
