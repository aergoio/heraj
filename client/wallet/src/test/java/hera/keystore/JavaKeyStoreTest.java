/*
 * @copyright defined in LICENSE.txt
 */

package hera.keystore;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import hera.api.model.Authentication;
import hera.key.AergoKey;
import hera.key.AergoKeyGenerator;
import hera.key.Signer;
import hera.model.KeyAlias;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.junit.Test;

public class JavaKeyStoreTest extends AbstractTestCase {

  @Test
  public void testCreateWithProvider() {
    final JavaKeyStore keyStore = new JavaKeyStore("PKCS12", new BouncyCastleProvider());
    assertNotNull(keyStore);
  }

  @Test
  public void testCreateOnExistingOne() throws FileNotFoundException {
    // when
    final JavaKeyStore keyStore = new JavaKeyStore("PKCS12", null, null);

    final KeyAlias alias = new KeyAlias(randomUUID().toString().replace("-", ""));
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

  @Test
  public void testLoadExistingOne() {
    // when
    final InputStream in = open("keystore.p12");
    final String ksPassword = "password";
    final JavaKeyStore keyStore = new JavaKeyStore("PKCS12", in, ksPassword.toCharArray());
    final KeyAlias alias = new KeyAlias("keyalias");
    final String keyPassword = "password";
    final Authentication authentication = Authentication.of(alias, keyPassword);

    // then
    final Signer signer = keyStore.load(authentication);
    logger.info("Signer: {}", signer);
    assertNotNull(signer);
  }

}
