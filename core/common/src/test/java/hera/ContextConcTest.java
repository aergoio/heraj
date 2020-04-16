/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class ContextConcTest extends AbstractTestCase {

  protected final Context root = EmptyContext.getInstance();

  @Test
  public void testWithValue() {
    final Key<String> parentKey = Key.of(randomUUID().toString(), String.class);
    final String parentValue = randomUUID().toString();
    final Context parent = new ContextConc(root, parentKey, parentValue);
    final Key<String> childKey = Key.of(randomUUID().toString(), String.class);
    final String childValue = randomUUID().toString();
    final Context child = parent.withValue(childKey, childValue);
    assertEquals(child.get(parentKey), parentValue);
    assertEquals(child.get(childKey), childValue);
  }

  @Test
  public void testGet() {
    final Key<String> key = Key.of(randomUUID().toString(), String.class);
    final String expected = randomUUID().toString();
    final Context context = new ContextConc(root, key, expected);
    final String actual = context.get(key);
    assertEquals(expected, actual);
  }

  @Test
  public void testGetOrDefault() {
    final Key<String> key = Key.of(randomUUID().toString(), String.class);
    final Context context = new ContextConc(root, key, randomUUID().toString());
    final Key<String> nokey = Key.of(randomUUID().toString(), String.class);
    final String expected = randomUUID().toString();
    final String actual = context.getOrDefault(nokey, expected);
    assertEquals(expected, actual);
  }

  @Test
  public void testGetScope() {
    final String expected = randomUUID().toString();
    final Context context = new ContextConc(root, expected);
    final String actual = context.getScope();
    assertEquals(expected, actual);
  }

  @Test
  public void testWithScope() {
    final Context context = new ContextConc(root, randomUUID().toString());
    final String expected = randomUUID().toString();
    final Context newContext = context.withScope(expected);
    final String actual = newContext.getScope();
    assertEquals(expected, actual);
  }

}
