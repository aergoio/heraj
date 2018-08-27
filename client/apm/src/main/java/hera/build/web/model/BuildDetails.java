/*
 * @copyright defined in LICENSE.txt
 */

package hera.build.web.model;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@RequiredArgsConstructor
public class BuildDetails extends BuildSummary {
  @Getter
  @Setter
  protected String result;

  @Getter
  @Setter
  protected BuildDependency dependencies;

  /**
   * Build summary from this build details.
   *
   * @return build summary
   */
  public BuildSummary getSummary() {
    final BuildSummary summary = new BuildSummary();
    summary.setUuid(getUuid());
    summary.setTimestamp(getTimestamp());
    summary.setSuccess(isSuccess());

    return summary;
  }
}
