/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.ValidationUtils.assertNotNull;
import static java.util.Collections.unmodifiableList;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import hera.exception.HerajException;
import java.util.Collections;
import java.util.List;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ApiAudience.Public
@ApiStability.Unstable
@ToString
@EqualsAndHashCode
public class ContractFunction {

  @Getter
  protected final String name;

  @Getter
  protected final List<String> argumentNames;

  /**
   * ContractFunction constructor.
   *
   * @param name a function name
   */
  @ApiAudience.Private
  public ContractFunction(final String name) {
    this(name, Collections.emptyList());
  }

  /**
   * ContractFunction constructor.
   *
   * @param name a function name
   * @param argumentNames an argument names
   */
  @ApiAudience.Private
  public ContractFunction(final String name, final List<String> argumentNames) {
    assertNotNull(name, new HerajException("Function name must not null"));
    assertNotNull(argumentNames, new HerajException("Argument names must not null"));
    this.name = name;
    this.argumentNames = unmodifiableList(argumentNames);
  }

}
