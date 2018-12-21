/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.TransportConstants.KEYSTORE_CREATE_ASYNC;
import static hera.TransportConstants.KEYSTORE_EXPORTKEY_ASYNC;
import static hera.TransportConstants.KEYSTORE_IMPORTKEY_ASYNC;
import static hera.TransportConstants.KEYSTORE_LIST_ASYNC;
import static hera.TransportConstants.KEYSTORE_LOCK_ASYNC;
import static hera.TransportConstants.KEYSTORE_UNLOCK_ASYNC;
import static hera.api.model.BytesValue.of;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import hera.AbstractTestCase;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.Authentication;
import hera.api.model.EncryptedPrivateKey;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.api.tupleorerror.ResultOrErrorFutureFactory;
import hera.api.tupleorerror.WithIdentity;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

@PrepareForTest({KeyStoreBaseTemplate.class})
public class KeyStoreAsyncTemplateTest extends AbstractTestCase {

  protected static final EncryptedPrivateKey ENCRYPTED_PRIVATE_KEY =
      new EncryptedPrivateKey(of(new byte[] {EncryptedPrivateKey.VERSION}));

  protected static final AccountAddress ADDRESS =
      new AccountAddress(of(new byte[] {AccountAddress.VERSION}));

  protected static final String PASSWORD = randomUUID().toString();

  protected KeyStoreAsyncTemplate supplyKeyStoreAsyncTemplate(
      final KeyStoreBaseTemplate keyStoreBaseTemplate) {
    final KeyStoreAsyncTemplate keyStoreAsyncTemplate = new KeyStoreAsyncTemplate();
    keyStoreAsyncTemplate.keyStoreBaseTemplate = keyStoreBaseTemplate;
    keyStoreAsyncTemplate.setContextProvider(() -> context);
    return keyStoreAsyncTemplate;
  }

  @Override
  public void setUp() {
    super.setUp();
  }

  @Test
  public void testList() {
    final KeyStoreBaseTemplate base = mock(KeyStoreBaseTemplate.class);
    final ResultOrErrorFuture<List<AccountAddress>> future =
        ResultOrErrorFutureFactory.supply(() -> new ArrayList<AccountAddress>());
    when(base.getListFunction()).thenReturn(() -> future);

    final KeyStoreAsyncTemplate keyStoreAsyncTemplate = supplyKeyStoreAsyncTemplate(base);

    final ResultOrErrorFuture<List<AccountAddress>> accountListFuture =
        keyStoreAsyncTemplate.list();
    assertTrue(accountListFuture.get().hasResult());
    assertEquals(KEYSTORE_LIST_ASYNC,
        ((WithIdentity) keyStoreAsyncTemplate.getListFunction()).getIdentity());
  }

  @Test
  public void testCreate() {
    final KeyStoreBaseTemplate base = mock(KeyStoreBaseTemplate.class);
    final Account mockAccount = mock(Account.class);
    final ResultOrErrorFuture<Account> future =
        ResultOrErrorFutureFactory.supply(() -> mockAccount);
    when(base.getCreateFunction()).thenReturn((p) -> future);

    final KeyStoreAsyncTemplate keyStoreAsyncTemplate = supplyKeyStoreAsyncTemplate(base);

    final ResultOrErrorFuture<Account> accountFuture =
        keyStoreAsyncTemplate.create(randomUUID().toString());
    assertTrue(accountFuture.get().hasResult());
    assertEquals(KEYSTORE_CREATE_ASYNC,
        ((WithIdentity) keyStoreAsyncTemplate.getCreateFunction()).getIdentity());
  }

  @Test
  public void testLock() {
    final KeyStoreBaseTemplate base = mock(KeyStoreBaseTemplate.class);
    final ResultOrErrorFuture<Boolean> future =
        ResultOrErrorFutureFactory.supply(() -> true);
    when(base.getLockFunction()).thenReturn((a) -> future);

    final KeyStoreAsyncTemplate keyStoreAsyncTemplate = supplyKeyStoreAsyncTemplate(base);

    final ResultOrErrorFuture<Boolean> lockResult =
        keyStoreAsyncTemplate.lock(Authentication.of(ADDRESS, PASSWORD));
    assertTrue(lockResult.get().hasResult());
    assertEquals(KEYSTORE_LOCK_ASYNC,
        ((WithIdentity) keyStoreAsyncTemplate.getLockFunction()).getIdentity());
  }

  @Test
  public void testUnlock() {
    final KeyStoreBaseTemplate base = mock(KeyStoreBaseTemplate.class);
    final ResultOrErrorFuture<Boolean> future =
        ResultOrErrorFutureFactory.supply(() -> true);
    when(base.getUnlockFunction()).thenReturn((a) -> future);

    final KeyStoreAsyncTemplate keyStoreAsyncTemplate = supplyKeyStoreAsyncTemplate(base);

    final ResultOrErrorFuture<Boolean> accountFuture =
        keyStoreAsyncTemplate.unlock(Authentication.of(ADDRESS, PASSWORD));
    assertTrue(accountFuture.get().hasResult());
    assertEquals(KEYSTORE_UNLOCK_ASYNC,
        ((WithIdentity) keyStoreAsyncTemplate.getUnlockFunction()).getIdentity());
  }

  @Test
  public void testImportKey() {
    final KeyStoreBaseTemplate base = mock(KeyStoreBaseTemplate.class);
    final Account account = mock(Account.class);
    final ResultOrErrorFuture<Account> future = ResultOrErrorFutureFactory.supply(() -> account);
    when(base.getImportKeyFunction()).thenReturn((k, op, np) -> future);

    final KeyStoreAsyncTemplate keyStoreAsyncTemplate = supplyKeyStoreAsyncTemplate(base);

    final ResultOrErrorFuture<Account> accountFuture =
        keyStoreAsyncTemplate.importKey(ENCRYPTED_PRIVATE_KEY, PASSWORD, PASSWORD);
    assertTrue(accountFuture.get().hasResult());
    assertEquals(KEYSTORE_IMPORTKEY_ASYNC,
        ((WithIdentity) keyStoreAsyncTemplate.getImportKeyFunction()).getIdentity());
  }

  @Test
  public void testExportKey() {
    final KeyStoreBaseTemplate base = mock(KeyStoreBaseTemplate.class);
    final EncryptedPrivateKey mockEncryptedKey = mock(EncryptedPrivateKey.class);
    final ResultOrErrorFuture<EncryptedPrivateKey> future =
        ResultOrErrorFutureFactory.supply(() -> mockEncryptedKey);
    when(base.getExportKeyFunction()).thenReturn((a) -> future);

    final KeyStoreAsyncTemplate keyStoreAsyncTemplate = supplyKeyStoreAsyncTemplate(base);

    final ResultOrErrorFuture<EncryptedPrivateKey> accountFuture =
        keyStoreAsyncTemplate.exportKey(Authentication.of(ADDRESS, PASSWORD));
    assertTrue(accountFuture.get().hasResult());
    assertEquals(KEYSTORE_EXPORTKEY_ASYNC,
        ((WithIdentity) keyStoreAsyncTemplate.getExportKeyFunction()).getIdentity());
  }

}
