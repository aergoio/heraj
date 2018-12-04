/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.TransportConstants.ACCOUNT_CREATE_EITHER;
import static hera.TransportConstants.ACCOUNT_EXPORTKEY_EITHER;
import static hera.TransportConstants.ACCOUNT_GETSTATE_EITHER;
import static hera.TransportConstants.ACCOUNT_IMPORTKEY_EITHER;
import static hera.TransportConstants.ACCOUNT_LIST_EITHER;
import static hera.TransportConstants.ACCOUNT_LOCK_EITHER;
import static hera.TransportConstants.ACCOUNT_UNLOCK_EITHER;
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
import hera.api.tupleorerror.ResultOrError;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.api.tupleorerror.ResultOrErrorFutureFactory;
import hera.api.tupleorerror.WithIdentity;
import java.util.ArrayList;
import java.util.List;
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
  public void testList() {
    final AccountBaseTemplate base = mock(AccountBaseTemplate.class);
    final ResultOrErrorFuture<List<AccountAddress>> future =
        ResultOrErrorFutureFactory.supply(() -> new ArrayList<AccountAddress>());
    when(base.getListFunction()).thenReturn(() -> future);

    final AccountEitherTemplate accountEitherTemplate = supplyAccountEitherTemplate(base);

    final ResultOrError<List<AccountAddress>> accountList = accountEitherTemplate.list();
    assertTrue(accountList.hasResult());
    assertEquals(ACCOUNT_LIST_EITHER,
        ((WithIdentity) accountEitherTemplate.getListFunction()).getIdentity());
  }

  @Test
  public void testCreate() {
    final AccountBaseTemplate base = mock(AccountBaseTemplate.class);
    final Account mockAccount = mock(Account.class);
    final ResultOrErrorFuture<Account> future =
        ResultOrErrorFutureFactory.supply(() -> mockAccount);
    when(base.getCreateFunction()).thenReturn((p) -> future);

    final AccountEitherTemplate accountEitherTemplate = supplyAccountEitherTemplate(base);

    final ResultOrError<Account> account =
        accountEitherTemplate.create(randomUUID().toString());
    assertTrue(account.hasResult());
    assertEquals(ACCOUNT_CREATE_EITHER,
        ((WithIdentity) accountEitherTemplate.getCreateFunction()).getIdentity());
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
  public void testLock() {
    final AccountBaseTemplate base = mock(AccountBaseTemplate.class);
    final ResultOrErrorFuture<Boolean> future =
        ResultOrErrorFutureFactory.supply(() -> true);
    when(base.getLockFunction()).thenReturn((a) -> future);

    final AccountEitherTemplate accountEitherTemplate = supplyAccountEitherTemplate(base);

    final ResultOrError<Boolean> lockResult =
        accountEitherTemplate.lock(Authentication.of(ACCOUNT_ADDRESS, PASSWORD));
    assertTrue(lockResult.hasResult());
    assertEquals(ACCOUNT_LOCK_EITHER,
        ((WithIdentity) accountEitherTemplate.getLockFunction()).getIdentity());
  }

  @Test
  public void testUnlock() {
    final AccountBaseTemplate base = mock(AccountBaseTemplate.class);
    final ResultOrErrorFuture<Boolean> future =
        ResultOrErrorFutureFactory.supply(() -> true);
    when(base.getUnlockFunction()).thenReturn((a) -> future);

    final AccountEitherTemplate accountEitherTemplate = supplyAccountEitherTemplate(base);

    final ResultOrError<Boolean> account =
        accountEitherTemplate.unlock(Authentication.of(ACCOUNT_ADDRESS, PASSWORD));
    assertTrue(account.hasResult());
    assertEquals(ACCOUNT_UNLOCK_EITHER,
        ((WithIdentity) accountEitherTemplate.getUnlockFunction()).getIdentity());
  }

  @Test
  public void testImportKey() {
    final AccountBaseTemplate base = mock(AccountBaseTemplate.class);
    final Account mockAccount = mock(Account.class);
    final ResultOrErrorFuture<Account> future =
        ResultOrErrorFutureFactory.supply(() -> mockAccount);
    when(base.getImportKeyFunction()).thenReturn((k, op, np) -> future);

    final AccountEitherTemplate accountEitherTemplate = supplyAccountEitherTemplate(base);

    final ResultOrError<Account> account =
        accountEitherTemplate.importKey(ENCRYPTED_PRIVATE_KEY, PASSWORD);
    assertTrue(account.hasResult());
    assertEquals(ACCOUNT_IMPORTKEY_EITHER,
        ((WithIdentity) accountEitherTemplate.getImportKeyFunction()).getIdentity());
  }

  @Test
  public void testExportKey() {
    final AccountBaseTemplate base = mock(AccountBaseTemplate.class);
    final EncryptedPrivateKey mockEncryptedKey = mock(EncryptedPrivateKey.class);
    final ResultOrErrorFuture<EncryptedPrivateKey> future =
        ResultOrErrorFutureFactory.supply(() -> mockEncryptedKey);
    when(base.getExportKeyFunction()).thenReturn((a) -> future);

    final AccountEitherTemplate accountEitherTemplate = supplyAccountEitherTemplate(base);

    final ResultOrError<EncryptedPrivateKey> account =
        accountEitherTemplate.exportKey(Authentication.of(ACCOUNT_ADDRESS, PASSWORD));
    assertTrue(account.hasResult());
    assertEquals(ACCOUNT_EXPORTKEY_EITHER,
        ((WithIdentity) accountEitherTemplate.getExportKeyFunction()).getIdentity());
  }

}
