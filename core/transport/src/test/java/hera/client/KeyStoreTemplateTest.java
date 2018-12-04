/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.TransportConstants.KEYSTORE_CREATE;
import static hera.TransportConstants.KEYSTORE_EXPORTKEY;
import static hera.TransportConstants.KEYSTORE_IMPORTKEY;
import static hera.TransportConstants.KEYSTORE_LIST;
import static hera.TransportConstants.KEYSTORE_LOCK;
import static hera.TransportConstants.KEYSTORE_UNLOCK;
import static hera.api.model.BytesValue.of;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
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

@PrepareForTest({KeyStoreBaseTemplate.class, Account.class, EncryptedPrivateKey.class})
public class KeyStoreTemplateTest extends AbstractTestCase {

  protected static final EncryptedPrivateKey ENCRYPTED_PRIVATE_KEY =
      new EncryptedPrivateKey(of(new byte[] {EncryptedPrivateKey.VERSION}));

  protected static final AccountAddress ADDRESS =
      new AccountAddress(of(new byte[] {AccountAddress.VERSION}));

  protected static final String PASSWORD = randomUUID().toString();

  protected KeyStoreTemplate supplyKeyStoreTemplate(
      final KeyStoreBaseTemplate keyStoreBaseTemplate) {
    final KeyStoreTemplate keyStoreTemplate = new KeyStoreTemplate();
    keyStoreTemplate.keyStoreBaseTemplate = keyStoreBaseTemplate;
    keyStoreTemplate.setContextProvider(() -> context);
    return keyStoreTemplate;
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

    final KeyStoreTemplate keyStoreTemplate = supplyKeyStoreTemplate(base);

    final List<AccountAddress> accountList = keyStoreTemplate.list();
    assertNotNull(accountList);
    assertEquals(KEYSTORE_LIST, ((WithIdentity) keyStoreTemplate.getListFunction()).getIdentity());
  }

  @Test
  public void testCreate() {
    final KeyStoreBaseTemplate base = mock(KeyStoreBaseTemplate.class);
    final Account mockAccount = mock(Account.class);
    final ResultOrErrorFuture<Account> future =
        ResultOrErrorFutureFactory.supply(() -> mockAccount);
    when(base.getCreateFunction()).thenReturn((p) -> future);

    final KeyStoreTemplate keyStoreTemplate = supplyKeyStoreTemplate(base);

    final Account account =
        keyStoreTemplate.create(randomUUID().toString());
    assertNotNull(account);
    assertEquals(KEYSTORE_CREATE,
        ((WithIdentity) keyStoreTemplate.getCreateFunction()).getIdentity());
  }

  @Test
  public void testLock() {
    final KeyStoreBaseTemplate base = mock(KeyStoreBaseTemplate.class);
    final ResultOrErrorFuture<Boolean> future =
        ResultOrErrorFutureFactory.supply(() -> true);
    when(base.getLockFunction()).thenReturn((a) -> future);

    final KeyStoreTemplate keyStoreTemplate = supplyKeyStoreTemplate(base);

    final Boolean lockResult =
        keyStoreTemplate.lock(Authentication.of(ADDRESS, PASSWORD));
    assertNotNull(lockResult);
    assertEquals(KEYSTORE_LOCK, ((WithIdentity) keyStoreTemplate.getLockFunction()).getIdentity());
  }

  @Test
  public void testUnlock() {
    final KeyStoreBaseTemplate base = mock(KeyStoreBaseTemplate.class);
    final ResultOrErrorFuture<Boolean> future =
        ResultOrErrorFutureFactory.supply(() -> true);
    when(base.getUnlockFunction()).thenReturn((a) -> future);

    final KeyStoreTemplate keyStoreTemplate = supplyKeyStoreTemplate(base);

    final boolean account =
        keyStoreTemplate.unlock(Authentication.of(ADDRESS, PASSWORD));
    assertNotNull(account);
    assertEquals(KEYSTORE_UNLOCK,
        ((WithIdentity) keyStoreTemplate.getUnlockFunction()).getIdentity());
  }

  @Test
  public void testImportKey() {
    final KeyStoreBaseTemplate base = mock(KeyStoreBaseTemplate.class);
    final Account mockAccount = mock(Account.class);
    final ResultOrErrorFuture<Account> future =
        ResultOrErrorFutureFactory.supply(() -> mockAccount);
    when(base.getImportKeyFunction()).thenReturn((k, op, np) -> future);

    final KeyStoreTemplate keyStoreTemplate = supplyKeyStoreTemplate(base);

    final Account account =
        keyStoreTemplate.importKey(ENCRYPTED_PRIVATE_KEY, PASSWORD);
    assertNotNull(account);
    assertEquals(KEYSTORE_IMPORTKEY,
        ((WithIdentity) keyStoreTemplate.getImportKeyFunction()).getIdentity());
  }

  @Test
  public void testExportKey() {
    final KeyStoreBaseTemplate base = mock(KeyStoreBaseTemplate.class);
    final EncryptedPrivateKey mockEncryptedKey = mock(EncryptedPrivateKey.class);
    final ResultOrErrorFuture<EncryptedPrivateKey> future =
        ResultOrErrorFutureFactory.supply(() -> mockEncryptedKey);
    when(base.getExportKeyFunction()).thenReturn((a) -> future);

    final KeyStoreTemplate keyStoreTemplate = supplyKeyStoreTemplate(base);

    final EncryptedPrivateKey account =
        keyStoreTemplate.exportKey(Authentication.of(ADDRESS, PASSWORD));
    assertNotNull(account);
    assertEquals(KEYSTORE_EXPORTKEY,
        ((WithIdentity) keyStoreTemplate.getExportKeyFunction()).getIdentity());
  }

}
