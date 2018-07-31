/*
 * @copyright defined in LICENSE.txt
 */

package hera.util.conf;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;

import hera.util.AbstractTestCase;
import org.junit.Test;

public class InMemoryConfigurationTest extends AbstractTestCase {

  @Test
  public void testDefine() {
    final InMemoryConfiguration inMemoryConfiguration = new InMemoryConfiguration();
    final String fragment1 = randomUUID().toString();
    final String fragment2 = randomUUID().toString();
    final String key = fragment1 + "." + fragment2;
    final String value = randomUUID().toString();
        
    inMemoryConfiguration.define(key, value);
    assertNotNull(inMemoryConfiguration.get(key));
  }

}
