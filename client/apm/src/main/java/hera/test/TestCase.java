/*
 * @copyright defined in LICENSE.txt
 */

package hera.test;

import static java.lang.System.currentTimeMillis;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class TestCase {

  @Getter
  protected final String name;

  @Getter
  @Setter
  protected long startTime = currentTimeMillis();

  @Getter
  @Setter
  protected long endTime;

  @Getter
  @Setter
  protected boolean success;

  @Getter
  @Setter
  protected String errorMessage;

  @Override
  public String toString() {
    return getName() + " --> " + (isSuccess() ? "success" : "failure");
  }
}
