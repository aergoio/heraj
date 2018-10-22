/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static java.util.Optional.ofNullable;

import hera.util.StringUtils;
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
    return "Node status:\n" + ofNullable(moduleStatus)
        .flatMap(m -> m.stream().map(Object::toString).reduce((a, b) -> a + "\n" + b))
        .orElse(StringUtils.NULL_STRING);
  }

}
