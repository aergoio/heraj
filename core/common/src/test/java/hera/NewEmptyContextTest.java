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
    final Context instance1 = EmptyContext.getInstance();
    final Context instance2 = EmptyContext.getInstance();
    assertNotNull(instance1);
    assertTrue(instance1 == instance2);
  }

  @Test
  public void testWithValue() {
    final Context instance = EmptyContext.getInstance();
    final Key<String> key = Key.of(randomUUID().toString(), String.class);
    final String expected = randomUUID().toString();
    final Context context = instance.withValue(key, expected);
    final String actual = context.get(key);
    assertEquals(expected, actual);
  }

  @Test
  public void testGet() {
    final Context instance = EmptyContext.getInstance();
    final Key<String> key = Key.of(randomUUID().toString(), String.class);
    final String value = instance.get(key);
    assertNull(value);
  }

  @Test
  public void testGetOrDefault() {
    final Context instance = EmptyContext.getInstance();
    final Key<String> key = Key.of(randomUUID().toString(), String.class);
    final String expected = randomUUID().toString();
    final String actual = instance.getOrDefault(key, expected);
    assertEquals(expected, actual);
  }

  @Test
  public void testGetScope() {
    final Context instance = EmptyContext.getInstance();
    final String expected = "<<empty>>";
    final String actual = instance.getScope();
    assertEquals(expected, actual);
  }

  @Test
  public void testWithScope() {
    final Context instance = EmptyContext.getInstance();
    final String expected = randomUUID().toString();
    final Context context = instance.withScope(expected);
    final String actual = context.getScope();
    assertEquals(expected, actual);
  }

}
