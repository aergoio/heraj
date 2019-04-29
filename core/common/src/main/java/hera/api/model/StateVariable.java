/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.ValidationUtils.assertNotNull;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ApiAudience.Public
@ApiStability.Unstable
@ToString
@EqualsAndHashCode
public class StateVariable {

  @Getter
  protected String name;

  @Getter
  protected String type;

  /**
   * StateVariable constructor.
   *
   * @param name a state variable name
   * @param type a state variable type
   */
  @ApiAudience.Private
  public StateVariable(final String name, final String type) {
    assertNotNull(name, "Name must not null");
    assertNotNull(type, "Type must not null");
    this.name = name;
    this.type = type;
  }

}
