/*
 * @copyright defined in LICENSE.txt
 */

package hera.build.web.model;

import static java.util.UUID.randomUUID;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class BuildSummary {
  public static final int SUCCESS = 0;
  public static final int BUILD_FAIL = 1;
  public static final int TEST_FAIL = 2;

  @Getter
  @Setter
  protected String uuid = randomUUID().toString();

  @Getter
  @Setter
  protected long timestamp = System.currentTimeMillis();

  @Getter
  @Setter
  protected int state = SUCCESS;

  @Getter
  @Setter
  protected String error;

  public BuildSummary() {
  }
}
