/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

public class NodeStatus {

  @Getter
  @Setter
  protected List<ModuleStatus> moduleStatus;

}
