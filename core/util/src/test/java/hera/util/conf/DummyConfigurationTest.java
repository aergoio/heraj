/*
 * @copyright defined in LICENSE.txt
 */

package hera.util.conf;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import hera.AbstractTestCase;
import hera.util.Configuration;
import java.util.Collections;
import org.junit.Test;

public class DummyConfigurationTest extends AbstractTestCase {

  @Test
  public void testGetValue() {
    Configuration conf = DummyConfiguration.getInstance();
    assertNull(conf.get(randomUUID().toString()));
  }

  @Test
  public void testAsMap() {
    Configuration conf = DummyConfiguration.getInstance();
    assertEquals(Collections.EMPTY_MAP, conf.asMap());
  }

  @Test(expected = UnsupportedOperationException.class)
  public void testDefinne() {
    Configuration conf = DummyConfiguration.getInstance();
    conf.define(randomUUID().toString(), randomUUID().toString());
  }

}
