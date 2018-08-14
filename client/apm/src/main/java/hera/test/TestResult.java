/*
 * @copyright defined in LICENSE.txt
 */

package hera.test;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class TestResult {
  public static TestResult success(final Object result) {
    return new TestResult(result, null, 0, 0);
  }

  public static TestResult fail(final String errorMessage, final int line, final int column) {
    return new TestResult(null, errorMessage, line, column);
  }

  @Getter
  protected final Object result;

  @Getter
  protected final String errorMessage;

  @Getter
  protected final int lineNumber;

  @Getter
  protected final int columnNumber;
}
