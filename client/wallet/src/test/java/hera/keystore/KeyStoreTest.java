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
import hera.model.KeyAlias;
import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import org.junit.Before;
import org.junit.Test;

public class KeyStoreTest extends AbstractTestCase {

  protected final String keyStoreRoot =
      System.getProperty("java.io.tmpdir") + "/" + randomUUID().toString();

  @Before
  public void setUp() {
    new File(keyStoreRoot).mkdirs();
  }

  protected Collection<KeyStore> keyStores() {
    return Arrays.asList(new KeyStore[] {
        new InMemoryKeyStore(),
        new AergoKeyStore(keyStoreRoot),
        new JavaKeyStore("PKCS12", null, null)
    });
  }

  protected KeyAlias supplyAlias() {
    return KeyAlias.of(randomUUID().toString().replaceAll("-", ""));
  }

  @Test
  public void testSave() {
    for (final KeyStore keyStore : keyStores()) {
      // when
      final KeyAlias alias = supplyAlias();
      final Authentication authentication = Authentication.of(alias, randomUUID().toString());
      final AergoKey key = new AergoKeyGenerator().create();
      keyStore.save(authentication, key);

      // then
      assertTrue(keyStore.listIdentities().contains(alias));
    }
  }

  @Test
  public void shouldSaveThrowErrorOnSameAlias() {
    for (final KeyStore keyStore : keyStores()) {
      // when
      final KeyAlias alias = supplyAlias();
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
  }

  @Test
  public void shouldSaveThrowErrorOnSameAuthentication() {
    for (final KeyStore keyStore : keyStores()) {
      // when
      final KeyAlias alias = supplyAlias();
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
  }

  @Test
  public void shouldLoadOnValidAuthentication() {
    for (final KeyStore keyStore : keyStores()) {
      // when
      final KeyAlias alias = supplyAlias();
      final Authentication authentication = Authentication.of(alias, randomUUID().toString());
      final AergoKey key = new AergoKeyGenerator().create();
      keyStore.save(authentication, key);

      // then
      assertNotNull(keyStore.load(authentication));
    }
  }

  @Test
  public void shouldLoadThrowErrorOnInvalidAuthentication() {
    for (final KeyStore keyStore : keyStores()) {
      // when
      final KeyAlias alias = supplyAlias();
      final Authentication invalid = Authentication.of(alias, randomUUID().toString());

      // then
      try {
        keyStore.load(invalid);
        fail();
      } catch (InvalidAuthenticationException e) {
        // good we expected this
      }
    }
  }

  @Test
  public void shouldRemoveOnSavedOne() {
    for (final KeyStore keyStore : keyStores()) {
      // when
      final KeyAlias alias = supplyAlias();
      final AergoKey key = new AergoKeyGenerator().create();
      final Authentication authentication = Authentication.of(alias, randomUUID().toString());
      keyStore.save(authentication, key);

      // then
      keyStore.remove(authentication);
      assertTrue(false == keyStore.listIdentities().contains(alias));
    }
  }

  @Test
  public void shouldRemoveThrowErrorOnInvalidAuthentication() {
    // when
    final KeyStore keyStore = new AergoKeyStore(keyStoreRoot);
    final KeyAlias alias = supplyAlias();
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
    for (final KeyStore keyStore : keyStores()) {
      // when
      final KeyAlias alias = supplyAlias();
      final AergoKey key = new AergoKeyGenerator().create();
      final Authentication authentication = Authentication.of(alias, randomUUID().toString());
      keyStore.save(authentication, key);

      // then
      final String password = randomUUID().toString();
      final EncryptedPrivateKey exported = keyStore.export(authentication, password);
      final AergoKey decrypted = new AergoKeyGenerator().create(exported, password);
      assertEquals(decrypted, key);
    }
  }

  @Test
  public void shouldExportThrowErrorOnInvalidAuthentication() {
    for (final KeyStore keyStore : keyStores()) {
      // when
      final KeyAlias alias = supplyAlias();
      final Authentication invalid = Authentication.of(alias, randomUUID().toString());

      // then
      try {
        keyStore.export(invalid, randomUUID().toString());
        fail();
      } catch (InvalidAuthenticationException e) {
        // good we expected this
      }
    }
  }

}
