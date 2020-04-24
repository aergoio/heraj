/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.ValidationUtils.assertNotNull;
import static java.util.Collections.unmodifiableList;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
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

  static final ContractFunction EMPTY = new ContractFunction("", Collections.<String>emptyList(),
      false, false, false);

  @Getter
  protected final String name;

  @Getter
  protected final List<String> argumentNames;

  @Getter
  protected final boolean payable;

  @Getter
  protected final boolean view;

  @Getter
  protected final boolean feeDelegation;

  /**
   * ContractFunction constructor.
   *
   * @param name a function name
   */
  @Deprecated
  public ContractFunction(final String name) {
    this(name, Collections.<String>emptyList());
  }

  /**
   * ContractFunction constructor.
   *
   * @param name          a function name
   * @param argumentNames an argument names
   */
  @Deprecated
  public ContractFunction(final String name, final List<String> argumentNames) {
    this(name, argumentNames, false, false, false);
  }

  /**
   * ContractFunction constructor.
   *
   * @param name          a function name
   * @param payable       whether a function is payable or not
   * @param view          whether a function is view or not
   * @param feeDelegation whether a function can delegate fee or not
   */
  @Deprecated
  public ContractFunction(final String name, final boolean payable, final boolean view,
      final boolean feeDelegation) {
    this(name, Collections.<String>emptyList(), payable, view, feeDelegation);
  }

  /**
   * ContractFunction constructor.
   *
   * @param name          a function name
   * @param argumentNames argument names
   * @param payable       whether a function is payable or not
   * @param view          whether a function is view or not
   * @param feeDelegation whether a function can delegate fee or not
   */
  @ApiAudience.Private
  @ApiStability.Unstable
  public ContractFunction(final String name, final List<String> argumentNames,
      final boolean payable, final boolean view, final boolean feeDelegation) {
    assertNotNull(name, "Function name must not null");
    assertNotNull(argumentNames, "Argument names must not null");
    this.name = name;
    this.argumentNames = unmodifiableList(argumentNames);
    this.payable = payable;
    this.view = view;
    this.feeDelegation = feeDelegation;
  }


}
