/*
 * @copyright defined in LICENSE.txt
 */

package hera;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import org.junit.Test;

public class NewContextHolderTest extends AbstractTestCase {

  @Test
  public void testGetOnEmpty() throws Exception {
    runOnOtherThread(new Runnable() {
      @Override
      public void run() {
        final NewContext expected = NewEmptyContext.getInstance();
        final NewContext actual = NewContextHolder.current();
        assertTrue(expected == actual);
      }
    });
  }

  @Test
  public void testGet() {
    runOnOtherThread(new Runnable() {
      @Override
      public void run() {
        final Key<String> key = Key.of(randomUUID().toString(), String.class);
        final String value = randomUUID().toString();
        final NewContext expected = NewEmptyContext.getInstance().withValue(key, value);
        NewContextHolder.attach(expected);
        final NewContext actual = NewContextHolder.current();
        assertEquals(expected, actual);
      }
    });
  }

  @Test
  public void testGetOnAnotherThread() throws Exception {
    runOnOtherThread(new Runnable() {
      @Override
      public void run() {
        // when
        final Key<String> key = Key.of(randomUUID().toString(), String.class);
        final String value = randomUUID().toString();
        final NewContext context = NewEmptyContext.getInstance().withValue(key, value);
        NewContextHolder.attach(context);

        runOnOtherThread(new Runnable() {
          @Override
          public void run() {
            // then
            final NewContext expected = NewEmptyContext.getInstance();
            final NewContext actual = NewContextHolder.current();
            assertEquals(expected, actual);
          }
        });
      }
    });
  }

  protected void runOnOtherThread(final Runnable runnable) {
    final ExecutorService service = Executors.newFixedThreadPool(1);
    try {
      final Future<?> future = service.submit(runnable);
      future.get();
    } catch (Exception e) {
      throw new RuntimeException(e);
    } finally {
      service.shutdown();
    }
  }

}
