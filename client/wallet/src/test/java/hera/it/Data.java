/*
 * @copyright defined in LICENSE.txt
 */

package hera.it;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class Data {

  @Getter
  @Setter
  protected int intVal;

  @Getter
  @Setter
  protected String stringVal;

}
