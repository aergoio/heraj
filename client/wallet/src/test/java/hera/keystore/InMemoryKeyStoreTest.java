/*
 * @copyright defined in LICENSE.txt
 */

package hera.keystore;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import hera.AbstractTestCase;
import hera.api.model.AccountAddress;
import hera.api.model.Aer;
import hera.api.model.Aer.Unit;
import hera.api.model.Authentication;
import hera.api.model.EncryptedPrivateKey;
import hera.api.model.Identity;
import hera.api.model.RawTransaction;
import hera.api.model.Transaction;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import org.junit.Test;

public class InMemoryKeyStoreTest extends AbstractTestCase {

  protected Identity invalidIdentity = new Identity() {

    @Override
    public String getValue() {
      return randomUUID().toString();
    }
  };

  @Test
  public void testSaveAndExport() {
    final InMemoryKeyStore keyStore = new InMemoryKeyStore();

    final AergoKey key = new AergoKeyGenerator().create();
    final String password = randomUUID().toString();
    final Authentication authentication = Authentication.of(key.getAddress(), password);
    keyStore.save(authentication, key);
    assertTrue(keyStore.listIdentities().contains(key.getAddress()));

    final EncryptedPrivateKey exported = keyStore.export(authentication);
    final AergoKey decrypted = new AergoKeyGenerator().create(exported, password);
    assertEquals(decrypted, key);
  }

  @Test
  public void testUnlockAndLock() {
    final InMemoryKeyStore keyStore = new InMemoryKeyStore();

    final AergoKey key = new AergoKeyGenerator().create();
    final String password = randomUUID().toString();
    final Authentication authentication =
        Authentication.of(key.getAddress(), password);
    keyStore.save(authentication, key);

    assertNotNull(keyStore.unlock(authentication));
    assertTrue(keyStore.lock(authentication));
  }

  @Test
  public void testUnlockOnInvalidAuth() {
    final InMemoryKeyStore keyStore = new InMemoryKeyStore();

    final String password = randomUUID().toString();
    final Authentication authentication = Authentication.of(invalidIdentity, password);
    try {
      keyStore.unlock(authentication);
      fail();
    } catch (Exception e) {
      // good we expected this
    }
  }

  @Test
  public void testLockWithInvalidAuth() {
    final InMemoryKeyStore keyStore = new InMemoryKeyStore();

    final AergoKey key = new AergoKeyGenerator().create();
    final String password = randomUUID().toString();
    final Authentication authentication = Authentication.of(key.getAddress(), password);
    keyStore.save(authentication, key);

    assertNotNull(keyStore.unlock(authentication));

    final Authentication invalid = Authentication.of(invalidIdentity, password);
    assertFalse(keyStore.lock(invalid));
  }

  @Test
  public void testSignRawTransaction() {
    final InMemoryKeyStore keyStore = new InMemoryKeyStore();

    final AergoKey key = new AergoKeyGenerator().create();
    final String password = randomUUID().toString();
    final Authentication authentication = Authentication.of(key.getAddress(), password);
    keyStore.save(authentication, key);

    final AccountAddress unlocked = keyStore.unlock(authentication);
    final RawTransaction rawTransaction = RawTransaction.newBuilder(chainIdHash)
        .from(key.getAddress())
        .to(key.getAddress())
        .amount(Aer.of("100", Unit.GAER))
        .nonce(1)
        .build();

    final Transaction signed = keyStore.sign(unlocked, rawTransaction);
    assertNotNull(signed);
  }

  @Test
  public void testSignOnLocked() {
    final InMemoryKeyStore keyStore = new InMemoryKeyStore();

    final AergoKey key = new AergoKeyGenerator().create();
    final String password = randomUUID().toString();
    final Authentication authentication = Authentication.of(invalidIdentity, password);
    keyStore.save(authentication, key);

    final RawTransaction rawTransaction = RawTransaction.newBuilder(chainIdHash)
        .from(key.getAddress())
        .to(key.getAddress())
        .amount(Aer.of("100", Unit.GAER))
        .nonce(1)
        .build();

    try {
      keyStore.sign(key.getAddress(), rawTransaction);
      fail("Sign on locked account should throw exception");
    } catch (Exception e) {
      // good we expected this
    }
  }

  @Test
  public void testStore() {
    InMemoryKeyStore keyStore = new InMemoryKeyStore();
    keyStore.store(randomUUID().toString(), randomUUID().toString().toCharArray());
  }

}
