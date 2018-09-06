/*
 * @copyright defined in LICENSE.txt
 */

package hera.build.web.model;

import lombok.Getter;
import lombok.Setter;

public class ContractInput {

  @Getter
  @Setter
  protected String name;

  @Getter
  @Setter
  protected String[] arguments;
}
