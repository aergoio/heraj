/*
 * @copyright defined in LICENSE.txt
 */

package hera.keystore;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hera.AbstractTestCase;
import hera.api.KeyStoreOperation;
import hera.api.model.AccountAddress;
import hera.api.model.Authentication;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.Identity;
import hera.client.AergoClient;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import hera.model.KeyAlias;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

public class ServerKeyStoreTest extends AbstractTestCase {

  protected static final AccountAddress accountAddress =
      new AergoKeyGenerator().create().getAddress();

  @Test
  public void testSave() {
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getKeyStoreOperation()).thenReturn(mock(KeyStoreOperation.class));

    final ServerKeyStore keyStore = new ServerKeyStore();
    keyStore.setClient(mockClient);
    final AergoKey key = new AergoKeyGenerator().create();
    final String password = randomUUID().toString();
    final Authentication authentication = Authentication.of(key.getAddress(), password);
    keyStore.save(authentication, key);
  }

  @Test
  public void shouldThrowErrorOnAliasIdentity() {
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getKeyStoreOperation()).thenReturn(mock(KeyStoreOperation.class));

    final ServerKeyStore keyStore = new ServerKeyStore();
    keyStore.setClient(mockClient);
    final AergoKey key = new AergoKeyGenerator().create();
    final Identity identity = new KeyAlias(randomUUID().toString());
    final String password = randomUUID().toString();
    final Authentication authentication = Authentication.of(identity, password);

    try {
      keyStore.save(authentication, key);
      fail();
    } catch (Exception e) {
      // good we expected this
    }
  }

  @Test
  public void testExport() {
    final AergoClient mockClient = mock(AergoClient.class);
    final KeyStoreOperation mockKeyStoreOperation = mock(KeyStoreOperation.class);
    when(mockKeyStoreOperation.exportKey(any(Authentication.class)))
        .thenReturn(mock(EncryptedPrivateKey.class));
    when(mockClient.getKeyStoreOperation()).thenReturn(mockKeyStoreOperation);

    final ServerKeyStore keyStore = new ServerKeyStore();
    keyStore.setClient(mockClient);
    final Authentication authentication =
        Authentication.of(accountAddress, randomUUID().toString());
    final EncryptedPrivateKey exported = keyStore.export(authentication);
    assertNotNull(exported);
  }

  @Test
  public void testListStoredAddresses() {
    final AergoClient mockClient = mock(AergoClient.class);
    final KeyStoreOperation mockKeyStoreOperation = mock(KeyStoreOperation.class);
    when(mockKeyStoreOperation.list()).thenReturn(new ArrayList<AccountAddress>());
    when(mockClient.getKeyStoreOperation()).thenReturn(mockKeyStoreOperation);

    final ServerKeyStore keyStore = new ServerKeyStore();
    keyStore.setClient(mockClient);
    final List<Identity> list = keyStore.listIdentities();
    assertNotNull(list);
  }

  @Test
  public void testUnlock() {
    final AergoClient mockClient = mock(AergoClient.class);
    final KeyStoreOperation mockKeyStoreOperation = mock(KeyStoreOperation.class);
    when(mockKeyStoreOperation.unlock(any(Authentication.class))).thenReturn(true);
    when(mockClient.getKeyStoreOperation()).thenReturn(mockKeyStoreOperation);

    final ServerKeyStore keyStore = new ServerKeyStore();
    keyStore.setClient(mockClient);
    final Authentication authentication =
        Authentication.of(accountAddress, randomUUID().toString());
    final boolean result = keyStore.unlock(authentication);
    assertTrue(result);
  }

  @Test
  public void testLock() {
    final AergoClient mockClient = mock(AergoClient.class);
    final KeyStoreOperation mockKeyStoreOperation = mock(KeyStoreOperation.class);
    when(mockKeyStoreOperation.lock(any(Authentication.class))).thenReturn(true);
    when(mockClient.getKeyStoreOperation()).thenReturn(mockKeyStoreOperation);

    final ServerKeyStore keyStore = new ServerKeyStore();
    keyStore.setClient(mockClient);
    final Authentication authentication =
        Authentication.of(accountAddress, randomUUID().toString());
    final boolean result = keyStore.lock(authentication);
    assertTrue(result);
  }

}
