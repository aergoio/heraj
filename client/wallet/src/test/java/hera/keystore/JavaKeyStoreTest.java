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
  }

  @Test
  public void testSaveAndExport() {
    final JavaKeyStore keyStore = new JavaKeyStore("PKCS12");

    final AergoKey key = new AergoKeyGenerator().create();
    final String password = randomUUID().toString();
    final Authentication authentication = Authentication.of(identity, password);
    keyStore.save(authentication, key);
    assertTrue(keyStore.listIdentities().contains(identity));

    final EncryptedPrivateKey exported = keyStore.export(authentication);
    final AergoKey decrypted = AergoKey.of(exported, password);
    assertEquals(decrypted, key);
  }

  @Test
  public void testUnlockAndLock() {
    final JavaKeyStore keyStore = new JavaKeyStore("PKCS12");

    final AergoKey key = new AergoKeyGenerator().create();
    final String password = randomUUID().toString();
    final Authentication authentication = Authentication.of(identity, password);
    keyStore.save(authentication, key);

    assertNotNull(keyStore.unlock(authentication));
    assertTrue(keyStore.lock(authentication));
  }

  @Test
  public void testUnlockOnInvalidAuth() {
    final JavaKeyStore keyStore = new JavaKeyStore("PKCS12");

    final String password = randomUUID().toString();
    final Authentication authentication = Authentication.of(identity, password);

    try {
      keyStore.unlock(authentication);
      fail();
    } catch (Exception e) {
      // good we expected this
    }
  }

  @Test
  public void testLockWithInvalidAuth() {
    final JavaKeyStore keyStore = new JavaKeyStore("PKCS12");

    final AergoKey key = new AergoKeyGenerator().create();
    final String password = randomUUID().toString();
    final Authentication authentication = Authentication.of(identity, password);
    keyStore.save(authentication, key);

    assertNotNull(keyStore.unlock(authentication));

    final Authentication invalid = Authentication.of(key.getAddress(), randomUUID().toString());
    assertFalse(keyStore.lock(invalid));
  }

  @Test
  public void testSignRawTransaction() {
    final JavaKeyStore keyStore = new JavaKeyStore("PKCS12");

    final AergoKey key = new AergoKeyGenerator().create();
    final String password = randomUUID().toString();
    final Authentication authentication = Authentication.of(identity, password);
    keyStore.save(authentication, key);

    AccountAddress unlocked = keyStore.unlock(authentication);
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
    final JavaKeyStore keyStore = new JavaKeyStore("PKCS12");

    final AergoKey key = new AergoKeyGenerator().create();
    final String password = randomUUID().toString();
    final Authentication authentication = Authentication.of(identity, password);
    keyStore.save(authentication, key);

    // keyStore.unlock(authentication);
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
  public void testStoreAndLoad() {
    // given
    final JavaKeyStore keyStore = new JavaKeyStore("PKCS12");

    final AergoKey key = new AergoKeyGenerator().create();
    final String password = randomUUID().toString();
    final Authentication authentication = Authentication.of(identity, password);
    keyStore.save(authentication, key);

    final String path = System.getProperty("java.io.tmpdir") + randomUUID().toString();
    final char[] keyStorePassword = randomUUID().toString().toCharArray();
    keyStore.store(path, keyStorePassword);

    // when
    final JavaKeyStore loaded = new JavaKeyStore("PKCS12", path, keyStorePassword);

    // then
    assertNotNull(loaded.unlock(authentication));
  }

}