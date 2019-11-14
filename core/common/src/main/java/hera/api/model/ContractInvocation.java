/*
 * @copyright defined in LICENSE.txt
 */

package hera.api.model;

import static hera.util.ValidationUtils.assertNotNull;
import static java.util.Collections.unmodifiableList;

import hera.annotation.ApiAudience;
import hera.annotation.ApiStability;
import java.util.List;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@ApiAudience.Public
@ApiStability.Unstable
@ToString
@EqualsAndHashCode
@Builder(builderMethodName = "newBuilder")
public class ContractInvocation {

  @Getter
  protected final ContractAddress address;

  @Getter
  protected final ContractFunction function;

  @Getter
  protected final List<Object> args;

  @Getter
  protected final Aer amount;

  @Getter
  protected final boolean delegateFee;

  ContractInvocation(final ContractAddress contractAddress, final ContractFunction contractFunction,
      final List<Object> args, final Aer amount, final boolean delegateFee) {
    assertNotNull(contractAddress, "Contract address must not null");
    assertNotNull(contractFunction, "Contract function must not null");
    assertNotNull(args, "Contract function args must not null");
    assertNotNull(amount, "Amount must not null");
    this.address = contractAddress;
    this.function = contractFunction;
    this.args = unmodifiableList(args);
    this.amount = amount;
    this.delegateFee = delegateFee;
  }

}
