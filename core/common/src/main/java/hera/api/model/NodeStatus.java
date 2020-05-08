/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.ValidationUtils.assertNotNull;
import static java.util.Collections.emptyList;
import static java.util.Collections.unmodifiableList;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.util.StringUtils;
import java.util.Collections;
import java.util.List;
import lombok.Builder;
import lombok.Builder.Default;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NonNull;
import lombok.ToString;
import lombok.Value;

@ApiAudience.Public
@ApiStability.Unstable
@Getter
@EqualsAndHashCode
@Builder(builderMethodName = "newBuilder")
public class NodeStatus {

  @NonNull
  @Default
  protected final List<ModuleStatus> moduleStatus = unmodifiableList(
      Collections.<ModuleStatus>emptyList());

  NodeStatus(final List<ModuleStatus> moduleStatus) {
    assertNotNull(moduleStatus, "Module status list must not null");
    this.moduleStatus = unmodifiableList(moduleStatus);
  }

  @Override
  public String toString() {
    return String.format("Node status={\n%s\n}", StringUtils.join(moduleStatus, "  \n"));
  }

}
