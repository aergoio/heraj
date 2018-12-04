/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.TransportConstants.ACCOUNT_CREATE;
import static hera.TransportConstants.ACCOUNT_EXPORTKEY;
import static hera.TransportConstants.ACCOUNT_GETSTATE;
import static hera.TransportConstants.ACCOUNT_IMPORTKEY;
import static hera.TransportConstants.ACCOUNT_LIST;
import static hera.TransportConstants.ACCOUNT_LOCK;
import static hera.TransportConstants.ACCOUNT_UNLOCK;
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
import hera.api.model.Authentication;
import hera.api.model.EncryptedPrivateKey;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.api.tupleorerror.ResultOrErrorFutureFactory;
import hera.api.tupleorerror.WithIdentity;
import java.util.ArrayList;
import java.util.List;
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
  public void testList() {
    final AccountBaseTemplate base = mock(AccountBaseTemplate.class);
    final ResultOrErrorFuture<List<AccountAddress>> future =
        ResultOrErrorFutureFactory.supply(() -> new ArrayList<AccountAddress>());
    when(base.getListFunction()).thenReturn(() -> future);

    final AccountTemplate accountTemplate = supplyAccountTemplate(base);

    final List<AccountAddress> accountList = accountTemplate.list();
    assertNotNull(accountList);
    assertEquals(ACCOUNT_LIST, ((WithIdentity) accountTemplate.getListFunction()).getIdentity());
  }

  @Test
  public void testCreate() {
    final AccountBaseTemplate base = mock(AccountBaseTemplate.class);
    final Account mockAccount = mock(Account.class);
    final ResultOrErrorFuture<Account> future =
        ResultOrErrorFutureFactory.supply(() -> mockAccount);
    when(base.getCreateFunction()).thenReturn((p) -> future);

    final AccountTemplate accountTemplate = supplyAccountTemplate(base);

    final Account account =
        accountTemplate.create(randomUUID().toString());
    assertNotNull(account);
    assertEquals(ACCOUNT_CREATE,
        ((WithIdentity) accountTemplate.getCreateFunction()).getIdentity());
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
  public void testLock() {
    final AccountBaseTemplate base = mock(AccountBaseTemplate.class);
    final ResultOrErrorFuture<Boolean> future =
        ResultOrErrorFutureFactory.supply(() -> true);
    when(base.getLockFunction()).thenReturn((a) -> future);

    final AccountTemplate accountTemplate = supplyAccountTemplate(base);

    final Boolean lockResult =
        accountTemplate.lock(Authentication.of(ACCOUNT_ADDRESS, PASSWORD));
    assertNotNull(lockResult);
    assertEquals(ACCOUNT_LOCK, ((WithIdentity) accountTemplate.getLockFunction()).getIdentity());
  }

  @Test
  public void testUnlock() {
    final AccountBaseTemplate base = mock(AccountBaseTemplate.class);
    final ResultOrErrorFuture<Boolean> future =
        ResultOrErrorFutureFactory.supply(() -> true);
    when(base.getUnlockFunction()).thenReturn((a) -> future);

    final AccountTemplate accountTemplate = supplyAccountTemplate(base);

    final boolean account =
        accountTemplate.unlock(Authentication.of(ACCOUNT_ADDRESS, PASSWORD));
    assertNotNull(account);
    assertEquals(ACCOUNT_UNLOCK,
        ((WithIdentity) accountTemplate.getUnlockFunction()).getIdentity());
  }

  @Test
  public void testImportKey() {
    final AccountBaseTemplate base = mock(AccountBaseTemplate.class);
    final Account mockAccount = mock(Account.class);
    final ResultOrErrorFuture<Account> future =
        ResultOrErrorFutureFactory.supply(() -> mockAccount);
    when(base.getImportKeyFunction()).thenReturn((k, op, np) -> future);

    final AccountTemplate accountTemplate = supplyAccountTemplate(base);

    final Account account =
        accountTemplate.importKey(ENCRYPTED_PRIVATE_KEY, PASSWORD);
    assertNotNull(account);
    assertEquals(ACCOUNT_IMPORTKEY,
        ((WithIdentity) accountTemplate.getImportKeyFunction()).getIdentity());
  }

  @Test
  public void testExportKey() {
    final AccountBaseTemplate base = mock(AccountBaseTemplate.class);
    final EncryptedPrivateKey mockEncryptedKey = mock(EncryptedPrivateKey.class);
    final ResultOrErrorFuture<EncryptedPrivateKey> future =
        ResultOrErrorFutureFactory.supply(() -> mockEncryptedKey);
    when(base.getExportKeyFunction()).thenReturn((a) -> future);

    final AccountTemplate accountTemplate = supplyAccountTemplate(base);

    final EncryptedPrivateKey account =
        accountTemplate.exportKey(Authentication.of(ACCOUNT_ADDRESS, PASSWORD));
    assertNotNull(account);
    assertEquals(ACCOUNT_EXPORTKEY,
        ((WithIdentity) accountTemplate.getExportKeyFunction()).getIdentity());
  }

}
