/*
 * @copyright defined in LICENSE.txt
 */

package hera.util.conf;

import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import org.junit.Test;

public class SystemPropertiesConfigurationTest extends AbstractTestCase {

  @Test
  public void testAsMap() {
    final SystemPropertiesConfiguration configuration = new SystemPropertiesConfiguration();
    logger.debug("Map: {}", configuration.asMap());
    assertNotNull(configuration.asMap());
  }

}
