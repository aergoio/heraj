/*
 * @copyright defined in LICENSE.txt
 */

package hera.keystore;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import hera.AbstractTestCase;
import hera.api.model.Account;
import hera.api.model.Aer;
import hera.api.model.Aer.Unit;
import hera.api.model.Authentication;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.Identity;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import org.junit.Before;
import org.junit.Test;

public class InMemoryKeyStoreTest extends AbstractTestCase {

  protected Identity identity;

  @Before
  public void setUp() {
    identity = new Identity() {

      @Override
      public String getInfo() {
        return randomUUID().toString();
      }
    };
  }

  @Test
  public void testSaveAndExport() {
    final InMemoryKeyStore keyStore = new InMemoryKeyStore();

    final AergoKey key = new AergoKeyGenerator().create();
    final String password = randomUUID().toString();
    final Authentication authentication = Authentication.of(identity, password);
    keyStore.saveKey(key, authentication);
    assertTrue(keyStore.auth2EncryptedPrivateKey.size() > 0);
    assertTrue(keyStore.listIdentities().contains(identity));

    final EncryptedPrivateKey exported = keyStore.export(authentication);
    final AergoKey decrypted = AergoKey.of(exported, password);
    assertEquals(decrypted, key);
  }

  @Test
  public void testUnlockAndLock() {
    final InMemoryKeyStore keyStore = new InMemoryKeyStore();

    final AergoKey key = new AergoKeyGenerator().create();
    final String password = randomUUID().toString();
    final Authentication authentication = Authentication.of(identity, password);
    keyStore.saveKey(key, authentication);

    final Account unlocked = keyStore.unlock(authentication);
    assertNotNull(unlocked);

    keyStore.lock(authentication);
  }

  @Test
  public void testUnlockOnInvalidAuth() {
    final InMemoryKeyStore keyStore = new InMemoryKeyStore();

    final String password = randomUUID().toString();
    final Authentication authentication = Authentication.of(identity, password);
    // keyStore.saveKey(key, authentication);

    final Account unlocked = keyStore.unlock(authentication);
    assertNull(unlocked);
  }

  @Test
  public void testLockWithInvalidAuth() {
    final InMemoryKeyStore keyStore = new InMemoryKeyStore();

    final AergoKey key = new AergoKeyGenerator().create();
    final String password = randomUUID().toString();
    final Authentication authentication = Authentication.of(identity, password);
    keyStore.saveKey(key, authentication);

    final Account unlocked = keyStore.unlock(authentication);
    assertNotNull(unlocked);

    final Authentication invalid = Authentication.of(key.getAddress(), randomUUID().toString());
    final boolean unlockResult = keyStore.lock(invalid);
    assertFalse(unlockResult);
  }

  @Test
  public void testSignAndVerify() {
    final InMemoryKeyStore keyStore = new InMemoryKeyStore();

    final AergoKey key = new AergoKeyGenerator().create();
    final String password = randomUUID().toString();
    final Authentication authentication = Authentication.of(identity, password);
    keyStore.saveKey(key, authentication);

    keyStore.unlock(authentication);
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
    final Authentication authentication = Authentication.of(identity, password);
    keyStore.saveKey(key, authentication);

    // keyStore.unlock(authentication);
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
