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
import hera.Context;
import hera.ContextProvider;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.Authentication;
import hera.api.model.EncryptedPrivateKey;
import hera.api.tupleorerror.Function0;
import hera.api.tupleorerror.Function1;
import hera.api.tupleorerror.Function3;
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
    keyStoreTemplate.setContextProvider(new ContextProvider() {
      @Override
      public Context get() {
        return context;
      }
    });
    return keyStoreTemplate;
  }

  @Override
  public void setUp() {
    super.setUp();
  }

  @Test
  public void testList() {
    final KeyStoreBaseTemplate base = mock(KeyStoreBaseTemplate.class);
    final FinishableFuture<List<AccountAddress>> future =
        new FinishableFuture<List<AccountAddress>>();
    future.success(new ArrayList<AccountAddress>());
    when(base.getListFunction())
        .thenReturn(new Function0<FinishableFuture<List<AccountAddress>>>() {
          @Override
          public FinishableFuture<List<AccountAddress>> apply() {
            return future;
          }
        });

    final KeyStoreTemplate keyStoreTemplate = supplyKeyStoreTemplate(base);

    final List<AccountAddress> accountList = keyStoreTemplate.list();
    assertNotNull(accountList);
    assertEquals(KEYSTORE_LIST, ((WithIdentity) keyStoreTemplate.getListFunction()).getIdentity());
  }

  @Test
  public void testCreate() {
    final KeyStoreBaseTemplate base = mock(KeyStoreBaseTemplate.class);
    final Account mockAccount = mock(Account.class);
    final FinishableFuture<Account> future = new FinishableFuture<Account>();
    future.success(mockAccount);
    when(base.getCreateFunction()).thenReturn(new Function1<String, FinishableFuture<Account>>() {
      @Override
      public FinishableFuture<Account> apply(String t) {
        return future;
      }
    });

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
    final FinishableFuture<Boolean> future = new FinishableFuture<Boolean>();
    future.success(true);
    when(base.getLockFunction())
        .thenReturn(new Function1<Authentication, FinishableFuture<Boolean>>() {
          @Override
          public FinishableFuture<Boolean> apply(Authentication t) {
            return future;
          }
        });

    final KeyStoreTemplate keyStoreTemplate = supplyKeyStoreTemplate(base);

    final Boolean lockResult =
        keyStoreTemplate.lock(Authentication.of(ADDRESS, PASSWORD));
    assertNotNull(lockResult);
    assertEquals(KEYSTORE_LOCK, ((WithIdentity) keyStoreTemplate.getLockFunction()).getIdentity());
  }

  @Test
  public void testUnlock() {
    final KeyStoreBaseTemplate base = mock(KeyStoreBaseTemplate.class);
    final FinishableFuture<Boolean> future = new FinishableFuture<Boolean>();
    future.success(true);
    when(base.getUnlockFunction())
        .thenReturn(new Function1<Authentication, FinishableFuture<Boolean>>() {
          @Override
          public FinishableFuture<Boolean> apply(Authentication t) {
            return future;
          }
        });

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
    final FinishableFuture<Account> future = new FinishableFuture<Account>();
    future.success(mockAccount);
    when(base.getImportKeyFunction()).thenReturn(
        new Function3<EncryptedPrivateKey, String, String, FinishableFuture<Account>>() {
          @Override
          public FinishableFuture<Account> apply(EncryptedPrivateKey t1, String t2, String t3) {
            return future;
          }
        });

    final KeyStoreTemplate keyStoreTemplate = supplyKeyStoreTemplate(base);

    final Account account =
        keyStoreTemplate.importKey(ENCRYPTED_PRIVATE_KEY, PASSWORD, PASSWORD);
    assertNotNull(account);
    assertEquals(KEYSTORE_IMPORTKEY,
        ((WithIdentity) keyStoreTemplate.getImportKeyFunction()).getIdentity());
  }

  @Test
  public void testExportKey() {
    final KeyStoreBaseTemplate base = mock(KeyStoreBaseTemplate.class);
    final EncryptedPrivateKey mockEncryptedKey = mock(EncryptedPrivateKey.class);
    final FinishableFuture<EncryptedPrivateKey> future =
        new FinishableFuture<EncryptedPrivateKey>();
    future.success(mockEncryptedKey);
    when(base.getExportKeyFunction())
        .thenReturn(new Function1<Authentication, FinishableFuture<EncryptedPrivateKey>>() {
          @Override
          public FinishableFuture<EncryptedPrivateKey> apply(Authentication t) {
            return future;
          }
        });

    final KeyStoreTemplate keyStoreTemplate = supplyKeyStoreTemplate(base);

    final EncryptedPrivateKey account =
        keyStoreTemplate.exportKey(Authentication.of(ADDRESS, PASSWORD));
    assertNotNull(account);
    assertEquals(KEYSTORE_EXPORTKEY,
        ((WithIdentity) keyStoreTemplate.getExportKeyFunction()).getIdentity());
  }

}
