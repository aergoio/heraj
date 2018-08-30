/*
 * @copyright defined in LICENSE.txt
 */

package hera.test;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@ToString
public class TestResult {
  public static TestResult success(final Object result) {
    return new TestResult(result, null, null);
  }

  public static TestResult fail(final LuaErrorInformation error, final String codeSnippet) {
    return new TestResult(null, error, codeSnippet);
  }

  @Getter
  protected final Object result;

  @Getter
  protected final LuaErrorInformation error;

  @Getter
  protected final String codeSnippet;

  public boolean isSuccess() {
    return null == error;
  }

}
