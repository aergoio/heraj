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
import org.junit.Test;

public class InMemoryKeyStoreTest extends AbstractTestCase {

  @Test
  public void testSave() {
    // when
    final InMemoryKeyStore keyStore = new InMemoryKeyStore();
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
    final InMemoryKeyStore keyStore = new InMemoryKeyStore();
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
    final InMemoryKeyStore keyStore = new InMemoryKeyStore();
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
    final InMemoryKeyStore keyStore = new InMemoryKeyStore();
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
    final InMemoryKeyStore keyStore = new InMemoryKeyStore();
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
    final InMemoryKeyStore keyStore = new InMemoryKeyStore();
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
    final InMemoryKeyStore keyStore = new InMemoryKeyStore();
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
    final InMemoryKeyStore keyStore = new InMemoryKeyStore();
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
    final InMemoryKeyStore keyStore = new InMemoryKeyStore();
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
  public void testStore() {
    InMemoryKeyStore keyStore = new InMemoryKeyStore();
    keyStore.store(randomUUID().toString(), randomUUID().toString().toCharArray());
  }

}
