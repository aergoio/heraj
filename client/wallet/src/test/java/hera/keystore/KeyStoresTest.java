/*
 * @copyright defined in LICENSE.txt
 */

package hera.keystore;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import org.junit.Test;

public class KeyStoresTest extends AbstractTestCase {

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
