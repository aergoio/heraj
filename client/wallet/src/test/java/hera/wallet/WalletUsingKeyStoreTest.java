/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

import static hera.util.ValidationUtils.assertTrue;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import hera.AbstractTestCase;
import hera.api.model.AccountAddress;
import hera.api.model.Authentication;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.RawTransaction;
import hera.api.model.ServerManagedAccount;
import hera.api.model.Transaction;
import hera.key.AergoKeyGenerator;
import org.junit.Test;
import org.mockito.Mockito;

public class WalletUsingKeyStoreTest extends AbstractTestCase {

  private final AccountAddress accountAddress =
      AccountAddress.of(() -> "AmLo9CGR3xFZPVKZ5moSVRNW1kyscY9rVkCvgrpwNJjRUPUWadC5");

  private final EncryptedPrivateKey encryptedPrivatekey = EncryptedPrivateKey
      .of(() -> "47RHxbUL3DhA1TMHksEPdVrhumcjdXLAB3Hkv61mqkC9M1Wncai5b91q7hpKydfFHKyyVvgKt");

  @Test
  public void testUnlockOnSuccess() {
    WalletUsingKeyStore wallet = mock(WalletUsingKeyStore.class, Mockito.CALLS_REAL_METHODS);
    final KeyStoreAdaptor keyStore = mock(KeyStoreAdaptor.class);
    when(keyStore.unlock(any())).thenReturn(accountAddress);
    wallet.keyStore = keyStore;

    final Authentication authentication =
        Authentication.of(accountAddress, randomUUID().toString());
    final boolean unlockResult = wallet.unlock(authentication);
    assertTrue(unlockResult);
  }

  @Test
  public void testUnlockOnFailure() {
    WalletUsingKeyStore wallet = mock(WalletUsingKeyStore.class, Mockito.CALLS_REAL_METHODS);
    wallet.keyStore = mock(KeyStoreAdaptor.class);

    final Authentication authentication =
        Authentication.of(accountAddress, randomUUID().toString());
    final boolean unlockResult = wallet.unlock(authentication);
    assertFalse(unlockResult);
  }

  @Test
  public void testSign() {
    WalletUsingKeyStore wallet = mock(WalletUsingKeyStore.class, Mockito.CALLS_REAL_METHODS);
    final KeyStoreAdaptor keyStore = mock(KeyStoreAdaptor.class);
    when(keyStore.sign(any(), any())).thenReturn(mock(Transaction.class));
    wallet.keyStore = keyStore;
    wallet.account = ServerManagedAccount.of(accountAddress);

    final Transaction signed = wallet.sign(mock(RawTransaction.class));
    assertNotNull(signed);
  }

  @Test
  public void testVerify() {
    WalletUsingKeyStore wallet = mock(WalletUsingKeyStore.class, Mockito.CALLS_REAL_METHODS);
    final KeyStoreAdaptor keyStore = mock(KeyStoreAdaptor.class);
    when(keyStore.verify(any(), any())).thenReturn(true);
    wallet.keyStore = keyStore;
    wallet.account = ServerManagedAccount.of(accountAddress);

    final boolean verifyResult = wallet.verify(mock(Transaction.class));
    assertTrue(verifyResult);
  }

  @Test
  public void testSavekey() {
    WalletUsingKeyStore wallet = mock(WalletUsingKeyStore.class, Mockito.CALLS_REAL_METHODS);
    wallet.keyStore = mock(KeyStoreAdaptor.class);
    wallet.saveKey(new AergoKeyGenerator().create(), randomUUID().toString());
  }

  @Test
  public void testExportKey() {
    WalletUsingKeyStore wallet = mock(WalletUsingKeyStore.class, Mockito.CALLS_REAL_METHODS);
    final KeyStoreAdaptor keyStore = mock(KeyStoreAdaptor.class);
    when(keyStore.export(any())).thenReturn(encryptedPrivatekey);
    wallet.keyStore = keyStore;

    final Authentication authentication =
        Authentication.of(accountAddress, randomUUID().toString());
    final String exported = wallet.exportKey(authentication);
    assertNotNull(exported);
  }

}
