/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import static org.junit.Assert.assertNotNull;

import hera.AbstractTestCase;
import java.util.function.Consumer;
import org.junit.Test;

public class DangerousConsumerTest extends AbstractTestCase {

  @Test
  public void testToSafe() {
    final DangerousConsumer<Object> dangerousConsumer = o -> o.toString();
    final Consumer<Object> consumer = dangerousConsumer.toSafe();
    assertNotNull(consumer);
    consumer.accept(new Object());
  }

  @Test(expected = IllegalStateException.class)
  public void testToSafeWithException() {
    final DangerousConsumer<Object> dangerousConsumer = o -> {
      throw new UnsupportedOperationException();
    };
    final Consumer<Object> consumer = dangerousConsumer.toSafe();
    assertNotNull(consumer);
    consumer.accept(new Object());
  }

}
