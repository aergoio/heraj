/*
 * @copyright defined in LICENSE.txt
 */

package hera.keystore;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.*;

import hera.AbstractTestCase;
import hera.api.model.AccountAddress;
import hera.api.model.Authentication;
import hera.api.model.Identity;
import hera.exception.InvalidAuthenticationException;
import hera.key.AergoKey;
import org.junit.Assert;
import org.junit.Test;

import java.util.List;

public class InMemoryKeyStoreTest extends AbstractTestCase {
  private static final String KEY1_ADDRESS = "AmNusYXGmi5zKxjyPvTKMpnLaCRP5qmQeXbeSf2GE72s2y3nhAG9";
  private static final String KEY1_PK = "47N1pMe88fNV4Kz9WpjNUNVGkgQc9pvPWdMSNcJ3bf1A1kM3jRXitXAPbxoQhYtCCuf6Wjprm";
  private static final String KEY2_ADDRESS = "AmNAvfJ9RxnVeybramFK15ZtVcBUES8sCzYWtLzY9HUbjCYQXZB5";
  private static final String KEY2_PK = "47JjJgk6i2zTjmWcv2KAGFKguUqtvEgmPB7N2F4FLyDN8Pm9vDH1mEeG5hjNNDnvJgUGDN56y";

  private static final String PASSWORD = "ieze33cp";

  @Test
  public void testStore() {
    InMemoryKeyStore keyStore = new InMemoryKeyStore();
    keyStore.store(randomUUID().toString(), randomUUID().toString().toCharArray());
  }

  @Test
  public void testSave() {
    final InMemoryKeyStore keyStore = new InMemoryKeyStore();
    AergoKey sampleKey = AergoKey.of(KEY1_PK, PASSWORD);
    Authentication sampleAuth = Authentication.of(AccountAddress.of(KEY1_ADDRESS), PASSWORD);
    // directory is empty. so it can save
    keyStore.save(sampleAuth, sampleKey);

    AergoKey sampleKey2 = AergoKey.of(KEY2_PK, PASSWORD);
    Authentication sampleAuth2 = Authentication.of(AccountAddress.of(KEY2_ADDRESS), PASSWORD);
    // directory is empty. so it can save
    keyStore.save(sampleAuth2, sampleKey2);

    try {
      keyStore.save(sampleAuth2, sampleKey2);
      fail("should throw InvalidAuthenticationException");
    } catch (InvalidAuthenticationException expectedEx) {
      assertTrue(expectedEx.getMessage().toLowerCase().contains("already exists"));
    }
    List<Identity> identities = keyStore.listIdentities();
    Assert.assertEquals(2, identities.size());


    keyStore.store(randomUUID().toString(), randomUUID().toString().toCharArray());
  }

  @Test
  public void testSaveOnExistingKeys() {
    InMemoryKeyStore prevKeystore = new InMemoryKeyStore();
    AergoKey sampleKey = AergoKey.of(KEY1_PK, PASSWORD);
    Authentication sampleAuth = Authentication.of(AccountAddress.of(KEY1_ADDRESS), PASSWORD);
    //
    prevKeystore.save(sampleAuth, sampleKey);
    assertEquals(1, prevKeystore.storedIdentities.size());
    prevKeystore = null;

    // New instance of InMemoryKeyStore is not affected by other instances
    final InMemoryKeyStore keyStore = new InMemoryKeyStore();
    AergoKey sampleKey2 = AergoKey.of(KEY2_PK, PASSWORD);
    Authentication sampleAuth2 = Authentication.of(AccountAddress.of(KEY2_ADDRESS), PASSWORD);
    keyStore.save(sampleAuth2, sampleKey2);
    keyStore.save(sampleAuth, sampleKey);

    // Saving previously existing key.
    try {
      keyStore.save(sampleAuth, sampleKey);
      fail("should throw InvalidAuthenticationException");
    } catch (InvalidAuthenticationException expectedEx) {
      assertTrue(expectedEx.getMessage().toLowerCase().contains("already exists"));
    }
    List<Identity> identities = keyStore.listIdentities();
    Assert.assertEquals(2, identities.size());


    keyStore.store(randomUUID().toString(), randomUUID().toString().toCharArray());
  }

  @Test
  public void testRemove() {
    final InMemoryKeyStore keyStore = new InMemoryKeyStore();
    AergoKey sampleKey = AergoKey.of(KEY1_PK, PASSWORD);
    AccountAddress idKey1 = AccountAddress.of(KEY1_ADDRESS);
    Authentication sampleAuth = Authentication.of(idKey1, PASSWORD);
    // directory is empty. so it can save
    keyStore.save(sampleAuth, sampleKey);
    assertTrue(keyStore.contains(idKey1));
    keyStore.remove(sampleAuth);
    assertFalse(keyStore.contains(idKey1));
  }
}
