/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.ValidationUtils.assertNotNull;
import static java.util.Collections.unmodifiableList;

import hera.exception.HerajException;
import hera.util.StringUtils;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;

@EqualsAndHashCode
public class NodeStatus {

  @Getter
  protected final List<ModuleStatus> moduleStatus;

  /**
   * NodeStatus constructor.
   *
   * @param moduleStatus a module status list
   */
  public NodeStatus(final List<ModuleStatus> moduleStatus) {
    assertNotNull(moduleStatus, new HerajException("Module status list must not null"));
    this.moduleStatus = unmodifiableList(moduleStatus);
  }

  @Override
  public String toString() {
    return String.format("Node status:\n%s", StringUtils.join(moduleStatus, "\n"));
  }

}
