/*
 * @copyright defined in LICENSE.txt
 */

package hera.util.conf;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
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

  @Test
  public void testGetSubConfiguration() {
    final String parentKey = "parent";
    final String childKey = "child";
    final String key = parentKey + "." + childKey;
    final String v1 = randomUUID().toString();
    final String v2 = randomUUID().toString();
    InMemoryConfiguration conf1 = new InMemoryConfiguration();
    conf1.define(key, v1);
    InMemoryConfiguration conf2 = new InMemoryConfiguration();
    conf1.define(key, v2);
    Configuration conf = HierachicalConfiguration.create(conf1, conf2);
    Configuration subConf = conf.getSubconfiguration(parentKey);
    assertEquals(v2, subConf.get(childKey));
  }

  @Test
  public void testAsMap() {
    Configuration conf = new HierachicalConfiguration(new DummyConfiguration());
    logger.debug("Map: {}", conf.asMap());
    assertNotNull(conf.asMap());
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testDefine() {
    Configuration conf = new HierachicalConfiguration(new DummyConfiguration());
    conf.define(randomUUID().toString(), randomUUID().toString());
  }

}
