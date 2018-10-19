/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import static java.util.UUID.randomUUID;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import org.junit.Test;

public class ExceptionUtilsTest {

  @Test
  public void testBuildExceptionMessage() {
    assertNull(ExceptionUtils.buildExceptionMessage(null, null));

    for (int i = 0; i < 100; ++i) {
      assertNotNull(ExceptionUtils.buildExceptionMessage(randomUUID().toString(), new Throwable()));
    }
  }

  @Test
  public void testTrace() {
    assertNotNull(ExceptionUtils.trace());
  }

  @Test
  public void testTraceOnStackTraceElement() {
    final Thread th = Thread.currentThread();
    final StackTraceElement[] elements = th.getStackTrace();

    assertNull(ExceptionUtils.trace(null));

    assertNotNull(ExceptionUtils.trace(elements));
  }

  @Test
  public void testTraceOnStackTraceElementWithStartAndEnd() {
    final Thread th = Thread.currentThread();
    final StackTraceElement[] elements = th.getStackTrace();
    final int start = 0;
    final int end = elements.length;

    assertNull(ExceptionUtils.trace(null, start, end));

    try {
      ExceptionUtils.trace(elements, start - 1, end);
    } catch (Exception e) {
      assertSame(ArrayIndexOutOfBoundsException.class, e.getClass());
    }

    try {
      ExceptionUtils.trace(elements, start, end + 1);
    } catch (Exception e) {
      assertSame(ArrayIndexOutOfBoundsException.class, e.getClass());
    }

    try {
      ExceptionUtils.trace(elements, start - 1, end + 1);
    } catch (Exception e) {
      assertSame(ArrayIndexOutOfBoundsException.class, e.getClass());
    }

    assertNotNull(ExceptionUtils.trace(elements, start, end));
  }

  @Test
  public void testGetStackTraceOf() {
    final Throwable e = new Throwable();

    assertNull(ExceptionUtils.getStackTraceOf(null));

    assertNotNull(ExceptionUtils.getStackTraceOf(e));
  }

  @Test
  public void testConcat() {
    final Throwable left = new Throwable();
    final Throwable right = new Throwable();

    final StackTraceElement[] leftTrace = left.getStackTrace();
    final StackTraceElement[] rightTrace = right.getStackTrace();
    final StackTraceElement[] expected =
        Arrays.copyOf(leftTrace, leftTrace.length + rightTrace.length);
    System.arraycopy(rightTrace, 0, expected, leftTrace.length, rightTrace.length);

    assertTrue(Arrays.equals(expected, ExceptionUtils.concat(left, right)));
  }

}
