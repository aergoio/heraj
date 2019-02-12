/*
 * @copyright defined in LICENSE.txt
 */

package hera.keystore;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import hera.AbstractTestCase;
import hera.api.model.Account;
import hera.api.model.Aer;
import hera.api.model.Aer.Unit;
import hera.api.model.Authentication;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.exception.InvalidAuthentiationException;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import hera.keystore.InMemoryKeyStore;
import org.junit.Test;

public class InMemoryKeyStoreTest extends AbstractTestCase {

  @Test
  public void testSaveAndExport() {
    final InMemoryKeyStore keyStore = new InMemoryKeyStore();

    final AergoKey key = new AergoKeyGenerator().create();
    final String password = randomUUID().toString();
    keyStore.saveKey(key, password);
    assertTrue(keyStore.auth2EncryptedPrivateKey.size() > 0);
    assertTrue(keyStore.listStoredAddresses().contains(key.getAddress()));

    final Authentication auth = Authentication.of(key.getAddress(), password);
    final EncryptedPrivateKey exported = keyStore.export(auth);
    final AergoKey decrypted = AergoKey.of(exported, password);
    assertEquals(decrypted, key);
  }

  @Test
  public void testUnlockAndLock() {
    final InMemoryKeyStore keyStore = new InMemoryKeyStore();
    final AergoKey key = new AergoKeyGenerator().create();
    final String password = randomUUID().toString();
    keyStore.saveKey(key, password);

    final Authentication auth = Authentication.of(key.getAddress(), password);
    final Account unlocked = keyStore.unlock(auth);
    assertNotNull(unlocked);

    keyStore.lock(auth);
  }

  @Test
  public void testUnlockOnInvalidAuth() {
    final InMemoryKeyStore keyStore = new InMemoryKeyStore();
    final AergoKey key = new AergoKeyGenerator().create();
    final String password = randomUUID().toString();
    // keyStore.save(key, password);

    final Authentication auth = Authentication.of(key.getAddress(), password);
    try {
      keyStore.unlock(auth);
      fail();
    } catch (InvalidAuthentiationException e) {
      // good we expected this
    }
  }

  @Test
  public void testLockWithInvalidAuth() {
    final InMemoryKeyStore keyStore = new InMemoryKeyStore();
    final AergoKey key = new AergoKeyGenerator().create();
    final String password = randomUUID().toString();
    keyStore.saveKey(key, password);

    final Authentication auth = Authentication.of(key.getAddress(), password);
    final Account unlocked = keyStore.unlock(auth);
    assertNotNull(unlocked);

    final Authentication invalid = Authentication.of(key.getAddress(), randomUUID().toString());

    try {
      keyStore.lock(invalid);
      fail();
    } catch (InvalidAuthentiationException e) {
      // good we expected this
    }
  }

  @Test
  public void testSignAndVerify() {
    final InMemoryKeyStore keyStore = new InMemoryKeyStore();
    final AergoKey key = new AergoKeyGenerator().create();
    final String password = randomUUID().toString();
    keyStore.saveKey(key, password);

    keyStore.unlock(Authentication.of(key.getAddress(), password));
    final RawTransaction rawTransaction = RawTransaction.newBuilder()
        .from(key.getAddress())
        .to(key.getAddress())
        .amount(Aer.of("100", Unit.GAER))
        .nonce(1)
        .build();
    final Transaction signed = keyStore.sign(rawTransaction);

    assertTrue(keyStore.verify(signed));
  }

  @Test
  public void testSignOnLocked() {
    final InMemoryKeyStore keyStore = new InMemoryKeyStore();
    final AergoKey key = new AergoKeyGenerator().create();
    final String password = randomUUID().toString();
    keyStore.saveKey(key, password);

    // keyStore.unlock(Authentication.of(key.getAddress(), password));
    final RawTransaction rawTransaction = RawTransaction.newBuilder()
        .from(key.getAddress())
        .to(key.getAddress())
        .amount(Aer.of("100", Unit.GAER))
        .nonce(1)
        .build();

    try {
      keyStore.sign(rawTransaction);
      fail("Sign on locked account should throw exception");
    } catch (Exception e) {
      // good we expected this
    }
  }

}
