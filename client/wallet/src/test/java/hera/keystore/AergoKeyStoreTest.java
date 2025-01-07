/*
 * @copyright defined in LICENSE.txt
 */

package hera.keystore;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.*;

import hera.AbstractTestCase;
import java.io.File;
import java.util.List;

import hera.api.model.AccountAddress;
import hera.api.model.Authentication;
import hera.api.model.Identity;
import hera.exception.InvalidAuthenticationException;
import hera.key.AergoKey;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class AergoKeyStoreTest extends AbstractTestCase {
  private static final String KEY1_ADDRESS = "AmNusYXGmi5zKxjyPvTKMpnLaCRP5qmQeXbeSf2GE72s2y3nhAG9";
  private static final String KEY1_PK = "47N1pMe88fNV4Kz9WpjNUNVGkgQc9pvPWdMSNcJ3bf1A1kM3jRXitXAPbxoQhYtCCuf6Wjprm";
  private static final String KEY2_ADDRESS = "AmNAvfJ9RxnVeybramFK15ZtVcBUES8sCzYWtLzY9HUbjCYQXZB5";
  private static final String KEY2_PK = "47JjJgk6i2zTjmWcv2KAGFKguUqtvEgmPB7N2F4FLyDN8Pm9vDH1mEeG5hjNNDnvJgUGDN56y";

  private static final String PASSWORD = "ieze33cp";

  protected final String keyStoreRoot =
      System.getProperty("java.io.tmpdir") + "/" + randomUUID().toString();

  @Before
  public void setUp() {
    new File(keyStoreRoot).mkdirs();
  }


  @Test
  public void testSave() {
    final AergoKeyStore keyStore = new AergoKeyStore(keyStoreRoot);
    AergoKey sampleKey1 = AergoKey.of(KEY1_PK, PASSWORD);
    AccountAddress idKey1 = AccountAddress.of(KEY1_ADDRESS);
    AergoKey sampleKey2 = AergoKey.of(KEY2_PK, PASSWORD);
    AccountAddress idKey2 = AccountAddress.of(KEY2_ADDRESS);

    Authentication sampleAuth1 = Authentication.of(idKey1, PASSWORD);
    // directory is empty. so it can save
    assertFalse(keyStore.contains(idKey1));
    keyStore.save(sampleAuth1, sampleKey1);
    assertTrue(keyStore.contains(idKey1));

    Authentication sampleAuth2 = Authentication.of(idKey2, PASSWORD);
    // directory is empty. so it can save
    keyStore.save(sampleAuth2, sampleKey2);

    try {
      keyStore.save(sampleAuth2, sampleKey2);
      Assert.fail("should throw InvalidAuthenticationException");
    } catch (InvalidAuthenticationException expectedEx) {
      Assert.assertTrue(expectedEx.getMessage().toLowerCase().contains("already exists"));
    }
    List<Identity> identities = keyStore.listIdentities();
    Assert.assertEquals(2, identities.size());


    keyStore.store(randomUUID().toString(), randomUUID().toString().toCharArray());
  }


  @Test
  public void testSaveOnExistingKeys() {
    AergoKeyStore prevKeystore = new AergoKeyStore(keyStoreRoot);
    AergoKey sampleKey = AergoKey.of(KEY1_PK, PASSWORD);
    Authentication sampleAuth = Authentication.of(AccountAddress.of(KEY1_ADDRESS), PASSWORD);
    // directory is empty. so it can save
    prevKeystore.save(sampleAuth, sampleKey);
    prevKeystore = null;

    final AergoKeyStore keyStore = new AergoKeyStore(keyStoreRoot);
    AergoKey sampleKey2 = AergoKey.of(KEY2_PK, PASSWORD);
    Authentication sampleAuth2 = Authentication.of(AccountAddress.of(KEY2_ADDRESS), PASSWORD);
    // directory is empty. so it can save
    keyStore.save(sampleAuth2, sampleKey2);

    // Saving previously existing key.
    try {
      keyStore.save(sampleAuth, sampleKey);
      Assert.fail("should throw InvalidAuthenticationException");
    } catch (InvalidAuthenticationException expectedEx) {
      Assert.assertTrue(expectedEx.getMessage().toLowerCase().contains("already exists"));
    }
    List<Identity> identities = keyStore.listIdentities();
    Assert.assertEquals(2, identities.size());


    keyStore.store(randomUUID().toString(), randomUUID().toString().toCharArray());
  }


  @Test
  public void testStore() {
    final AergoKeyStore keyStore = new AergoKeyStore(keyStoreRoot);
    keyStore.store(randomUUID().toString(), randomUUID().toString().toCharArray());
  }

}
