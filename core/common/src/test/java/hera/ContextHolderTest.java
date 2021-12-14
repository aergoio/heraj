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

import org.junit.Assume;
import org.junit.BeforeClass;
import org.junit.Test;

public class ContextHolderTest extends AbstractTestCase {
  @BeforeClass
  public static void beforeClass() throws Exception {
    // powermock cannot mock java.security packages in jdk17 due to stricter security policies
    Assume.assumeTrue(getVersion() < 17 );
  }

  private static int getVersion() {
      String version = System.getProperty("java.version");
      if(version.startsWith("1.")) {
          version = version.substring(2, 3);
      } else {
          int dot = version.indexOf(".");
          if(dot != -1) { version = version.substring(0, dot); }
      } return Integer.parseInt(version);
  }

  @Test
  public void testGetOnEmpty() throws Exception {
    runOnOtherThread(new Runnable() {
      @Override
      public void run() {
        final Context expected = EmptyContext.getInstance();
        final Context actual = ContextHolder.current();
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
        final Context expected = EmptyContext.getInstance().withValue(key, value);
        ContextHolder.attach(expected);
        final Context actual = ContextHolder.current();
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
        final Context context = EmptyContext.getInstance().withValue(key, value);
        ContextHolder.attach(context);

        runOnOtherThread(new Runnable() {
          @Override
          public void run() {
            // then
            final Context expected = EmptyContext.getInstance();
            final Context actual = ContextHolder.current();
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
