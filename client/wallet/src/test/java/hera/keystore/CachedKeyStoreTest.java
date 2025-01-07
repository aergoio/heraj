package hera.keystore;

import hera.api.model.AccountAddress;
import hera.api.model.Authentication;
import hera.exception.InvalidAuthenticationException;
import hera.key.AergoKey;
import hera.key.Signer;
import org.junit.BeforeClass;
import org.junit.Test;
import org.omg.CORBA.DynAnyPackage.Invalid;

import java.io.File;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.*;

public class CachedKeyStoreTest {
  static AbstractKeyStore baseKeyStore;
  private static final String KEY1_ADDRESS = "AmNusYXGmi5zKxjyPvTKMpnLaCRP5qmQeXbeSf2GE72s2y3nhAG9";
  private static final String KEY1_PK = "47N1pMe88fNV4Kz9WpjNUNVGkgQc9pvPWdMSNcJ3bf1A1kM3jRXitXAPbxoQhYtCCuf6Wjprm";
  public static final AccountAddress ID_KEY1 = AccountAddress.of(KEY1_ADDRESS);

  private static final String KEY2_ADDRESS = "AmNAvfJ9RxnVeybramFK15ZtVcBUES8sCzYWtLzY9HUbjCYQXZB5";
  private static final String KEY2_PK = "47JjJgk6i2zTjmWcv2KAGFKguUqtvEgmPB7N2F4FLyDN8Pm9vDH1mEeG5hjNNDnvJgUGDN56y";
  public static final AccountAddress ID_KEY2 = AccountAddress.of(KEY2_ADDRESS);

  private static final String PASSWORD = "ieze33cp";

  protected static final String keyStoreRoot =
      System.getProperty("java.io.tmpdir") + "/" + randomUUID().toString();

  @BeforeClass
  public static void beforeClass() throws Exception {
    new File(keyStoreRoot).mkdirs();
    baseKeyStore = new AergoKeyStore(keyStoreRoot);
    AergoKey sampleKey = AergoKey.of(KEY1_PK, PASSWORD);
    Authentication auth1 = Authentication.of(ID_KEY1, PASSWORD);
    baseKeyStore.save(auth1, sampleKey);
  }

  @Test
  public void load() {
    final CachedKeyStore keyStore = new CachedKeyStore(baseKeyStore);
    Authentication authKey1 = Authentication.of(ID_KEY1, PASSWORD);
    assertTrue(baseKeyStore.contains(ID_KEY1));
    assertTrue(keyStore.contains(ID_KEY1));
    assertFalse(keyStore.contains(ID_KEY2));


    AergoKey key = keyStore.loadAergoKey(authKey1);
    assertEquals(key.getAddress(), ID_KEY1);
  }

  @Test
  public void saveAndRemove() {
    final CachedKeyStore keyStore = new CachedKeyStore(baseKeyStore);
    assertTrue(keyStore.contains(ID_KEY1));
    assertFalse(keyStore.contains(ID_KEY2));

    AergoKey sampleKey = AergoKey.of(KEY2_PK, PASSWORD);
    Authentication authKey2 = Authentication.of(ID_KEY2, PASSWORD);
    keyStore.save(authKey2, sampleKey);
    assertTrue(keyStore.contains(ID_KEY2));
    assertEquals(keyStore.loadAergoKey(authKey2), sampleKey);

    // only keys saved to keystore will be removed
    keyStore.remove(authKey2);
    assertFalse(keyStore.contains(ID_KEY2));

    // keys in baseKeyStore will not be removed
    Authentication authKey1 = Authentication.of(ID_KEY1, PASSWORD);
    try {
      keyStore.remove(authKey1);
      fail("should throw IllegalArgumentException");
    } catch (InvalidAuthenticationException expected) {
    }
    assertTrue(keyStore.contains(ID_KEY1));
  }

  @Test
  public void listIdentities() {
    final CachedKeyStore keyStore = new CachedKeyStore(baseKeyStore);
    AergoKey sampleKey = AergoKey.of(KEY2_PK, PASSWORD);
    Authentication authKey2 = Authentication.of(ID_KEY2, PASSWORD);
    keyStore.save(authKey2, sampleKey);
    assertEquals(2, keyStore.listIdentities().size());
    assertEquals(1, baseKeyStore.listIdentities().size());
  }

}