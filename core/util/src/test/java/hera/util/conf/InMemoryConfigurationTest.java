/*
 * @copyright defined in LICENSE.txt
 */

package hera.util.conf;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import java.util.Arrays;
import org.junit.Test;

public class InMemoryConfigurationTest extends AbstractTestCase {

  @Test
  public void testAsMap() {
    final InMemoryConfiguration inMemoryConfiguration = new InMemoryConfiguration();
    logger.debug("Map: {}", inMemoryConfiguration.asMap());
    assertNotNull(inMemoryConfiguration.asMap());
  }

  @Test
  public void testDefine() {
    final InMemoryConfiguration inMemoryConfiguration = new InMemoryConfiguration();
    final String fragment1 = randomUUID().toString();
    final String fragment2 = randomUUID().toString();
    final String key = fragment1 + "." + fragment2;
    final String value = randomUUID().toString();

    inMemoryConfiguration.define(key, value);
    assertNotNull(inMemoryConfiguration.get(key));
    logger.debug("Configuration: {}", inMemoryConfiguration);
  }

  @Test
  public void testGetAsSeveralTypes() {
    final InMemoryConfiguration inMemoryConfiguration = new InMemoryConfiguration();
    final String stringKey = randomUUID().toString();
    final String booleanKey = randomUUID().toString();
    final String intKey = randomUUID().toString();
    final String longKey = randomUUID().toString();
    final String doubleKey = randomUUID().toString();
    final String listKey = randomUUID().toString();

    final String stringValue = randomUUID().toString();
    final String booleanValue = "true";
    final String intValue = "123";
    final String longValue = "456";
    final String doubleValue = "789.01";
    final String listValue = "first,second,third";

    inMemoryConfiguration.define(stringKey, stringValue);
    inMemoryConfiguration.define(booleanKey, booleanValue);
    inMemoryConfiguration.define(intKey, intValue);
    inMemoryConfiguration.define(longKey, longValue);
    inMemoryConfiguration.define(doubleKey, doubleValue);
    inMemoryConfiguration.define(listKey, listValue);

    assertEquals(stringValue,
        inMemoryConfiguration.getAsString(stringKey, randomUUID().toString()));
    assertEquals(Boolean.parseBoolean(booleanValue),
        inMemoryConfiguration.getAsBoolean(booleanKey, false));
    assertEquals(Integer.parseInt(intValue),
        inMemoryConfiguration.getAsInt(intKey, randomUUID().hashCode()));
    assertEquals(Long.parseLong(longValue),
        inMemoryConfiguration.getAsLong(longKey, randomUUID().hashCode()));
    assertEquals(Double.parseDouble(doubleValue),
        inMemoryConfiguration.getAsDouble(doubleKey, randomUUID().hashCode()), 6);
    assertEquals(Arrays.asList(listValue.split(",")), inMemoryConfiguration.getAsList(listKey));
  }

}
