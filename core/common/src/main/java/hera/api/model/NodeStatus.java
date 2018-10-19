/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@EqualsAndHashCode
public class NodeStatus {

  @Getter
  @Setter
  protected List<ModuleStatus> moduleStatus;

  @Override
  public String toString() {
    return "Node status:\n"
        + moduleStatus.stream().map(Object::toString).reduce((a, b) -> a + "\n" + b).get();
  }

}
