/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.TransportConstants.ACCOUNT_CREATE_ASYNC;
import static hera.TransportConstants.ACCOUNT_EXPORTKEY_ASYNC;
import static hera.TransportConstants.ACCOUNT_GETSTATE_ASYNC;
import static hera.TransportConstants.ACCOUNT_IMPORTKEY_ASYNC;
import static hera.TransportConstants.ACCOUNT_LIST_ASYNC;
import static hera.TransportConstants.ACCOUNT_LOCK_ASYNC;
import static hera.TransportConstants.ACCOUNT_UNLOCK_ASYNC;
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
import hera.api.model.Authentication;
import hera.api.model.EncryptedPrivateKey;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.api.tupleorerror.ResultOrErrorFutureFactory;
import hera.api.tupleorerror.WithIdentity;
import java.util.ArrayList;
import java.util.List;
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
  public void testList() {
    final AccountBaseTemplate base = mock(AccountBaseTemplate.class);
    final ResultOrErrorFuture<List<AccountAddress>> future =
        ResultOrErrorFutureFactory.supply(() -> new ArrayList<AccountAddress>());
    when(base.getListFunction()).thenReturn(() -> future);

    final AccountAsyncTemplate accountAsyncTemplate = supplyAccountAsyncTemplate(base);

    final ResultOrErrorFuture<List<AccountAddress>> accountListFuture = accountAsyncTemplate.list();
    assertTrue(accountListFuture.get().hasResult());
    assertEquals(ACCOUNT_LIST_ASYNC,
        ((WithIdentity) accountAsyncTemplate.getListFunction()).getIdentity());
  }

  @Test
  public void testCreate() {
    final AccountBaseTemplate base = mock(AccountBaseTemplate.class);
    final Account mockAccount = mock(Account.class);
    final ResultOrErrorFuture<Account> future =
        ResultOrErrorFutureFactory.supply(() -> mockAccount);
    when(base.getCreateFunction()).thenReturn((p) -> future);

    final AccountAsyncTemplate accountAsyncTemplate = supplyAccountAsyncTemplate(base);

    final ResultOrErrorFuture<Account> accountFuture =
        accountAsyncTemplate.create(randomUUID().toString());
    assertTrue(accountFuture.get().hasResult());
    assertEquals(ACCOUNT_CREATE_ASYNC,
        ((WithIdentity) accountAsyncTemplate.getCreateFunction()).getIdentity());
  }

  @Test
  public void testGetState() {
    final AccountBaseTemplate base = mock(AccountBaseTemplate.class);
    final AccountState mockState = new AccountState();
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
  public void testLock() {
    final AccountBaseTemplate base = mock(AccountBaseTemplate.class);
    final ResultOrErrorFuture<Boolean> future =
        ResultOrErrorFutureFactory.supply(() -> true);
    when(base.getLockFunction()).thenReturn((a) -> future);

    final AccountAsyncTemplate accountAsyncTemplate = supplyAccountAsyncTemplate(base);

    final ResultOrErrorFuture<Boolean> lockResult =
        accountAsyncTemplate.lock(Authentication.of(ACCOUNT_ADDRESS, PASSWORD));
    assertTrue(lockResult.get().hasResult());
    assertEquals(ACCOUNT_LOCK_ASYNC,
        ((WithIdentity) accountAsyncTemplate.getLockFunction()).getIdentity());
  }

  @Test
  public void testUnlock() {
    final AccountBaseTemplate base = mock(AccountBaseTemplate.class);
    final ResultOrErrorFuture<Boolean> future =
        ResultOrErrorFutureFactory.supply(() -> true);
    when(base.getUnlockFunction()).thenReturn((a) -> future);

    final AccountAsyncTemplate accountAsyncTemplate = supplyAccountAsyncTemplate(base);

    final ResultOrErrorFuture<Boolean> accountFuture =
        accountAsyncTemplate.unlock(Authentication.of(ACCOUNT_ADDRESS, PASSWORD));
    assertTrue(accountFuture.get().hasResult());
    assertEquals(ACCOUNT_UNLOCK_ASYNC,
        ((WithIdentity) accountAsyncTemplate.getUnlockFunction()).getIdentity());
  }

  @Test
  public void testImportKey() {
    final AccountBaseTemplate base = mock(AccountBaseTemplate.class);
    final Account account = mock(Account.class);
    final ResultOrErrorFuture<Account> future = ResultOrErrorFutureFactory.supply(() -> account);
    when(base.getImportKeyFunction()).thenReturn((k, op, np) -> future);

    final AccountAsyncTemplate accountAsyncTemplate = supplyAccountAsyncTemplate(base);

    final ResultOrErrorFuture<Account> accountFuture =
        accountAsyncTemplate.importKey(ENCRYPTED_PRIVATE_KEY, PASSWORD);
    assertTrue(accountFuture.get().hasResult());
    assertEquals(ACCOUNT_IMPORTKEY_ASYNC,
        ((WithIdentity) accountAsyncTemplate.getImportKeyFunction()).getIdentity());
  }

  @Test
  public void testExportKey() {
    final AccountBaseTemplate base = mock(AccountBaseTemplate.class);
    final EncryptedPrivateKey mockEncryptedKey = mock(EncryptedPrivateKey.class);
    final ResultOrErrorFuture<EncryptedPrivateKey> future =
        ResultOrErrorFutureFactory.supply(() -> mockEncryptedKey);
    when(base.getExportKeyFunction()).thenReturn((a) -> future);

    final AccountAsyncTemplate accountAsyncTemplate = supplyAccountAsyncTemplate(base);

    final ResultOrErrorFuture<EncryptedPrivateKey> accountFuture =
        accountAsyncTemplate.exportKey(Authentication.of(ACCOUNT_ADDRESS, PASSWORD));
    assertTrue(accountFuture.get().hasResult());
    assertEquals(ACCOUNT_EXPORTKEY_ASYNC,
        ((WithIdentity) accountAsyncTemplate.getExportKeyFunction()).getIdentity());
  }

}
