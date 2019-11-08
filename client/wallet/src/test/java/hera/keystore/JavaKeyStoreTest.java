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
import hera.api.model.Authentication;
import hera.api.model.EncryptedPrivateKey;
import hera.exception.InvalidAuthenticationException;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import hera.key.Signer;
import hera.model.KeyAlias;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Test;

public class JavaKeyStoreTest extends AbstractTestCase {

  @Test
  public void testCreateWithProvider() {
    final JavaKeyStore keyStore = new JavaKeyStore("PKCS12", new BouncyCastleProvider());
    assertNotNull(keyStore);
  }

  @Test
  public void testSave() {
    // when
    final JavaKeyStore keyStore = new JavaKeyStore("PKCS12", null, null);
    final KeyAlias alias = new KeyAlias(randomUUID().toString());
    final Authentication authentication = Authentication.of(alias, randomUUID().toString());
    final AergoKey key = new AergoKeyGenerator().create();
    keyStore.save(authentication, key);

    // then
    assertTrue(keyStore.listIdentities().contains(alias));
  }

  @Test
  public void shouldSaveThrowErrorOnSameAlias() {
    // when
    final JavaKeyStore keyStore = new JavaKeyStore("PKCS12", null, null);
    final KeyAlias alias = new KeyAlias(randomUUID().toString());
    final Authentication authentication = Authentication.of(alias, randomUUID().toString());
    final AergoKey key = new AergoKeyGenerator().create();
    keyStore.save(authentication, key);

    // then
    try {
      final AergoKey newKey = new AergoKeyGenerator().create();
      final Authentication sameAlias = Authentication.of(alias, randomUUID().toString());
      keyStore.save(sameAlias, newKey);
      fail();
    } catch (InvalidAuthenticationException e) {
      // good we expected this
    }
  }

  @Test
  public void shouldSaveThrowErrorOnSameAuthentication() {
    // when
    final JavaKeyStore keyStore = new JavaKeyStore("PKCS12", null, null);
    final KeyAlias alias = new KeyAlias(randomUUID().toString());
    final Authentication authentication = Authentication.of(alias, randomUUID().toString());
    final AergoKey key = new AergoKeyGenerator().create();
    keyStore.save(authentication, key);

    // then
    try {
      final AergoKey newKey = new AergoKeyGenerator().create();
      keyStore.save(authentication, newKey);
      fail();
    } catch (InvalidAuthenticationException e) {
      // good we expected this
    }
  }

  @Test
  public void shouldLoadOnValidAuthentication() {
    // when
    final JavaKeyStore keyStore = new JavaKeyStore("PKCS12", null, null);
    final KeyAlias alias = new KeyAlias(randomUUID().toString());
    final Authentication authentication = Authentication.of(alias, randomUUID().toString());
    final AergoKey key = new AergoKeyGenerator().create();
    keyStore.save(authentication, key);

    // then
    assertNotNull(keyStore.load(authentication));
  }

  @Test
  public void shouldLoadThrowErrorOnInvalidAuthentication() {
    // when
    final JavaKeyStore keyStore = new JavaKeyStore("PKCS12", null, null);
    final KeyAlias alias = new KeyAlias(randomUUID().toString());
    final Authentication invalid = Authentication.of(alias, randomUUID().toString());

    // then
    try {
      keyStore.load(invalid);
      fail();
    } catch (InvalidAuthenticationException e) {
      // good we expected this
    }
  }

  @Test
  public void shouldRemoveOnSavedOne() {
    // when
    final JavaKeyStore keyStore = new JavaKeyStore("PKCS12", null, null);
    final KeyAlias alias = new KeyAlias(randomUUID().toString());
    final AergoKey key = new AergoKeyGenerator().create();
    final Authentication authentication = Authentication.of(alias, randomUUID().toString());
    keyStore.save(authentication, key);

    // then
    keyStore.remove(authentication);
    assertTrue(false == keyStore.listIdentities().contains(alias));
  }

  @Test
  public void shouldRemoveThrowErrorOnInvalidAuthentication() {
    // when
    final JavaKeyStore keyStore = new JavaKeyStore("PKCS12", null, null);
    final KeyAlias alias = new KeyAlias(randomUUID().toString());
    final Authentication invalid = Authentication.of(alias, randomUUID().toString());

    // then
    try {
      keyStore.remove(invalid);
      fail();
    } catch (InvalidAuthenticationException e) {
      // good we expected this
    }
  }

  @Test
  public void shouldExportOnSavedOne() {
    // when
    final JavaKeyStore keyStore = new JavaKeyStore("PKCS12", null, null);
    final KeyAlias alias = new KeyAlias(randomUUID().toString());
    final AergoKey key = new AergoKeyGenerator().create();
    final Authentication authentication = Authentication.of(alias, randomUUID().toString());
    keyStore.save(authentication, key);

    // then
    final String password = randomUUID().toString();
    final EncryptedPrivateKey exported = keyStore.export(authentication, password);
    final AergoKey decrypted = new AergoKeyGenerator().create(exported, password);
    assertEquals(decrypted, key);
  }

  @Test
  public void shouldExportThrowErrorOnInvalidAuthentication() {
    // when
    final JavaKeyStore keyStore = new JavaKeyStore("PKCS12", null, null);
    final KeyAlias alias = new KeyAlias(randomUUID().toString());
    final Authentication invalid = Authentication.of(alias, randomUUID().toString());

    // then
    try {
      keyStore.export(invalid, randomUUID().toString());
      fail();
    } catch (InvalidAuthenticationException e) {
      // good we expected this
    }
  }

  @Test
  public void testCreateOnExistingOne() throws FileNotFoundException {
    // when
    final JavaKeyStore keyStore = new JavaKeyStore("PKCS12", null, null);

    final KeyAlias alias = new KeyAlias(randomUUID().toString());
    final String password = randomUUID().toString();
    final AergoKey key = new AergoKeyGenerator().create();
    final Authentication authentication = Authentication.of(alias, password);
    keyStore.save(authentication, key);

    final String path = System.getProperty("java.io.tmpdir") + "/" + randomUUID().toString();
    final char[] keyStorePassword = randomUUID().toString().toCharArray();
    keyStore.store(path, keyStorePassword);

    // then
    final JavaKeyStore recovered =
        new JavaKeyStore("PKCS12", new FileInputStream(path), keyStorePassword);
    final Signer loadedSigner = recovered.load(authentication);
    assertEquals(key.getAddress(), loadedSigner.getPrincipal());
  }

}
