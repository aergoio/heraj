/*
 * @copyright defined in LICENSE.txt
 */

package hera.wallet;

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
import org.junit.Before;
import org.junit.Test;

public class JavaKeyStoreTest extends AbstractTestCase {

  protected String keyStorePasword = "password";

  protected java.security.KeyStore keyStore;

  @Before
  public void setUp() throws Exception {
    keyStore = java.security.KeyStore.getInstance("PKCS12");
    keyStore.load(null, keyStorePasword.toCharArray());
  }

  @Test
  public void testSaveAndExport() {
    final JavaKeyStore keyStore = new JavaKeyStore(this.keyStore);

    final AergoKey key = new AergoKeyGenerator().create();
    final String password = randomUUID().toString();
    keyStore.saveKey(key, password);

    final Authentication auth = Authentication.of(key.getAddress(), password);
    final EncryptedPrivateKey exported = keyStore.export(auth);
    final AergoKey decrypted = AergoKey.of(exported, password);
    assertEquals(decrypted, key);
  }

  @Test
  public void testUnlockAndLock() {
    final JavaKeyStore keyStore = new JavaKeyStore(this.keyStore);
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
    final JavaKeyStore keyStore = new JavaKeyStore(this.keyStore);
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
    final JavaKeyStore keyStore = new JavaKeyStore(this.keyStore);
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
    final JavaKeyStore keyStore = new JavaKeyStore(this.keyStore);
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
    final JavaKeyStore keyStore = new JavaKeyStore(this.keyStore);
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
