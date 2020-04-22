/*
 * @copyright defined in LICENSE.txt
 */

package hera.client;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import hera.AbstractTestCase;
import hera.Context;
import hera.ContextStorage;
import hera.EmptyContext;
import hera.Key;
import org.junit.Test;

public class UnmodifiableContextStorageTest extends AbstractTestCase {

  @Test
  public void testPut() {
    // given
    final Context expected = EmptyContext.getInstance()
        .withValue(Key.of(randomUUID().toString(), String.class), randomUUID().toString())
        .withValue(Key.of(randomUUID().toString(), String.class), randomUUID().toString())
        .withValue(Key.of(randomUUID().toString(), String.class), randomUUID().toString());
    final ContextStorage<Context> contextStorage = new UnmodifiableContextStorage(expected);

    // then
    final Context actual = contextStorage.get();
    assertEquals(expected, actual);
  }

  @Test
  public void shouldPutThrowException() {
    final ContextStorage<Context> contextStorage = new UnmodifiableContextStorage(
        EmptyContext.getInstance());
    try {
      contextStorage.put(EmptyContext.getInstance());
      fail("Should throw UnsupportedOperationException");
    } catch (UnsupportedOperationException e) {
      // then
    }
  }

}
