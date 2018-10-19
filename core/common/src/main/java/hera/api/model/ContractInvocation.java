/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@ToString
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
public class ContractInvocation {

  public static ContractInvocation of(final ContractAddress address,
      final ContractFunction function) {
    return new ContractInvocation(address, function);
  }

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

  public ContractInvocation(final ContractAddress address, final ContractFunction function) {
    this(address, function, new Object[0]);
  }

}
