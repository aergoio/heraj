/*
 * @copyright defined in LICENSE.txt
 */

package hera.test;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@RequiredArgsConstructor
@ToString
public class TestResult {
  @Getter
  @Setter
  protected Object result;

  @Getter
  @Setter
  protected String output;

  @Getter
  @Setter
  protected LuaErrorInformation error;

  @Getter
  @Setter
  protected String codeSnippet;

  public boolean isSuccess() {
    return null == error;
  }

}
