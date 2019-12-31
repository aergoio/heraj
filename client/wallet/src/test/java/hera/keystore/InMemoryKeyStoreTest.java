/*
 * @copyright defined in LICENSE.txt
 */

package hera.keystore;

import static java.util.UUID.randomUUID;

import hera.AbstractTestCase;
import org.junit.Test;

public class InMemoryKeyStoreTest extends AbstractTestCase {

  @Test
  public void testStore() {
    InMemoryKeyStore keyStore = new InMemoryKeyStore();
    keyStore.store(randomUUID().toString(), randomUUID().toString().toCharArray());
  }

}
