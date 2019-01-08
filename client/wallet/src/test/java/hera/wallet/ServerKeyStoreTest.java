/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hera.AbstractTestCase;
import hera.api.KeyStoreOperation;
import hera.api.model.Account;
import hera.api.model.AccountAddress;
import hera.api.model.Authentication;
import hera.api.model.EncryptedPrivateKey;
import hera.client.AergoClient;
import hera.key.AergoKeyGenerator;
import org.junit.Test;

public class ServerKeyStoreTest extends AbstractTestCase {

  protected static final AccountAddress ACCOUNT_ADDRESS =
      AccountAddress.of("AmLo9CGR3xFZPVKZ5moSVRNW1kyscY9rVkCvgrpwNJjRUPUWadC5");

  @Test
  public void testSave() {
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getKeyStoreOperation()).thenReturn(mock(KeyStoreOperation.class));

    final KeyStore keyStore = new ServerKeyStore(mockClient);
    keyStore.saveKey(new AergoKeyGenerator().create(), randomUUID().toString());
  }

  @Test
  public void testExport() {
    final AergoClient mockClient = mock(AergoClient.class);
    final KeyStoreOperation mockKeyStoreOperation = mock(KeyStoreOperation.class);
    when(mockKeyStoreOperation.exportKey(any(Authentication.class)))
        .thenReturn(mock(EncryptedPrivateKey.class));
    when(mockClient.getKeyStoreOperation()).thenReturn(mockKeyStoreOperation);

    final KeyStore keyStore = new ServerKeyStore(mockClient);
    final Authentication authentication =
        Authentication.of(ACCOUNT_ADDRESS, randomUUID().toString());
    final EncryptedPrivateKey exported = keyStore.export(authentication);
    assertNotNull(exported);
  }

  @Test
  public void testUnlockOnSuccess() {
    final AergoClient mockClient = mock(AergoClient.class);
    final KeyStoreOperation mockKeyStoreOperation = mock(KeyStoreOperation.class);
    when(mockKeyStoreOperation.unlock(any(Authentication.class))).thenReturn(true);
    when(mockClient.getKeyStoreOperation()).thenReturn(mockKeyStoreOperation);

    final KeyStore keyStore = new ServerKeyStore(mockClient);
    final Authentication authentication =
        Authentication.of(ACCOUNT_ADDRESS, randomUUID().toString());
    final Account unlocked = keyStore.unlock(authentication);
    assertNotNull(unlocked);
  }

  @Test
  public void testUnlockOnFailure() {
    final AergoClient mockClient = mock(AergoClient.class);
    final KeyStoreOperation mockKeyStoreOperation = mock(KeyStoreOperation.class);
    when(mockKeyStoreOperation.unlock(any(Authentication.class))).thenReturn(false);
    when(mockClient.getKeyStoreOperation()).thenReturn(mockKeyStoreOperation);

    final KeyStore keyStore = new ServerKeyStore(mockClient);
    final Authentication authentication =
        Authentication.of(ACCOUNT_ADDRESS, randomUUID().toString());
    final Account unlocked = keyStore.unlock(authentication);
    assertNull(unlocked);
  }


  @Test
  public void testLock() {
    final AergoClient mockClient = mock(AergoClient.class);
    final KeyStoreOperation mockKeyStoreOperation = mock(KeyStoreOperation.class);
    when(mockKeyStoreOperation.lock(any(Authentication.class))).thenReturn(true);
    when(mockClient.getKeyStoreOperation()).thenReturn(mockKeyStoreOperation);

    final KeyStore keyStore = new ServerKeyStore(mockClient);
    final Authentication authentication =
        Authentication.of(ACCOUNT_ADDRESS, randomUUID().toString());
    keyStore.lock(authentication);
  }

}
