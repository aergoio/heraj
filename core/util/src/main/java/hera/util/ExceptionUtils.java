/*
 * @copyright defined in LICENSE.txt
 */

package hera.util;

import static hera.util.ValidationUtils.assertTrue;

import java.io.PrintWriter;
import java.io.StringWriter;

public class ExceptionUtils {

  /**
   * Assemble exception message from {@code message} and {@code cause}.
   *
   * @param message message
   * @param cause   exception
   * @return message to be made
   */
  public static String buildExceptionMessage(
      final String message,
      final Throwable cause) {
    if (null == cause) {
      return message;
    }
    final StringBuilder sb = new StringBuilder();
    if (null != message) {
      sb.append(message).append("; ");
    }

    sb.append("nested exception is ").append(cause);
    return sb.toString();
  }

  /**
   * Print the current thread's stacktrace.
   * <p>
   * Print stacktrace the upper part from {@link ExceptionUtils#trace()}.
   * </p>
   *
   * @return StackTraceElement's information
   *
   * @see #trace(StackTraceElement[], int, int)
   */
  public static String trace() {
    final Thread th = Thread.currentThread();
    final StackTraceElement[] elements = th.getStackTrace();
    return trace(elements, 2, elements.length);
  }

  /**
   * Print the stacktrace.
   * <p>
   * return {@code null} if stacktrace is {@code null}
   * </P>
   *
   * @param elements array of {@link StackTraceElement}
   *
   * @return stacktrace string
   *
   * @see #trace(StackTraceElement[], int, int)
   */
  public static String trace(
      final StackTraceElement[] elements) {
    if (null == elements) {
      return null;
    }
    return trace(elements, 0, elements.length);
  }

  /**
   * Print the partial stacktrace.
   * <p>
   * {@code start} is less than {@code end} and both are in a range of {@code elements}.
   * return {@code null} if stacktrace is {@code null}
   * </p>
   *
   * @param elements array of {@link StackTraceElement}
   * @param start    start index
   * @param end      end index
   * @return stacktrace string
   */
  public static String trace(
      final StackTraceElement[] elements,
      final int start,
      final int end) {
    // Check input
    if (null == elements) {
      return null;
    }

    if (start < 0) {
      throw new ArrayIndexOutOfBoundsException();
    }
    if (elements.length < end) {
      throw new ArrayIndexOutOfBoundsException();
    }
    assertTrue(start < end);

    // Build the stack trace information.
    final StringBuilder buffer = new StringBuilder();
    for (int i = start; i < end; ++i) {
      buffer.append(elements[i].toString());
      buffer.append("\n");
    }
    return buffer.toString();
  }

  /**
   * Build printable string from {@code e}.
   * <p>
   * return {@code null} if {@code e} is {@code null}
   * </p>
   *
   * @param e {@link Throwable} to print
   * @return information about <code>e</code>
   */
  public static String getStackTraceOf(
      final Throwable e) {
    if (null == e) {
      return null;
    }
    final StringWriter writer = new StringWriter();
    final PrintWriter w = new PrintWriter(writer);
    e.printStackTrace(w);
    return writer.toString();
  }
}
