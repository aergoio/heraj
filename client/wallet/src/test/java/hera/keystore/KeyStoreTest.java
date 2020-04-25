/*
 * @copyright defined in LICENSE.txt
 */

package hera.keystore;

import static java.util.Arrays.asList;
import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
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
import java.util.List;
import org.junit.Test;

public class KeyStoreTest extends AbstractTestCase {

  protected final String keyStoreRoot =
      System.getProperty("java.io.tmpdir") + "/" + randomUUID().toString();

  protected List<? extends KeyStore> keyStores() {
    return asList(
        new InMemoryKeyStore(),
        new AergoKeyStore(keyStoreRoot),
        new JavaKeyStore("PKCS12", null, null)
    );
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
      // given
      final KeyAlias alias = supplyAlias();
      final Authentication authentication = Authentication.of(alias, randomUUID().toString());
      final AergoKey key = new AergoKeyGenerator().create();
      keyStore.save(authentication, key);

      try {
        // when
        final AergoKey newKey = new AergoKeyGenerator().create();
        final Authentication sameAlias = Authentication.of(alias, randomUUID().toString());
        keyStore.save(sameAlias, newKey);
        fail();
      } catch (InvalidAuthenticationException e) {
        // then
      }
    }
  }

  @Test
  public void shouldSaveThrowErrorOnSameAuthentication() {
    for (final KeyStore keyStore : keyStores()) {
      // given
      final KeyAlias alias = supplyAlias();
      final Authentication authentication = Authentication.of(alias, randomUUID().toString());
      final AergoKey key = new AergoKeyGenerator().create();
      keyStore.save(authentication, key);

      try {
        // when
        final AergoKey newKey = new AergoKeyGenerator().create();
        keyStore.save(authentication, newKey);
        fail();
      } catch (InvalidAuthenticationException e) {
        // then
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
      try {
        // when
        final KeyAlias alias = supplyAlias();
        final Authentication invalid = Authentication.of(alias, randomUUID().toString());
        keyStore.load(invalid);
        fail();
      } catch (InvalidAuthenticationException e) {
        // then
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
      assertFalse(keyStore.listIdentities().contains(alias));
    }
  }

  @Test
  public void shouldRemoveThrowErrorOnInvalidAuthentication() {
    for (final KeyStore keyStore : keyStores()) {
      try {
        // when
        final KeyAlias alias = supplyAlias();
        final Authentication invalid = Authentication.of(alias, randomUUID().toString());
        keyStore.remove(invalid);
        fail();
      } catch (InvalidAuthenticationException e) {
        // then
      }
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
      final AergoKey decrypted = AergoKey.of(exported, password);
      assertEquals(decrypted, key);
    }
  }

  @Test
  public void shouldExportThrowErrorOnInvalidAuthentication() {
    for (final KeyStore keyStore : keyStores()) {
      try {
        // when
        final KeyAlias alias = supplyAlias();
        final Authentication invalid = Authentication.of(alias, randomUUID().toString());
        keyStore.export(invalid, randomUUID().toString());
        fail();
      } catch (InvalidAuthenticationException e) {
        // then
      }
    }
  }

}
