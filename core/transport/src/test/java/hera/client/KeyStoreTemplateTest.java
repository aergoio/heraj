/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static hera.api.model.BytesValue.of;
import static hera.client.ClientConstants.KEYSTORE_CREATE;
import static hera.client.ClientConstants.KEYSTORE_EXPORTKEY;
import static hera.client.ClientConstants.KEYSTORE_IMPORTKEY;
import static hera.client.ClientConstants.KEYSTORE_LIST;
import static hera.client.ClientConstants.KEYSTORE_LOCK;
import static hera.client.ClientConstants.KEYSTORE_SEND;
import static hera.client.ClientConstants.KEYSTORE_UNLOCK;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

import hera.AbstractTestCase;
import hera.ContextProvider;
import hera.api.function.Function0;
import hera.api.function.Function1;
import hera.api.function.Function3;
import hera.api.function.Function4;
import hera.api.function.WithIdentity;
import hera.api.model.AccountAddress;
import hera.api.model.Aer;
import hera.api.model.Aer.Unit;
import hera.api.model.Authentication;
import hera.api.model.BytesValue;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.TxHash;
import hera.client.internal.HerajFutures;
import hera.client.internal.KeyStoreBaseTemplate;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Future;
import org.junit.Test;
import org.mockito.Mockito;
import org.powermock.core.classloader.annotations.PrepareForTest;

@PrepareForTest({KeyStoreBaseTemplate.class, EncryptedPrivateKey.class})
public class KeyStoreTemplateTest extends AbstractTestCase {

  protected static final String PASSWORD = randomUUID().toString();

  protected KeyStoreTemplate supplyKeyStoreTemplate(
      final KeyStoreBaseTemplate keyStoreBaseTemplate) {
    final KeyStoreTemplate keyStoreTemplate = new KeyStoreTemplate();
    keyStoreTemplate.keyStoreBaseTemplate = keyStoreBaseTemplate;
    keyStoreTemplate.setContextProvider(ContextProvider.defaultProvider);
    return keyStoreTemplate;
  }

  @Override
  public void setUp() {
    super.setUp();
  }

  @Test
  public void testList() {
    final KeyStoreBaseTemplate base = mock(KeyStoreBaseTemplate.class);
    final List<AccountAddress> list = new ArrayList<AccountAddress>();
    final Future<List<AccountAddress>> future = HerajFutures.success(list);
    when(base.getListFunction())
        .thenReturn(new Function0<Future<List<AccountAddress>>>() {
          @Override
          public Future<List<AccountAddress>> apply() {
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
    final Future<AccountAddress> future = HerajFutures.success(AccountAddress.EMPTY);
    when(base.getCreateFunction())
        .thenReturn(new Function1<String, Future<AccountAddress>>() {
          @Override
          public Future<AccountAddress> apply(String t) {
            return future;
          }
        });

    final KeyStoreTemplate keyStoreTemplate = supplyKeyStoreTemplate(base);

    final AccountAddress account = keyStoreTemplate.create(randomUUID().toString());
    assertNotNull(account);
    assertEquals(KEYSTORE_CREATE,
        ((WithIdentity) keyStoreTemplate.getCreateFunction()).getIdentity());
  }

  @Test
  public void testLock() {
    final KeyStoreBaseTemplate base = mock(KeyStoreBaseTemplate.class);
    final Future<Boolean> future = HerajFutures.success(true);
    when(base.getLockFunction())
        .thenReturn(new Function1<Authentication, Future<Boolean>>() {
          @Override
          public Future<Boolean> apply(Authentication t) {
            return future;
          }
        });

    final KeyStoreTemplate keyStoreTemplate = supplyKeyStoreTemplate(base);

    final Boolean lockResult =
        keyStoreTemplate.lock(Authentication.of(accountAddress, PASSWORD));
    assertNotNull(lockResult);
    assertEquals(KEYSTORE_LOCK, ((WithIdentity) keyStoreTemplate.getLockFunction()).getIdentity());
  }

  @Test
  public void testUnlock() {
    final KeyStoreBaseTemplate base = mock(KeyStoreBaseTemplate.class);
    final Future<Boolean> future = HerajFutures.success(true);
    when(base.getUnlockFunction())
        .thenReturn(new Function1<Authentication, Future<Boolean>>() {
          @Override
          public Future<Boolean> apply(Authentication t) {
            return future;
          }
        });

    final KeyStoreTemplate keyStoreTemplate = supplyKeyStoreTemplate(base);

    final boolean account =
        keyStoreTemplate.unlock(Authentication.of(accountAddress, PASSWORD));
    assertNotNull(account);
    assertEquals(KEYSTORE_UNLOCK,
        ((WithIdentity) keyStoreTemplate.getUnlockFunction()).getIdentity());
  }

  @Test
  public void testImportKey() {
    final KeyStoreBaseTemplate base = mock(KeyStoreBaseTemplate.class);
    final Future<AccountAddress> future = HerajFutures.success(AccountAddress.EMPTY);
    when(base.getImportKeyFunction()).thenReturn(
        new Function3<EncryptedPrivateKey, String, String, Future<AccountAddress>>() {
          @Override
          public Future<AccountAddress> apply(EncryptedPrivateKey t1, String t2,
              String t3) {
            return future;
          }
        });

    final KeyStoreTemplate keyStoreTemplate = supplyKeyStoreTemplate(base);

    final AccountAddress account =
        keyStoreTemplate.importKey(encryptedPrivateKey, PASSWORD, PASSWORD);
    assertNotNull(account);
    assertEquals(KEYSTORE_IMPORTKEY,
        ((WithIdentity) keyStoreTemplate.getImportKeyFunction()).getIdentity());
  }

  @Test
  public void testExportKey() {
    final KeyStoreBaseTemplate base = mock(KeyStoreBaseTemplate.class);
    final EncryptedPrivateKey mockEncryptedKey = mock(EncryptedPrivateKey.class);
    final Future<EncryptedPrivateKey> future = HerajFutures.success(mockEncryptedKey);
    when(base.getExportKeyFunction())
        .thenReturn(new Function1<Authentication, Future<EncryptedPrivateKey>>() {
          @Override
          public Future<EncryptedPrivateKey> apply(Authentication t) {
            return future;
          }
        });

    final KeyStoreTemplate keyStoreTemplate = supplyKeyStoreTemplate(base);

    final EncryptedPrivateKey account =
        keyStoreTemplate.exportKey(Authentication.of(accountAddress, PASSWORD));
    assertNotNull(account);
    assertEquals(KEYSTORE_EXPORTKEY,
        ((WithIdentity) keyStoreTemplate.getExportKeyFunction()).getIdentity());
  }

  @Test
  public void testSend() {
    final KeyStoreBaseTemplate base = mock(KeyStoreBaseTemplate.class);
    final Future<TxHash> future =
        HerajFutures.success(new TxHash(of(randomUUID().toString().getBytes())));
    Mockito.when(base.getSendFunction())
        .thenReturn(
            new Function4<AccountAddress, AccountAddress, Aer, BytesValue, Future<TxHash>>() {
              @Override
              public Future<TxHash> apply(AccountAddress t1, AccountAddress t2, Aer t3,
                  BytesValue t4) {
                return future;
              }
            });

    final KeyStoreTemplate keyStoreTemplate = supplyKeyStoreTemplate(base);

    final TxHash txHash = keyStoreTemplate
        .send(accountAddress, accountAddress, Aer.of("10", Unit.AER), BytesValue.EMPTY);
    assertNotNull(txHash);
    assertEquals(KEYSTORE_SEND,
        ((WithIdentity) keyStoreTemplate.getSendFunction()).getIdentity());
  }

}
