/*
 * @copyright defined in LICENSE.txt
 */

package hera.build.web.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import hera.test.TestFile;
import java.util.Collection;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@RequiredArgsConstructor
@ToString(callSuper = true)
public class BuildDetails extends BuildSummary {

  @Getter
  @Setter
  protected int sequence;

  @Getter
  @Setter
  protected String result;

  @Getter
  @Setter
  protected BuildDependency dependencies;

  @Getter
  @Setter
  protected Collection<TestFile> unitTestReport;

  /**
   * Build summary from this build details.
   *
   * @return build summary
   */
  @JsonIgnore
  public BuildSummary getSummary() {
    final BuildSummary summary = new BuildSummary();
    summary.setUuid(getUuid());
    summary.setElapsedTime(getElapsedTime());
    summary.setState(getState());
    summary.setError(getError());

    return summary;
  }

  /**
   * Copy from {@code source}.
   *
   * @param source {@link BuildDetails} to copy from
   */
  public void copyFrom(final BuildDetails source) {
    this.state = source.getState();
    this.elapsedTime = source.getElapsedTime();
    this.error = source.getError();
    this.result = source.getResult();
    this.dependencies = source.getDependencies();
    this.unitTestReport = source.getUnitTestReport();
  }
}
