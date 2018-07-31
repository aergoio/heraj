/*
 * @copyright defined in LICENSE.txt
 */

package hera.util.conf;

import static org.junit.Assert.assertNotNull;

import hera.util.AbstractTestCase;
import org.junit.Test;

public class EnvironmentConfigurationTest extends AbstractTestCase {

  @Test
  public void testAsMap()
      throws Exception {
    final EnvironmentConfiguration configuration = new EnvironmentConfiguration();
    logger.debug("Map: {}", configuration.asMap());
    assertNotNull(configuration.asMap());
  }

}
