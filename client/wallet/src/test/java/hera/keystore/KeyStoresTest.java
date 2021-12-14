/*
 * @copyright defined in LICENSE.txt
 */

package hera.keystore;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class KeyStoresTest extends AbstractTestCase {
  @BeforeClass
  public static void beforeClass() throws Exception {
    // powermock cannot mock java.security packages in jdk17 due to stricter security policies
    Assume.assumeTrue(getVersion() < 17 );
  }

  private static int getVersion() {
      String version = System.getProperty("java.version");
      if(version.startsWith("1.")) {
          version = version.substring(2, 3);
      } else {
          int dot = version.indexOf(".");
          if(dot != -1) { version = version.substring(0, dot); }
      } return Integer.parseInt(version);
  }

  @Test
  public void testNewInMemoryKeyStore() {
    final KeyStore keyStore = KeyStores.newInMemoryKeyStore();
    assertNotNull(keyStore);
  }

  @Test
  public void testNewJavaKeyStore() throws Exception {
    final java.security.KeyStore delegate = java.security.KeyStore.getInstance("JKS");
    final KeyStore keyStore = KeyStores.newJavaKeyStore(delegate);
    assertNotNull(keyStore);
  }

  @Test
  public void testNewAergoKeyStore() {
    final String root = System.getProperty("java.io.tmpdir") + "/" + randomUUID().toString();
    final KeyStore keyStore = KeyStores.newAergoKeyStore(root);
    assertNotNull(keyStore);
  }

}
