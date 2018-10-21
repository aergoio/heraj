/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@NoArgsConstructor
public class ContractInvocation {

  /**
   * Build contract invocation.
   *
   * @param address contract address
   * @param function invocation function
   * @param args arguments of invocation function
   */
  public static ContractInvocation of(final ContractAddress address,
      final ContractFunction function, final Object... args) {
    return new ContractInvocation(address, function, args);
  }

  @Setter
  @Getter
  protected ContractAddress address;

  @Setter
  @Getter
  protected ContractFunction function;

  @Setter
  @Getter
  protected Object[] args = new Object[0];

  /**
   * Contract invocation constructor.
   *
   * @param address contract address
   * @param function invocation function
   * @param args arguments of invocation function
   */
  public ContractInvocation(final ContractAddress address, final ContractFunction function,
      final Object... args) {
    this.address = address;
    this.function = function;
    this.args = args;
  }

}
