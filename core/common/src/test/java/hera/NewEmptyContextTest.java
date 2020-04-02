/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class NewEmptyContextTest extends AbstractTestCase {

  @Test
  public void testNewInstance() {
    final NewContext instance1 = NewEmptyContext.getInstance();
    final NewContext instance2 = NewEmptyContext.getInstance();
    assertNotNull(instance1);
    assertTrue(instance1 == instance2);
  }

  @Test
  public void testWithValue() {
    final NewContext instance = NewEmptyContext.getInstance();
    final Key<String> key = Key.of(randomUUID().toString(), String.class);
    final String expected = randomUUID().toString();
    final NewContext newContext = instance.withValue(key, expected);
    final String actual = newContext.get(key);
    assertEquals(expected, actual);
  }

  @Test
  public void testGet() {
    final NewContext instance = NewEmptyContext.getInstance();
    final Key<String> key = Key.of(randomUUID().toString(), String.class);
    final String value = instance.get(key);
    assertNull(value);
  }

  @Test
  public void testGetOrDefault() {
    final NewContext instance = NewEmptyContext.getInstance();
    final Key<String> key = Key.of(randomUUID().toString(), String.class);
    final String expected = randomUUID().toString();
    final String actual = instance.getOrDefault(key, expected);
    assertEquals(expected, actual);
  }

  @Test
  public void testGetScope() {
    final NewContext instance = NewEmptyContext.getInstance();
    final String expected = "<<empty>>";
    final String actual = instance.getScope();
    assertEquals(expected, actual);
  }

  @Test
  public void testWithScope() {
    final NewContext instance = NewEmptyContext.getInstance();
    final String expected = randomUUID().toString();
    final NewContext newContext = instance.withScope(expected);
    final String actual = newContext.getScope();
    assertEquals(expected, actual);
  }

}
