/*
 * @copyright defined in LICENSE.txt
 */

package hera.build.web.model;

import static java.util.UUID.randomUUID;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@NoArgsConstructor
public class BuildSummary {
  public static final int SUCCESS = 0;
  public static final int BUILD_FAIL = 1;
  public static final int TEST_FAIL = 2;

  @Getter
  @Setter
  protected String uuid = randomUUID().toString();

  @Getter
  @Setter
  protected long elapsedTime;

  @Getter
  @Setter
  protected int state = SUCCESS;

  @Getter
  @Setter
  protected String error;
}
