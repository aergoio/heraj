/*
 * @copyright defined in LICENSE.txt
 */

package hera.keystore;

import static java.util.UUID.randomUUID;

import hera.AbstractTestCase;
import java.io.File;
import org.junit.Before;
import org.junit.Test;

public class AergoKeyStoreTest extends AbstractTestCase {

  protected final String keyStoreRoot =
      System.getProperty("java.io.tmpdir") + "/" + randomUUID().toString();

  @Before
  public void setUp() {
    new File(keyStoreRoot).mkdirs();
  }

  @Test
  public void testStore() {
    final AergoKeyStore keyStore = new AergoKeyStore(keyStoreRoot);
    keyStore.store(randomUUID().toString(), randomUUID().toString().toCharArray());
  }

}
