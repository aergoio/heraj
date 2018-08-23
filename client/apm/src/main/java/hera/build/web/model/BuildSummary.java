/*
 * @copyright defined in LICENSE.txt
 */

package hera.build.web.model;

import hera.BuildResult;
import lombok.Getter;
import lombok.Setter;

public class BuildSummary {
  @Getter
  @Setter
  protected String uuid;

  @Getter
  @Setter
  protected long timestamp;

  @Getter
  @Setter
  protected boolean success;

  public BuildSummary() {
  }

  /**
   * Constructor with {@link BuildResult}.
   *
   * @param buildResult build result
   */
  public BuildSummary(final BuildResult buildResult) {
    this.uuid = buildResult.getUuid();
    this.timestamp = buildResult.getTimestamp();
    this.success = buildResult.isSuccess();
  }
}
