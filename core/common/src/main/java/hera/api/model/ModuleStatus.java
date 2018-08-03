/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import hera.util.Pair;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

public class ModuleStatus {

  @Getter
  @Setter
  protected String moduleName;

  @Getter
  @Setter
  protected List<Pair<String, Double>> internalStatus;

}
