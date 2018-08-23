/*
 * @copyright defined in LICENSE.txt
 */

package hera.build.web.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import hera.BuildResult;
import hera.util.IoUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import lombok.Getter;
import lombok.Setter;

public class BuildDetails extends BuildSummary {
  @Getter
  @Setter
  protected String contents;

  /**
   * Constructor with {@link BuildResult}.
   *
   * @param buildResult build result
   */
  public BuildDetails(final BuildResult buildResult) {
    super(buildResult);
    buildResult.getFileSet().stream().findFirst().ifPresent(fileContent -> {
      try (final InputStream in = fileContent.open()) {
        contents = IoUtils.from(new InputStreamReader(in));
      } catch (final IOException e) {
        throw new IllegalStateException(e);
      }
    });
  }

  /**
   * Get summary for this.
   *
   * @return BuildSummary
   */
  @JsonIgnore
  public BuildSummary getSummary() {
    final BuildSummary summary = new BuildSummary();
    summary.setUuid(this.getUuid());
    summary.setTimestamp(this.getTimestamp());
    summary.setSuccess(this.isSuccess());
    return summary;
  }
}
