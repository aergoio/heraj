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
import hera.model.KeyAlias;
import org.junit.Before;
import org.junit.Test;

public class JavaKeyStoreTest extends AbstractTestCase {

  protected String keyStorePasword = "password";

  protected Identity identity;

  protected java.security.KeyStore keyStore;

  @Before
  public void setUp() throws Exception {
    identity = new KeyAlias(randomUUID().toString().replace("-", "").toLowerCase());
    keyStore = java.security.KeyStore.getInstance("PKCS12");
    keyStore.load(null, keyStorePasword.toCharArray());
  }

  @Test
  public void testSaveAndExport() {
    final JavaKeyStore keyStore = new JavaKeyStore(this.keyStore);

    final AergoKey key = new AergoKeyGenerator().create();
    final String password = randomUUID().toString();
    final Authentication authentication = Authentication.of(identity, password);
    keyStore.saveKey(key, authentication);
    assertTrue(keyStore.listIdentities().contains(identity));

    final EncryptedPrivateKey exported = keyStore.export(authentication);
    final AergoKey decrypted = AergoKey.of(exported, password);
    assertEquals(decrypted, key);
  }

  @Test
  public void testUnlockAndLock() {
    final JavaKeyStore keyStore = new JavaKeyStore(this.keyStore);

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
    final JavaKeyStore keyStore = new JavaKeyStore(this.keyStore);

    final String password = randomUUID().toString();
    final Authentication authentication = Authentication.of(identity, password);
    // keyStore.saveKey(key, authentication);

    final Account unlocked = keyStore.unlock(authentication);
    assertNull(unlocked);
  }

  @Test
  public void testLockWithInvalidAuth() {
    final JavaKeyStore keyStore = new JavaKeyStore(this.keyStore);

    final AergoKey key = new AergoKeyGenerator().create();
    final String password = randomUUID().toString();
    final Authentication authentication = Authentication.of(identity, password);
    keyStore.saveKey(key, authentication);

    final Account unlocked = keyStore.unlock(authentication);
    assertNotNull(unlocked);

    final Authentication invalid = Authentication.of(key.getAddress(), randomUUID().toString());
    final boolean lockResult = keyStore.lock(invalid);
    assertFalse(lockResult);
  }

  @Test
  public void testSignAndVerify() {
    final JavaKeyStore keyStore = new JavaKeyStore(this.keyStore);

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
    final JavaKeyStore keyStore = new JavaKeyStore(this.keyStore);

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
