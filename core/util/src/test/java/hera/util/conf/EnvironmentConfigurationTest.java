/*
 * @copyright defined in LICENSE.txt
 */

package hera.util.conf;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import org.junit.Test;

public class EnvironmentConfigurationTest extends AbstractTestCase {

  @Test
  public void testAsMap() throws Exception {
    final EnvironmentConfiguration configuration = new EnvironmentConfiguration();
    logger.debug("Map: {}", configuration.asMap());
    assertNotNull(configuration.asMap());
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testDefine() {
    final EnvironmentConfiguration configuration = new EnvironmentConfiguration();
    configuration.define(randomUUID().toString(), randomUUID().toString());
  }

  @Test
  public void testGetValue() {
    final EnvironmentConfiguration configuration = new EnvironmentConfiguration();
    final Object value = configuration.getValue(randomUUID().toString());
    logger.debug("Value: {}", value);
  }

}
