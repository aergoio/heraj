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
    return new TestResult(result, null);
  }

  public static TestResult fail(final LuaErrorInformation error) {
    return new TestResult(null, error);
  }

  @Getter
  protected final Object result;

  @Getter
  protected final LuaErrorInformation error;

  public boolean isSuccess() {
    return null == error;
  }

}
