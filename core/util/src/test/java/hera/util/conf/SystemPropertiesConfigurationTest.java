/*
 * @copyright defined in LICENSE.txt
 */

package hera.util.conf;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import hera.AbstractTestCase;
import hera.util.Configuration;
import org.junit.Test;

public class SystemPropertiesConfigurationTest extends AbstractTestCase {

  @Test
  public void testAsMap() {
    final SystemPropertiesConfiguration configuration = new SystemPropertiesConfiguration();
    logger.debug("Map: {}", configuration.asMap());
    assertNotNull(configuration.asMap());
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testDefine() {
    final SystemPropertiesConfiguration configuration = new SystemPropertiesConfiguration();
    configuration.define(randomUUID().toString(), randomUUID().toString());
  }

  @Test
  public void testGetSubConfiguration() {
    final SystemPropertiesConfiguration configuration = new SystemPropertiesConfiguration();
    final Configuration subConfiguration =
        configuration.getSubconfiguration(randomUUID().toString());
    assertNotNull(subConfiguration);
  }

  @Test
  public void testGetValue() {
    final SystemPropertiesConfiguration configuration = new SystemPropertiesConfiguration();
    final Object value = configuration.getValue(randomUUID().toString());
    assertNull(value);
  }

}
