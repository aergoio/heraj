/*
 * @copyright defined in LICENSE.txt
 */

package hera.example;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@ToString
public class Data {
  @Getter
  @Setter
  protected int val1;

  @Getter
  @Setter
  protected String val2;
}
