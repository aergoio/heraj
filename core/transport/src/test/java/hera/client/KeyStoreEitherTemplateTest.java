/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.TransportConstants.KEYSTORE_CREATE_EITHER;
import static hera.TransportConstants.KEYSTORE_EXPORTKEY_EITHER;
import static hera.TransportConstants.KEYSTORE_IMPORTKEY_EITHER;
import static hera.TransportConstants.KEYSTORE_LIST_EITHER;
import static hera.TransportConstants.KEYSTORE_LOCK_EITHER;
import static hera.TransportConstants.KEYSTORE_UNLOCK_EITHER;
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
import hera.api.tupleorerror.ResultOrError;
import hera.api.tupleorerror.ResultOrErrorFuture;
import hera.api.tupleorerror.ResultOrErrorFutureFactory;
import hera.api.tupleorerror.WithIdentity;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import org.powermock.core.classloader.annotations.PrepareForTest;

@PrepareForTest({KeyStoreBaseTemplate.class})
public class KeyStoreEitherTemplateTest extends AbstractTestCase {

  protected static final EncryptedPrivateKey ENCRYPTED_PRIVATE_KEY =
      new EncryptedPrivateKey(of(new byte[] {EncryptedPrivateKey.VERSION}));

  protected static final AccountAddress ADDRESS =
      new AccountAddress(of(new byte[] {AccountAddress.VERSION}));

  protected static final String PASSWORD = randomUUID().toString();

  protected KeyStoreEitherTemplate supplyKeyStoreEitherTemplate(
      final KeyStoreBaseTemplate keyStoreBaseTemplate) {
    final KeyStoreEitherTemplate keyStoreEitherTemplate = new KeyStoreEitherTemplate();
    keyStoreEitherTemplate.keyStoreBaseTemplate = keyStoreBaseTemplate;
    keyStoreEitherTemplate.setContextProvider(() -> context);
    return keyStoreEitherTemplate;
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

    final KeyStoreEitherTemplate keyStoreEitherTemplate = supplyKeyStoreEitherTemplate(base);

    final ResultOrError<List<AccountAddress>> accountList = keyStoreEitherTemplate.list();
    assertTrue(accountList.hasResult());
    assertEquals(KEYSTORE_LIST_EITHER,
        ((WithIdentity) keyStoreEitherTemplate.getListFunction()).getIdentity());
  }

  @Test
  public void testCreate() {
    final KeyStoreBaseTemplate base = mock(KeyStoreBaseTemplate.class);
    final Account mockAccount = mock(Account.class);
    final ResultOrErrorFuture<Account> future =
        ResultOrErrorFutureFactory.supply(() -> mockAccount);
    when(base.getCreateFunction()).thenReturn((p) -> future);

    final KeyStoreEitherTemplate keyStoreEitherTemplate = supplyKeyStoreEitherTemplate(base);

    final ResultOrError<Account> account =
        keyStoreEitherTemplate.create(randomUUID().toString());
    assertTrue(account.hasResult());
    assertEquals(KEYSTORE_CREATE_EITHER,
        ((WithIdentity) keyStoreEitherTemplate.getCreateFunction()).getIdentity());
  }

  @Test
  public void testLock() {
    final KeyStoreBaseTemplate base = mock(KeyStoreBaseTemplate.class);
    final ResultOrErrorFuture<Boolean> future =
        ResultOrErrorFutureFactory.supply(() -> true);
    when(base.getLockFunction()).thenReturn((a) -> future);

    final KeyStoreEitherTemplate keyStoreEitherTemplate = supplyKeyStoreEitherTemplate(base);

    final ResultOrError<Boolean> lockResult =
        keyStoreEitherTemplate.lock(Authentication.of(ADDRESS, PASSWORD));
    assertTrue(lockResult.hasResult());
    assertEquals(KEYSTORE_LOCK_EITHER,
        ((WithIdentity) keyStoreEitherTemplate.getLockFunction()).getIdentity());
  }

  @Test
  public void testUnlock() {
    final KeyStoreBaseTemplate base = mock(KeyStoreBaseTemplate.class);
    final ResultOrErrorFuture<Boolean> future =
        ResultOrErrorFutureFactory.supply(() -> true);
    when(base.getUnlockFunction()).thenReturn((a) -> future);

    final KeyStoreEitherTemplate keyStoreEitherTemplate = supplyKeyStoreEitherTemplate(base);

    final ResultOrError<Boolean> account =
        keyStoreEitherTemplate.unlock(Authentication.of(ADDRESS, PASSWORD));
    assertTrue(account.hasResult());
    assertEquals(KEYSTORE_UNLOCK_EITHER,
        ((WithIdentity) keyStoreEitherTemplate.getUnlockFunction()).getIdentity());
  }

  @Test
  public void testImportKey() {
    final KeyStoreBaseTemplate base = mock(KeyStoreBaseTemplate.class);
    final Account mockAccount = mock(Account.class);
    final ResultOrErrorFuture<Account> future =
        ResultOrErrorFutureFactory.supply(() -> mockAccount);
    when(base.getImportKeyFunction()).thenReturn((k, op, np) -> future);

    final KeyStoreEitherTemplate keyStoreEitherTemplate = supplyKeyStoreEitherTemplate(base);

    final ResultOrError<Account> account =
        keyStoreEitherTemplate.importKey(ENCRYPTED_PRIVATE_KEY, PASSWORD);
    assertTrue(account.hasResult());
    assertEquals(KEYSTORE_IMPORTKEY_EITHER,
        ((WithIdentity) keyStoreEitherTemplate.getImportKeyFunction()).getIdentity());
  }

  @Test
  public void testExportKey() {
    final KeyStoreBaseTemplate base = mock(KeyStoreBaseTemplate.class);
    final EncryptedPrivateKey mockEncryptedKey = mock(EncryptedPrivateKey.class);
    final ResultOrErrorFuture<EncryptedPrivateKey> future =
        ResultOrErrorFutureFactory.supply(() -> mockEncryptedKey);
    when(base.getExportKeyFunction()).thenReturn((a) -> future);

    final KeyStoreEitherTemplate keyStoreEitherTemplate = supplyKeyStoreEitherTemplate(base);

    final ResultOrError<EncryptedPrivateKey> account =
        keyStoreEitherTemplate.exportKey(Authentication.of(ADDRESS, PASSWORD));
    assertTrue(account.hasResult());
    assertEquals(KEYSTORE_EXPORTKEY_EITHER,
        ((WithIdentity) keyStoreEitherTemplate.getExportKeyFunction()).getIdentity());
  }

}
