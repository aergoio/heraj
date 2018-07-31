/*
 * @copyright defined in LICENSE.txt
 */

package hera.util.conf;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;

import hera.util.AbstractTestCase;
import hera.util.Configuration;
import org.junit.Test;

public class HierachicalConfigurationTest extends AbstractTestCase {

  @Test
  public void testCreate() {
    final String key = randomUUID().toString();
    final String v1 = randomUUID().toString();
    final String v2 = randomUUID().toString();
    InMemoryConfiguration conf1 = new InMemoryConfiguration();
    conf1.define(key, v1);
    InMemoryConfiguration conf2 = new InMemoryConfiguration();
    conf1.define(key, v2);
    Configuration conf = HierachicalConfiguration.create(conf1, conf2);
    assertEquals(v2, conf.get(key));
  }

}
