/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hera.AbstractTestCase;
import hera.api.AccountOperation;
import hera.api.KeyStoreOperation;
import hera.api.model.AccountAddress;
import hera.api.model.Authentication;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.client.AergoClient;
import hera.key.AergoKeyGenerator;
import org.junit.Test;

public class ServerKeyStoreAdaptorTest extends AbstractTestCase {

  protected static final AccountAddress ACCOUNT_ADDRESS =
      AccountAddress.of(() -> "AmLo9CGR3xFZPVKZ5moSVRNW1kyscY9rVkCvgrpwNJjRUPUWadC5");

  @Test
  public void testSave() {
    final AergoClient mockClient = mock(AergoClient.class);
    when(mockClient.getKeyStoreOperation()).thenReturn(mock(KeyStoreOperation.class));

    final KeyStoreAdaptor keyStore = new ServerKeyStoreAdaptor(mockClient);
    keyStore.save(new AergoKeyGenerator().create(), randomUUID().toString());
  }

  @Test
  public void testExport() {
    final AergoClient mockClient = mock(AergoClient.class);
    final KeyStoreOperation mockKeyStoreOperation = mock(KeyStoreOperation.class);
    when(mockKeyStoreOperation.exportKey(any())).thenReturn(mock(EncryptedPrivateKey.class));
    when(mockClient.getKeyStoreOperation()).thenReturn(mockKeyStoreOperation);

    final KeyStoreAdaptor keyStore = new ServerKeyStoreAdaptor(mockClient);
    final Authentication authentication =
        Authentication.of(ACCOUNT_ADDRESS, randomUUID().toString());
    final EncryptedPrivateKey exported = keyStore.export(authentication);
    assertNotNull(exported);
  }

  @Test
  public void testUnlockOnSuccess() {
    final AergoClient mockClient = mock(AergoClient.class);
    final KeyStoreOperation mockKeyStoreOperation = mock(KeyStoreOperation.class);
    when(mockKeyStoreOperation.unlock(any())).thenReturn(true);
    when(mockClient.getKeyStoreOperation()).thenReturn(mockKeyStoreOperation);

    final KeyStoreAdaptor keyStore = new ServerKeyStoreAdaptor(mockClient);
    final Authentication authentication =
        Authentication.of(ACCOUNT_ADDRESS, randomUUID().toString());
    final AccountAddress unlocked = keyStore.unlock(authentication);
    assertNotNull(unlocked);
  }

  @Test
  public void testUnlockOnFailure() {
    final AergoClient mockClient = mock(AergoClient.class);
    final KeyStoreOperation mockKeyStoreOperation = mock(KeyStoreOperation.class);
    when(mockKeyStoreOperation.unlock(any())).thenReturn(false);
    when(mockClient.getKeyStoreOperation()).thenReturn(mockKeyStoreOperation);

    final KeyStoreAdaptor keyStore = new ServerKeyStoreAdaptor(mockClient);
    final Authentication authentication =
        Authentication.of(ACCOUNT_ADDRESS, randomUUID().toString());
    final AccountAddress unlocked = keyStore.unlock(authentication);
    assertNull(unlocked);
  }


  @Test
  public void testLock() {
    final AergoClient mockClient = mock(AergoClient.class);
    final KeyStoreOperation mockKeyStoreOperation = mock(KeyStoreOperation.class);
    when(mockKeyStoreOperation.lock(any())).thenReturn(true);
    when(mockClient.getKeyStoreOperation()).thenReturn(mockKeyStoreOperation);

    final KeyStoreAdaptor keyStore = new ServerKeyStoreAdaptor(mockClient);
    final Authentication authentication =
        Authentication.of(ACCOUNT_ADDRESS, randomUUID().toString());
    final boolean unlocked = keyStore.lock(authentication);
    assertTrue(unlocked);
  }

  @Test
  public void testSign() {
    final AergoClient mockClient = mock(AergoClient.class);
    final AccountOperation mockAccountOperation = mock(AccountOperation.class);
    when(mockAccountOperation.sign(any(), any())).thenReturn(mock(Transaction.class));
    when(mockClient.getAccountOperation()).thenReturn(mockAccountOperation);

    final KeyStoreAdaptor keyStore = new ServerKeyStoreAdaptor(mockClient);
    final Transaction signed = keyStore.sign(ACCOUNT_ADDRESS, mock(RawTransaction.class));
    assertNotNull(signed);
  }

  @Test
  public void testVerify() {
    final AergoClient mockClient = mock(AergoClient.class);
    final AccountOperation mockAccountOperation = mock(AccountOperation.class);
    when(mockAccountOperation.verify(any(), any())).thenReturn(true);
    when(mockClient.getAccountOperation()).thenReturn(mockAccountOperation);

    final KeyStoreAdaptor keyStore = new ServerKeyStoreAdaptor(mockClient);
    final boolean result = keyStore.verify(ACCOUNT_ADDRESS, mock(Transaction.class));
    assertTrue(result);
  }
}
